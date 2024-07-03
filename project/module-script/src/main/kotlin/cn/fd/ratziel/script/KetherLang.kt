package cn.fd.ratziel.script

import taboolib.common.platform.ProxyCommandSender
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions

/**
 * KetherLang
 *
 * @author TheFloodDragon
 * @since 2024/6/30 10:14
 */
object KetherLang : ReleasableScriptLanguage(
    "kether", "Kether", "ke", "ks" // 语言名称
) {

    override fun eval(script: ScriptStorage, environment: ScriptEnvironment): Any {
        return KetherShell.eval(script.content, ScriptOptions.new { vars(environment.scriptBindings) })
    }

    init {
        addReleaser { env ->
            val sender = env.argumentContext.popOrNull(ProxyCommandSender::class.java)
            env.scriptBindings.apply {
                put("@Sender", sender)
            }
        }
    }

}