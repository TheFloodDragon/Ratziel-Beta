package cn.fd.ratziel.common.block

import cn.fd.ratziel.core.contextual.ArgumentContext
import kotlinx.serialization.json.JsonElement

/**
 * BlockContext
 *
 * @author TheFloodDragon
 * @since 2025/8/30 19:46
 */
open class BlockContext(
    /**
     * 语句块调度器 (仅解析时有用)
     */
    val scheduler: BlockParser? = null,
) : BlockConfiguration() {

    /**
     * 开始执行时调用
     */
    internal var onStart: (ArgumentContext) -> Unit = { }

    /**
     * 结束执行时调用
     */
    internal var onEnd: (ArgumentContext, Any?) -> Any? = { _, v -> v }

    /**
     * 添加开始执行时的回调
     */
    fun onStart(action: (ArgumentContext) -> Unit) {
        onStart = {
            onStart(it)
            action(it)
        }
    }

    /**
     * 添加结束执行时的回调
     */
    fun onEnd(action: (ArgumentContext, Any?) -> Any?) {
        onEnd = { c, v ->
            onEnd(c, v)
            action(c, v)
        }
    }

    /**
     * 使用此上下文解析 [JsonElement]
     */
    fun parse(element: JsonElement): ExecutableBlock {
        val scheduler = this.scheduler ?: throw UnsupportedOperationException("BlockContext hasn't a scheduler.")
        val parsed = scheduler.parse(element, this)
        return parsed ?: throw IllegalStateException("Could not parse block: $element")
    }

    override fun toString() = "BlockContext(workFile=${workFile()}, scheduler=$scheduler)"

}

/**
 * 执行入口
 */
internal class ExecutionEntrance(
    val run: ExecutableBlock,
    val ctx: BlockContext,
) : ExecutableBlock {

    init {
        ctx.cleanWeakKeys() // 清空非运行时需要的键
    }

    override fun execute(context: ArgumentContext): Any? {
        // 根据配置复制上下文(或者不复制)
        val context = if (ctx[BlockConfigurationKeys.copyContext]) context.copy() else context
        // 开始时调用
        ctx.onStart.invoke(context)
        // 执行并取得结果
        val result = run.execute(context)
        // 结束时调用
        return ctx.onEnd.invoke(context, result) ?: result
    }

    override fun toString() = "$run;"
}