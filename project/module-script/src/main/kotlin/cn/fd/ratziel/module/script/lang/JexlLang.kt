package cn.fd.ratziel.module.script.lang

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.api.ScriptExecutor
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import cn.fd.ratziel.module.script.lang.jexl.JexlScriptExecutor
import taboolib.library.configuration.ConfigurationSection

/**
 * JexlLang
 *
 * @author TheFloodDragon
 * @since 2025/7/6 15:33
 */
object JexlLang : ScriptType, ScriptBootstrap {

    override val name = "Jexl"

    override val alias = arrayOf("Jexl3")

    override val executor: ScriptExecutor get() = JexlScriptExecutor

    override fun initialize(settings: ConfigurationSection) {
        ScriptManager.loadDependencies("jexl")
    }

}