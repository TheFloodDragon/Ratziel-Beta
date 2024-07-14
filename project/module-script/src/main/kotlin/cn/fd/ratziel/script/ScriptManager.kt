package cn.fd.ratziel.script

/**
 * ScriptManager
 *
 * @author TheFloodDragon
 * @since 2024/6/30 12:06
 */
object ScriptManager {

    /**
     * 使用的脚本语言
     * |- Kether, JavaScript, Jexl3, Kotlin Script
     */
    @JvmStatic
    var scriptLanguages: Array<ScriptLanguage> = arrayOf(KetherLang, JavaScriptLang, JexlLang, KotlinScriptLang)
        private set

    /**
     * 默认语言
     */
    var defaultLang: ScriptLanguage = scriptLanguages.first()

    /**
     * 通过ID获取 [ScriptLanguage]
     */
    @JvmStatic
    fun findLang(id: String): ScriptLanguage {
        for (lang in scriptLanguages) {
            if (lang.names.contains(id)) return lang
        }
        throw IllegalArgumentException("Couldn't find ScriptLanguage with id: $id")
    }

    @JvmStatic
    fun register(lang: ScriptLanguage) {
        scriptLanguages = scriptLanguages.plus(lang)
    }

    @JvmStatic
    fun unregister(lang: ScriptLanguage) {
        scriptLanguages = scriptLanguages.toMutableSet().also { it.remove(lang) }.toTypedArray()
    }

}