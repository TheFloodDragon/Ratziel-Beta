package cn.fd.ratziel.module.item

import cn.altawk.nbt.NbtFormat
import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.serialization.json.baseJson
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.impl.action.ActionInterpreter
import cn.fd.ratziel.module.item.impl.builder.DefaultGenerator
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import cn.fd.ratziel.module.item.impl.builder.NativeSource
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import cn.fd.ratziel.module.item.impl.builder.provided.*
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.module.item.impl.feature.layer.PhysicalLayerInterpreter
import cn.fd.ratziel.module.item.impl.feature.template.TemplateElement
import cn.fd.ratziel.module.item.internal.NbtNameDeterminer
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.internal.serializers.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.serializer
import taboolib.common.LifeCycle
import taboolib.common.io.isDebugMode
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
object ItemElement : ElementHandler {

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
        ItemRegistry.registerInterpreter(ActionInterpreter)
        ItemRegistry.registerInterpreter(DefaultResolver)
        ItemRegistry.registerInterpreter { DataInterpreter() }
        ItemRegistry.registerInterpreter(PhysicalLayerInterpreter)
    }

    init {
        // 物品源注册
        ItemRegistry.registerSource(NativeSource.MaterialSource)
        ItemRegistry.registerSource(SkullSource)
        ItemRegistry.registerSource(NbtTagSource)
    }

    init {
        // 注册默认的解析器
        ItemRegistry.registerSectionResolver(InheritResolver)
        ItemRegistry.registerSectionResolver(InlineScriptResolver)
        ItemRegistry.registerSectionResolver(TaggedSectionResolver(ItemRegistry.staticTagResolvers))
        ItemRegistry.registerSectionResolver(PapiResolver)
        ItemRegistry.registerSectionResolver(EnhancedListResolver)
        // 只支持 *静态* 标签的标签解析器
        ItemRegistry.registerStaticTagResolver(InheritResolver)
        // 只支持 *动态* 标签的标签解析器
        ItemRegistry.registerDynamicTagResolver(DataInterpreter.NativeDataResolver)
        ItemRegistry.registerDynamicTagResolver(DataInterpreter.ComputationResolver)
    }

    override fun handle(element: Element) {
        val generator = DefaultGenerator(element)

        if (isDebugMode) {

            val item = generator.build().get()
            println(item.data)

            val ri = RefItemStack.of(item.data)
            println(ri.tag)
            val bi = ri.bukkitStack
            println(bi)

        }

        // 注册
        ItemManager.registry[element.name] = generator
    }

    override fun onStart(elements: Collection<Element>) {
        // 清除注册的物品
        ItemManager.registry.clear()
    }

    private inline fun <reified T : Any> register(serializer: KSerializer<T> = serializer<T>()) {
        ItemRegistry.registerComponent(T::class.java, serializer)
    }

}
