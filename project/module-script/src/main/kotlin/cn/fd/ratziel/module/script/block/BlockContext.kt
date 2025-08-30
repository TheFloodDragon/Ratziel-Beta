package cn.fd.ratziel.module.script.block

import cn.fd.ratziel.core.contextual.AttachedContext
import kotlinx.serialization.json.JsonElement

/**
 * BlockContext
 *
 * @author TheFloodDragon
 * @since 2025/8/30 19:46
 */
interface BlockContext {

    /**
     * 语句块调度器
     */
    val scheduler: BlockParser

    /**
     * 附加的上下文
     */
    val attached: AttachedContext

    /**
     * 使用此上下文解析 [JsonElement]
     */
    fun parse(element: JsonElement): ExecutableBlock? = this.scheduler.parse(element, this)

    companion object {

        @JvmStatic
        fun of(scheduler: BlockParser): BlockContext = BlockContextImpl(scheduler)

    }

    private class BlockContextImpl(
        override val scheduler: BlockParser,
        override val attached: AttachedContext = AttachedContext.newContext(),
    ) : BlockContext {
        override fun toString() = "BlockContext(attached=$attached, scheduler=$scheduler)"
    }


}