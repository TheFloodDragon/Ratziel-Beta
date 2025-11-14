package cn.fd.ratziel.module.script.conf

import cn.fd.ratziel.core.contextual.AttachedProperties

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
    baseConfigurations: Iterable<ScriptConfiguration> = emptyList(), body: Builder.() -> Unit = {},
) : AttachedProperties(Builder(baseConfigurations).apply { body(this) }), ScriptConfigurationKeys {

    class Builder(baseConfigurations: Iterable<ScriptConfiguration> = emptyList()) : Mutable(baseConfigurations), ScriptConfigurationKeys

    object Default : ScriptConfiguration()

}
