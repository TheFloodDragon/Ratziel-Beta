package cn.fd.ratziel.script

/**
 * ScriptRunner
 *
 * @author TheFloodDragon
 * @since 2024/6/30 12:06
 */
object ScriptRunner {

    /**
     * 使用的脚本语言
     * |- Kether, JavaScript, Jexl3
     */
    @JvmStatic
    val scriptLanguages = arrayOf(
        KetherLang, JavaScriptLang, JexlLang
    )

    /**
     * 运行脚本
     */
    @JvmStatic
    fun eval(script: ScriptStorage, env: ScriptEnvironment, lang: ScriptLanguage) {
        lang.eval(script, env)
    }

    /**
     * 运行脚本
     */
    @JvmStatic
    fun eval(id: String, script: ScriptStorage, env: ScriptEnvironment) {
        findLang(id).eval(script, env)
    }

    /**
     * 通过ID获取 [ScriptLanguage]
     */
    @JvmStatic
    fun findLang(id: String): ScriptLanguage {
        for (lang in scriptLanguages) {
            if (lang.name == id || lang.alias.contains(id))
                return lang
        }
        throw IllegalArgumentException("Couldn't find ScriptLanguage with id: $id")
    }

}