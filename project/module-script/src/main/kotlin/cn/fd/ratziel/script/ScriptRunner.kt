package cn.fd.ratziel.script

import cn.fd.ratziel.script.js.JavaScriptLang
import cn.fd.ratziel.script.kether.KetherLang

/**
 * ScriptRunner
 *
 * @author TheFloodDragon
 * @since 2024/6/30 12:06
 */
object ScriptRunner {

    /**
     * 使用的脚本语言
     */
    @JvmStatic
    val scriptLanguages = arrayOf(
        KetherLang, JavaScriptLang.getInstance()
    )

    /**
     * 默认使用的脚本语言
     */
    @JvmField
    var defaultLang: ScriptLanguage = scriptLanguages.first()

    /**
     * 运行脚本
     */
    @JvmOverloads
    @JvmStatic
    fun eval(script: ScriptStorage, env: ScriptEnvironment = SimpleScriptEnv(), lang: ScriptLanguage = defaultLang) {
        lang.eval(script, env)
    }

    /**
     * 运行脚本
     */
    @JvmOverloads
    @JvmStatic
    fun eval(id: String, script: ScriptStorage, env: ScriptEnvironment = SimpleScriptEnv()) {
        scriptById(id)?.eval(script, env)
    }

    /**
     * 通过ID获取 [ScriptLanguage]
     */
    @JvmStatic
    fun scriptById(id: String): ScriptLanguage? {
        for (lang in scriptLanguages) {
            if (lang.name == id || lang.alias.contains(id))
                return lang
        }
        return null
    }

}