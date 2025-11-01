package cn.fd.ratziel.module.script.api

/**
 * ScriptCompiler
 *
 * @author TheFloodDragon
 * @since 2025/11/1 19:55
 */
interface ScriptCompiler {

    /**
     * 编译脚本
     *
     * @param  source 脚本源
     * @param environment 脚本环境
     */
    @Throws(ScriptCompilationException::class)
    fun compile(source: ScriptSource, environment: ScriptEnvironment): CompiledScript

}
