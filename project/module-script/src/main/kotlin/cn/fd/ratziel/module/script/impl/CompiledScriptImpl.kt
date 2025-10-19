package cn.fd.ratziel.module.script.impl

import cn.fd.ratziel.module.script.api.CompiledScript
import cn.fd.ratziel.module.script.api.ScriptExecutor

/**
 * CompiledScriptImpl
 *
 * @author TheFloodDragon
 * @since 2025/8/30 19:16
 */
open class CompiledScriptImpl<T : Any>(
    override val content: String,
    override val executor: ScriptExecutor,
    /** 编译后的脚本 **/
    override val compiled: T,
) : CompiledScript<T>
