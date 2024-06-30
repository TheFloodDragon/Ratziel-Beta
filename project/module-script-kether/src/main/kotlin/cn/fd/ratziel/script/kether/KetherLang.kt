package cn.fd.ratziel.script.kether

import cn.fd.ratziel.script.ScriptEnvironment
import cn.fd.ratziel.script.ScriptLanguage
import cn.fd.ratziel.script.ScriptStorage
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions

/**
 * KetherLang
 *
 * @author TheFloodDragon
 * @since 2024/6/30 10:14
 */
object KetherLang : ScriptLanguage {

    private const val name = "kether"

    private val alias = arrayOf("Kether", "ke", "ks")

    override fun getName() = name

    override fun getAlias() = alias

    override fun eval(script: ScriptStorage, environment: ScriptEnvironment): Any {
        return KetherShell.eval(script.content, ScriptOptions.new { vars(environment.scriptBindings) })
    }

}