package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptExecutor

/**
 * CompiledScript
 *
 * @author TheFloodDragon
 * @since 2025/8/30 19:16
 */
open class CompiledScript<T : Any>(
    override val content: String,
    override val executor: ScriptExecutor,
    /** 编译后的脚本 **/
    open val compiled: T,
) : ScriptContent {

    constructor(delegate: CompiledScript<T>) : this(delegate.content, delegate.executor, delegate.compiled)

}
