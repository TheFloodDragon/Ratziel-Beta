package cn.fd.ratziel.module.script.impl

import java.util.concurrent.ConcurrentHashMap

/**
 * VariablesMap
 *
 * @author TheFloodDragon
 * @since 2025/6/9 17:15
 */
class VariablesMap(
    private val vars: MutableMap<String, Any?> = ConcurrentHashMap(),
) : MutableMap<String, Any?> by vars {

    constructor(action: MutableMap<String, Any?>.() -> Unit) : this() {
        action(this.vars)
    }

}