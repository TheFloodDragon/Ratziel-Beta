package cn.fd.ratziel.module.script.api

import cn.fd.ratziel.core.contextual.AttachedContext


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
) {

    /**
     * 执行器的上下文
     */
    val context: AttachedContext = AttachedContext.newContext()

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