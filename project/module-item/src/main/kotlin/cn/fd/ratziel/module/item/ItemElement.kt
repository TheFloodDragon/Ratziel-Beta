package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.module.item.impl.builder.DefaultGenerator
import cn.fd.ratziel.module.item.impl.builder.DefaultSerializer
import cn.fd.ratziel.module.item.impl.component.*
import cn.fd.ratziel.module.item.nms.RefItemStack
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import taboolib.common.platform.event.SubscribeEvent
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

    init {
        // 注册默认序列化器
        ItemRegistry.register(DefaultSerializer)
        // 注册默认转换器
        ItemRegistry.register(ItemDisplay::class.java, ItemDisplay)
        ItemRegistry.register(ItemDurability::class.java, ItemDurability)
        ItemRegistry.register(ItemSundry::class.java, ItemSundry)
        ItemRegistry.register(ItemMetadata::class.java, ItemMetadata)
        ItemRegistry.register(ItemCharacteristic::class.java, ItemCharacteristic)
        // 注册自定义物品解析器 (默认解析器无需注册, 保持在最后直接使用)
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

}