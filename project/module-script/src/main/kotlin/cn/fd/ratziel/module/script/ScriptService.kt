package cn.fd.ratziel.module.script

import cn.fd.ratziel.module.script.api.ScriptType
import cn.fd.ratziel.module.script.lang.fluxon.FluxonLang
import cn.fd.ratziel.module.script.lang.jexl.JexlLang
import cn.fd.ratziel.module.script.lang.js.JavaScriptLang
import cn.fd.ratziel.module.script.lang.kts.KotlinScriptingLang

/**
 * ScriptService
 *
 * @author TheFloodDragon
 * @since 2025/11/1 23:09
 */
object ScriptService {

    /**
     * 脚本类型注册表
     */
    @JvmStatic
    val registry: MutableSet<ScriptType> = mutableSetOf()

    /**
     * 启用的脚本语言列表
     */
    var enabledLanguages: Set<ScriptType> = emptySet()
        internal set

    /** JavaScript **/
    @JvmStatic
    val JAVASCRIPT = register(JavaScriptLang)

    /** Jexl **/
    @JvmStatic
    val JEXL = register(JexlLang)

    /** Kotlin Scripting **/
    @JvmStatic
    val KOTLIN_SCRIPTING = register(KotlinScriptingLang)

    /** Fluxon **/
    @JvmStatic
    val FLUXON = register(FluxonLang)

    /**
     * 匹配脚本类型
     */
    @JvmStatic
    fun matchOrThrow(name: String, onlyEnabled: Boolean = true): ScriptType {
        val cleaned = name.filterNot { it.isWhitespace() }
        val find = registry.find { type ->
            type.name.equals(cleaned, true)
                    || type.languageId.equals(cleaned, true)
                    || type.alias.any { it.equals(cleaned, true) }
        } ?: throw IllegalArgumentException("Couldn't find script-language by id: $name")
        // 当脚本语言为启用并且要求只匹配启用的脚本时抛出异常
        if (!find.isEnabled && onlyEnabled) {
            error("Script language ${find.name} is not enabled.")
        }
        return find
    }

    /**
     * 判断脚本是否启用
     */
    @JvmStatic
    fun isEnabled(type: ScriptType) = type in enabledLanguages

    /**
     * 启用脚本语言
     */
    @JvmStatic
    internal fun enableLanguage(type: ScriptType): Boolean {
        if (isEnabled(type) || runCatching { type.executor }.isSuccess) {
            // 成功启用脚本, 初始化下一个
            enabledLanguages = enabledLanguages.plus(type)
            return true
        } else return false
    }

    @JvmStatic
    private fun <T : ScriptType> register(scriptType: T): T {
        this.registry.add(scriptType)
        return scriptType
    }

}