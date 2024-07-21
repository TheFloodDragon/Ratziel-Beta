package cn.fd.ratziel.script

import cn.fd.ratziel.script.api.ScriptType
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake

/**
 * ScriptManager
 *
 * @author TheFloodDragon
 * @since 2024/7/16 11:30
 */
object ScriptManager {

    var defaultScriptLanguage: ScriptType = ScriptTypes.KETHER
        internal set

    @Awake
    private fun initLanguages() {
        // Load Env
        for (lang in ScriptTypes.entries) {
            if (lang.enabled) RuntimeEnv.ENV.loadDependency(lang.executor::class.java)
        }
    }

}