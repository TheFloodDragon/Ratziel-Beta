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
        fun of(scheduler: BlockParser): BlockContext = object : BlockContext {
            override val scheduler = scheduler
            override val attached = AttachedContext.newContext()
            override fun toString() = "BlockContext(attached=$attached, scheduler=$scheduler)"
        }

        @JvmStatic
        fun withoutScheduler(): BlockContext = object : BlockContext {
            override val scheduler get() = throw UnsupportedOperationException("BlockContext hasn't a scheduler.")
            override val attached = AttachedContext.newContext()
            override fun toString() = "BlockContext$attached"
        }

    }

}