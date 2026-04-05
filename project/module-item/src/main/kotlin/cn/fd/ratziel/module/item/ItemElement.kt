package cn.fd.ratziel.module.item

import cn.altawk.nbt.NbtFormat
import cn.fd.ratziel.common.command.CommandMain
import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.serialization.json.baseJson
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.feature.action.ActionInterpreter
import cn.fd.ratziel.module.item.feature.layer.PhysicalLayerInterpreter
import cn.fd.ratziel.module.item.feature.template.InheritInterpreter
import cn.fd.ratziel.module.item.feature.template.TemplateElement
import cn.fd.ratziel.module.item.impl.builder.DefaultGenerator
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import cn.fd.ratziel.module.item.impl.builder.provided.*
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.test.ComponentCompatibilityTest
import cn.fd.ratziel.module.item.internal.NbtNameDeterminer
import cn.fd.ratziel.module.item.internal.RefItemStack
import cn.fd.ratziel.module.item.internal.command.ItemCommand
import cn.fd.ratziel.module.item.internal.serializers.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.serializer
import net.kyori.adventure.text.Component
import taboolib.common.LifeCycle
import taboolib.common.io.isDebugMode
import taboolib.common.platform.Awake
import taboolib.common.platform.function.debug
import taboolib.library.xseries.XItemFlag
import taboolib.module.nms.MinecraftVersion

/**
 * ItemElement
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:55
 */
@NewElement("item")
@ElementConfig(
    lifeCycle = LifeCycle.ENABLE,
    requires = [TemplateElement::class]
)
object ItemElement : ElementHandler.ParralHandler {

    /**
     * 协程上下文
     */
    val coroutineContext by lazy {
        CoroutineName("ItemElement") + Dispatchers.Default
    }

    /**
     * 协程作用域
     */
    val scope get() = CoroutineScope(coroutineContext)

    /**
     * [Json]
     */
    val json = Json(baseJson) {
        serializersModule += SerializersModule {
            // Common Serializers
            contextual(ItemMaterial::class, ItemMaterialSerializer)
            // Bukkit Serializers
            contextual(XItemFlag::class, HideFlagSerializer)
            contextual(Attribute::class, AttributeSerializer)
            contextual(AttributeModifier::class, AttributeModifierSerializer)
        }
    }

    /**
     * [NbtFormat]
     */
    val nbt = NbtFormat {
        nameDeterminer = NbtNameDeterminer
    }

    init {
        // 注册默认组件
        register(if (MinecraftVersion.versionId >= 12005) ItemDisplay.serializer() else LegacyItemDisplaySerializer)
        register<ItemDurability>()
        register<ItemHideFlag>()
        register<ItemEnchant>()
    }

    init {
        // 物品解释器注册
        ItemRegistry.registerInterpreter { ActionInterpreter }
        ItemRegistry.registerInterpreter { InheritInterpreter }
        ItemRegistry.registerInterpreter { DefaultResolver }
        ItemRegistry.registerInterpreter { DataInterpreter() }
        ItemRegistry.registerInterpreter { PhysicalLayerInterpreter }
        ItemRegistry.registerInterpreter { ComponentInterpreter() }
    }

    init {
        // 注册默认的部分解析器
        ItemRegistry.registerSectionResolver(InlineScriptResolver)
        ItemRegistry.registerSectionResolver(TaggedSectionResolver(ItemRegistry.staticTagResolvers))
        ItemRegistry.registerSectionResolver(PapiResolver)
        ItemRegistry.registerSectionResolver(EnhancedListResolver)
        // 非受默认解析器管控的 *静态* 标签的标签解析器
        // 暂时没有
        // 非受默认解析器管控的 *动态* 标签的标签解析器
        ItemRegistry.registerDynamicTagResolver(DataInterpreter.NativeDataResolver)
        ItemRegistry.registerDynamicTagResolver(DataInterpreter.ComputationResolver)
    }

    override fun handle(element: Element) {
        // 创建原生物品生成器
        val generator = DefaultGenerator(element)
        // 加到注册表里
        ItemManager.registry[element.name] = generator
        // 准备物品
        generator.compositor.prepare()

        // Debug
        if (isDebugMode) {

            val item = generator.build().get()
            println(item.data)
            val holder = item as? cn.fd.ratziel.module.item.api.component.ItemComponentHolder
                ?: error("Generated item must implement ItemComponentHolder for DSL test.")

            val ri = RefItemStack.of(item.data)
            println(ri.tag)
            val bi = ri.bukkitStack
            println(bi)

            val dsl = holder.dsl()
            fun assertDebugEquals(name: String, expected: Any?, actual: Any?) {
                check(actual == expected) {
                    "ComponentHolderDSL $name test failed. expected=$expected, actual=$actual"
                }
            }

            val originDisplayName = dsl.displayName
            val originItemName = dsl.itemName
            val originLore = dsl.lore
            val originMaxDamage = dsl.maxDamage

            val debugDisplayName = Component.text("dsl-display-name")
            val debugItemName = Component.text("dsl-item-name")
            val debugLore = listOf(Component.text("dsl-lore-1"), Component.text("dsl-lore-2"))

            dsl.displayName = debugDisplayName
            dsl.itemName = debugItemName
            dsl.lore = debugLore

            assertDebugEquals("displayName", debugDisplayName, dsl.displayName)
            assertDebugEquals("itemName", debugItemName, dsl.itemName)
            assertDebugEquals("lore", debugLore, dsl.lore)

            if (MinecraftVersion.versionId >= 12005) {
                dsl.maxDamage = 114514
                assertDebugEquals("maxDamage", 114514, dsl.maxDamage)
            }

            dsl.displayName = originDisplayName
            dsl.itemName = originItemName
            if (originLore.isEmpty()) dsl.remove(ItemComponents.LORE) else dsl.lore = originLore
            dsl.maxDamage = originMaxDamage

            debug(ItemComponents.registry.map {
                it
            })

        }
    }

    override fun onStart(elements: Collection<Element>) {
        // 清除注册的物品
        ItemManager.registry.clear()
        // 主动触发组件一致性检查
        ComponentCompatibilityTest.runAtEnable()
    }

    private inline fun <reified T : Any> register(serializer: KSerializer<T> = serializer<T>()) {
        ItemRegistry.registerComponent(T::class.java, serializer)
    }

    @Awake(LifeCycle.ACTIVE)
    private fun registerSubCommand() {
        // 子命令注册
        CommandMain.registerSubCommand(ItemCommand::class.java, "item")
    }

}
