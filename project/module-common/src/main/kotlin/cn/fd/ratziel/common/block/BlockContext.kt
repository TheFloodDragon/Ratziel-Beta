package cn.fd.ratziel.common.block

import cn.fd.ratziel.core.contextual.ArgumentContext
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
     * 选项
     */
    val options: MutableMap<String, Any> = hashMapOf()

    /**
     * 附加的上下文
     */
    val attached = AttachedContext.newContext()

    /**
     * 运行时是否复制参数上下文
     */
    var copyContext: Boolean = false

    /**
     * 开始执行时调用
     */
    internal var onStart: (ArgumentContext) -> Unit = { }

    /**
     * 结束执行时调用
     */
    internal var onEnd: (ArgumentContext, Any?) -> Any? = { _, v -> v }

    /**
     * 获取选项
     */
    operator fun get(name: String): String? = this.options[name]?.toString()

    /**
     * 设置选项
     */
    operator fun set(name: String, value: Any) = this.options.put(name, value)

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

    override fun toString() = "BlockContext(workFile=$workFile, scheduler=$scheduler)"

}