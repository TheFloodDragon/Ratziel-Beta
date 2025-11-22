package cn.fd.ratziel.module.script.conf

import cn.fd.ratziel.core.contextual.AttachedProperties
import cn.fd.ratziel.module.script.ScriptManager

/**
 * scriptConfiguration
 *
 * @author TheFloodDragon
 * @since 2025/11/15 00:49
 */

interface ScriptConfigurationKeys {
    companion object : ScriptConfigurationKeys
}

open class ScriptConfiguration(
    baseConfigurations: Iterable<AttachedProperties> = emptyList(), body: Builder.() -> Unit = {},
) : AttachedProperties(Builder(baseConfigurations).apply { body(this) }), ScriptConfigurationKeys {

    class Builder(baseConfigurations: Iterable<AttachedProperties> = emptyList()) : Mutable(baseConfigurations), ScriptConfigurationKeys

    object Default : ScriptConfiguration()

}

/**
 * An alternative to the constructor with base configuration, which returns a new configuration only if [body] adds anything
 * to the original one, otherwise returns original
 */
fun ScriptConfiguration?.with(body: ScriptConfiguration.Builder.() -> Unit): ScriptConfiguration {
    val newConfiguration =
        if (this == null) ScriptConfiguration(body = body)
        else ScriptConfiguration(listOf(this), body = body)
    return if (newConfiguration == this) this else newConfiguration
}


/**
 * 脚本导入
 */
val ScriptConfigurationKeys.scriptImporting by AttachedProperties.key { ScriptManager.globalGroup }

/**
 * 脚本名称
 */
val ScriptConfigurationKeys.scriptName by AttachedProperties.key<String?>(null)

/**
 * 脚本缓存等级: (默认为1)
 *   < 0 : 不缓存
 *   >= 1 : 缓存 (缓存的模式视脚本语言而定)
 *
 * Fluxon:
 *   1 - AST 缓存
 *   2 - 字节码缓存
 */
val ScriptConfigurationKeys.scriptCaching by AttachedProperties.key(1)

/**
 * 显式脚本解析 (默认为 true)
 */
val ScriptConfigurationKeys.explicitScriptParsing by AttachedProperties.key(true)
