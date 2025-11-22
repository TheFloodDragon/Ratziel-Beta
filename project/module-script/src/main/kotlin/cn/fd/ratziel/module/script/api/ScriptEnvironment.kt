package cn.fd.ratziel.module.script.api

import cn.fd.ratziel.core.contextual.AttachedContext
import cn.fd.ratziel.module.script.conf.ScriptConfiguration
import cn.fd.ratziel.module.script.conf.with


/**
 * ScriptEnvironment - 脚本环境
 *
 * @author TheFloodDragon
 * @since 2024/7/15 13:20
 */
open class ScriptEnvironment(
    /**
     * 脚本的绑定键
     */
    var bindings: MutableMap<String, Any?> = linkedMapOf(),
    /**
     * 脚本配置
     */
    var configuration: ScriptConfiguration = ScriptConfiguration.Default,
) {

    constructor(
        bindings: MutableMap<String, Any?> = linkedMapOf(),
        baseConfigurations: Iterable<ScriptConfiguration> = emptyList(),
        body: ScriptConfiguration.Builder.() -> Unit,
    ) : this(bindings, ScriptConfiguration(baseConfigurations, body))

    constructor(other: ScriptEnvironment, body: ScriptConfiguration.Builder.() -> Unit) : this(other.bindings, other.configuration.with(body))

    /**
     * 运行时状态 (存放各种脚本的上下文)
     */
    val runningState: AttachedContext = AttachedContext.newContext()

    /**
     * 获取绑定内容
     *
     * @param key   绑定键
     */
    operator fun get(key: String) = this.bindings[key]

    /**
     * 设置绑定内容
     *
     * @param key   绑定键
     * @param value 绑定内容
     */
    operator fun set(key: String, value: Any?) {
        this.bindings[key] = value
    }

}