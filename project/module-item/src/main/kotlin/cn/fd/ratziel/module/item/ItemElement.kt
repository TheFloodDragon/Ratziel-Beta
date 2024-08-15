package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.module.item.impl.builder.DefaultItemGenerator
import cn.fd.ratziel.module.item.nms.RefItemStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
    val buildScope = CoroutineScope(Dispatchers.Default)

    init {
        // 注册默认的东西
        ItemRegistry.registerDefault()
    }

    override fun handle(element: Element) {

        val generator = DefaultItemGenerator(element)

        val item = generator.build().get()

        println(item.data)

        val ri = RefItemStack(item.data)
        println(ri.getTag())
        val bi = ri.getAsBukkit()
        println(bi)

        // 注册
        ItemManager.registry[element.name] = generator
    }

    @SubscribeEvent
    fun onLoadStart(event: WorkspaceLoadEvent.Start) {
        // 清除协程任务
        buildScope.cancel()
        // 清除注册的物品
        ItemManager.registry.clear()
    }

}