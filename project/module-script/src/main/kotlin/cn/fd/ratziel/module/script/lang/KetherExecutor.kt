package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.api.ScriptContent
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.SimpleScriptContent
import cn.fd.ratziel.module.script.internal.Initializable
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptCommandSender
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.kether.Kether
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions

/**
 * KetherExecutor
 *
 * @author TheFloodDragon
 * @since 2024/7/14 21:40
 */
object KetherExecutor : ScriptExecutor, Initializable {

    override fun build(script: String, environment: ScriptEnvironment): ScriptContent = SimpleScriptContent(script, this)

    override fun evaluate(script: ScriptContent, environment: ScriptEnvironment): Any {
        val options = ScriptOptions.new {
            val bindings = environment.bindings
            vars(bindings)
            // 尝试获取 Sender
            val sender = bindings["sender"] ?: bindings["player"]
            if (sender != null) {
                val adapted = sender as? ProxyCommandSender ?: runCatching { adaptCommandSender(sender) }.getOrNull()
                if (adapted != null) sender(adapted) // 设置 Sender
            }
        }
        return KetherShell.eval(script.content, options)
    }

    override fun initialize(settings: ConfigurationSection) {
        try {
            Kether
        } catch (ex: Exception) {
            throw RuntimeException("Kether Environment does not initialized!", ex)
        }
        // 宽容解析
        val tolerance = settings.getBoolean("tolerance", true)
        Kether.isAllowToleranceParser = tolerance
    }

}