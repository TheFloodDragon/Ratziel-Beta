package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.contextual.ArgumentContextProvider
import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.SimpleData
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture

/**
 * DefaultGenerator
 *
 * @author TheFloodDragon
 * @since 2025/3/22 15:32
 */
class DefaultGenerator(
    /**
     * 原始物品配置 (元素)
     */
    val origin: Element,
) : ItemGenerator {

    /**
     * 附加的上下文
     */
    val attached = AttachedContext.newContext()

    /**
     * 上下文提供者 (目前就是为了提供 [AttachedContext])
     */
    override val contextProvider = ArgumentContextProvider { SimpleContext(attached) }

    /**
     * 解释器编排器
     */
    override val compositor = DefaultCompositor(createNativeStream(SimpleData(), contextProvider.newContext()))

    override fun build() = build(contextProvider.newContext())

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(context)

    /**
     * 异步生成物品
     */
    fun buildAsync(context: ArgumentContext) = ItemElement.scope.async {
        // 获取物品流
        val stream = compositor.produce().await() as NativeItemStream
        // 更新上下文
        stream.context = contextProvider.newContext()
        stream.context.putAll(context.args())

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(stream.identifier, this@DefaultGenerator, context, origin.property).call()

        // 非纯静态物品处理物品流
        if (!compositor.staticStrategy.fullStaticMode) {
            compositor.dispatch(stream)
        }

        // 呼出生成结束的事件
        val event = ItemGenerateEvent.Post(stream.identifier, this@DefaultGenerator, context, stream.item)
        event.call()
        event.item // 返回最终结果
    }.asCompletableFuture()

    /**
     * 创建原生物品流
     */
    fun createNativeStream(sourceData: ItemData, context: ArgumentContext): NativeItemStream {
        // 生成基本物品 (本地源物品)
        val item = NativeSource.generateItem(origin, sourceData)
            ?: throw IllegalStateException("Failed to generate item source!")
        // 创建物品流并返回
        return NativeItemStream(origin, item, context)
    }

}