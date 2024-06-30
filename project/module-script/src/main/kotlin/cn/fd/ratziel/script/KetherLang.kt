package cn.fd.ratziel.script

import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions

/**
 * KetherLang
 *
 * @author TheFloodDragon
 * @since 2024/6/30 10:14
 */
object KetherLang : ScriptLanguage {

    private const val NAME = "kether"

    private val ALIAS = arrayOf("Kether", "ke", "ks")

    override fun eval(script: ScriptStorage, environment: ScriptEnvironment): Any {
        return KetherShell.eval(script.content, ScriptOptions.new { vars(environment.scriptBindings) })
    }

    override fun getName() = NAME

    override fun getAlias() = ALIAS

}