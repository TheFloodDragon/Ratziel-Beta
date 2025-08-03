package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.ArgumentContextProvider
import cn.fd.ratziel.core.functional.CacheContext
import cn.fd.ratziel.core.functional.SimpleContext
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
open class DefaultGenerator(
    /**
     * 原始物品配置 (元素)
     */
    val origin: Element,
) : ItemGenerator {

    /**
     * 针对同元素物品的缓存上下文
     */
    protected open val cacheContext = CacheContext()

    /**
     * 上下文提供者 (目前就是为了提供 [cacheContext])
     */
    override val contextProvider = ArgumentContextProvider { SimpleContext(cacheContext) }

    /**
     * 基础物品流 (经过预处理生成的流)
     */
    open val baseStream: NativeItemStream by lazy {
        createNativeStream(SimpleData(), contextProvider.newContext())
    }

    /**
     * 解释器编排器
     */
    override val compositor = DefaultCompositor(baseStream)

    /**
     * 静态物品策略
     */
    open val staticStrategy = StaticStrategy.fromStream(baseStream)

    /**
     * 静态物品流生成器
     */
    open val staticGenerator = staticStrategy.StreamGenerator(baseStream) { compositor.dispatch(it) }

    override fun build() = build(SimpleContext())

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(context)

    /**
     * 异步生成物品
     */
    open fun buildAsync(context: ArgumentContext) = ItemElement.scope.async {
        // 获取物品流
        val stream = staticGenerator.stream().await()
        context.putAll(contextProvider.newContext().args())
        // 更新上下文
        stream.context = context

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(stream.identifier, this@DefaultGenerator, context, origin.property).call()

        // 非纯静态物品处理物品流
        if (!staticStrategy.fullStaticMode) {
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
    open fun createNativeStream(sourceData: ItemData, context: ArgumentContext): NativeItemStream {
        // 生成基本物品 (本地源物品)
        val item = NativeSource.generateItem(origin, sourceData)
            ?: throw IllegalStateException("Failed to generate item source!")
        // 创建物品流并返回
        return NativeItemStream(origin, item, context)
    }

}