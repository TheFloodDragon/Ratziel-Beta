package cn.fd.ratziel.module.item

import cn.altawk.nbt.NbtFormat
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.common.element.registry.ElementConfig
import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementHandler
import cn.fd.ratziel.core.serialization.json.baseJson
import cn.fd.ratziel.module.item.api.ItemMaterial
import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.impl.builder.DefaultGenerator
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.module.item.impl.component.serializers.*
import cn.fd.ratziel.module.item.internal.NbtNameDeterminer
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.nbt.NBTSerializer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.serializer
import org.bukkit.enchantments.Enchantment
import taboolib.common.LifeCycle
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XItemFlag
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * ItemElement
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:55
 */
@NewElement(
    name = "meta",
    space = "test"
)
@ElementConfig(
    lifeCycle = LifeCycle.ENABLE
)
object ItemElement : ElementHandler {

    /**
     * 构建物品用到的线程池
     */
    val executor: ExecutorService by lazy {
        Executors.newFixedThreadPool(8)
    }

    /**
     * 协程作用域
     */
    val scope = CoroutineScope(CoroutineName("ItemHandler") + executor.asCoroutineDispatcher())

    /**
     * [Json]
     */
    val json = Json(baseJson) {
        serializersModule += SerializersModule {
            // Common Serializers
            contextual(NbtTag::class, NBTSerializer)
            contextual(ItemMaterial::class, ItemMaterialSerializer)
            // Bukkit Serializers
            contextual(Enchantment::class, EnchantmentSerializer)
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
        register<ItemSkull>(processor = ItemSkull.Processor)
        register<ItemHideFlag>()
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

    @SubscribeEvent
    fun onLoadStart(event: WorkspaceLoadEvent.Start) {
        // 清除注册的物品
        ItemManager.registry.clear()
    }

    private inline fun <reified T : Any> register(serializer: KSerializer<T> = serializer<T>(), processor: DataProcessor = DataProcessor.NoProcess) {
        ItemRegistry.register(T::class.java, serializer, processor)
    }

}