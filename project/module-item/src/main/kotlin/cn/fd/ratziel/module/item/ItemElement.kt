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
import cn.fd.ratziel.module.item.impl.builder.provided.*
import cn.fd.ratziel.module.item.impl.component.*
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


    /*
     * 尽管解释器 (Resolver, Interpreter, Source) 在解释的过程中是并行的,
     * 但是启动协程的顺序就近乎决定了解释器获取同步锁的顺序,
     * 比如解析器 (Resolver) 的执行是一开始就尝试拿锁的, 故而但看解析器执行链, 会发现它们其实是串行的.
     * 其他的同理也不是完全的并行或者串行, 这种方式虽说有可能会带来错位解释 (不按照启动的顺序),
     * 但相比来说, 却极为简洁, 在大多情况下无异常.
     */

    init {
        // 注册默认解释器
        ItemRegistry.registerInterpreter(InheritResolver)
        ItemRegistry.registerSource(SkullSource)
        ItemRegistry.registerSource(NbtTagSource)
        ItemRegistry.registerSource(NativeSource.MaterialSource)
        ItemRegistry.registerInterpreter(ActionInterpreter)
        ItemRegistry.registerInterpreter(DefinitionInterpreter)
        ItemRegistry.registerInterpreter(NativeDataInterpreter)
        ItemRegistry.registerInterpreter(DefaultResolver)
    }

    override fun handle(element: Element) {
        val generator = DefaultGenerator(element)

        val item = generator.build().get()

        println(item.data)

        val ri = RefItemStack.of(item.data)
        println(ri.tag)
        val bi = ri.bukkitStack
        println(bi)

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