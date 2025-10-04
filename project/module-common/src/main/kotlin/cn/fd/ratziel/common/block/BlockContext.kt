package cn.fd.ratziel.common.block

import cn.fd.ratziel.core.contextual.AttachedContext
import kotlinx.serialization.json.JsonElement
import java.io.File

/**
 * BlockContext
 *
 * @author TheFloodDragon
 * @since 2025/8/30 19:46
 */
open class BlockContext(
    /**
     * 语句块调度器
     */
    val scheduler: BlockParser? = null,
) {

    /**
     * 工作文件
     */
    var workFile: File? = null

    /**
     * 附加的上下文
     */
    val attached = AttachedContext.newContext()

    /**
     * 使用此上下文解析 [JsonElement]
     */
    fun parse(element: JsonElement): ExecutableBlock {
        val scheduler = this.scheduler ?: throw UnsupportedOperationException("BlockContext hasn't a scheduler.")
        val parsed = scheduler.parse(element, this)
        return parsed ?: throw IllegalStateException("Could not parse block: $element")
    }

    override fun toString() = "BlockContext(workFile=$workFile, scheduler=$scheduler)"

}