package cn.fd.ratziel.script.api

import cn.fd.ratziel.script.SimpleScript
import java.io.Reader
import javax.script.Compilable
import javax.script.CompiledScript

/**
 * CompilableScriptExecutor
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:49
 */
interface CompilableScriptExecutor : ScriptExecutor, Compilable {

    override fun compile(script: Reader?): CompiledScript {
        return compile(script?.readText())
    }

    override fun build(script: String): ScriptContent {
        return SimpleScript(script,this) // TODO
    }

}