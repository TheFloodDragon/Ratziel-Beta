package cn.fd.ratziel.module.script.lang.kts

import cn.fd.ratziel.module.script.ScriptManager
import cn.fd.ratziel.module.script.ScriptType
import cn.fd.ratziel.module.script.impl.ScriptBootstrap
import taboolib.library.configuration.ConfigurationSection

/**
 * KotlinScriptingLang
 *
 * @author TheFloodDragon
 * @since 2025/10/2 17:39
 */
object KotlinScriptingLang : ScriptType, ScriptBootstrap {

    override val name = "KotlinScripting"

    override val alias = arrayOf("Kotlin", "kts")

//    override val executor: ScriptExecutor get() = KotlinScriptingExecutor

    override fun initialize(settings: ConfigurationSection) {
        ScriptManager.loadDependencies("kotlin-scripting")
    }

}