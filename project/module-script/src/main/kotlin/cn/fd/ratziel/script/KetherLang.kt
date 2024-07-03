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
        return KetherShell.eval(script.content, ScriptOptions.new { vars(environment.bindings) })
    }

    init {
        addReleaser { env ->
            val sender = env.context.popOrNull(ProxyCommandSender::class.java)
            env.bindings.apply {
                put("@Sender", sender)
            }
        }
    }

}