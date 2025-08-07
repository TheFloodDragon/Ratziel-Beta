package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.functional.SimpleContext
import cn.fd.ratziel.core.reactive.ContextualResponse
import cn.fd.ratziel.core.reactive.SimpleTrigger
import cn.fd.ratziel.core.reactive.Trigger
import cn.fd.ratziel.module.item.impl.service.NativeServiceRegistry
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.SimpleScriptEnvironment
import taboolib.common.platform.function.severe
import java.util.concurrent.ConcurrentHashMap

/**
 * ActionManager
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:20
 */
object ActionManager {

    /**
     * 触发器注册表: 触发器名称 -> 触发器
     */
    private val registry: MutableMap<String, Trigger> = ConcurrentHashMap()

    /**
     * 注册的物品触发器列表
     */
    val triggers: Collection<Trigger> get() = registry.values

    /**
     * 物品动作服务
     */
    val service: MutableMap<Identifier, ActionMap> = ConcurrentHashMap()

    init {
        // 注册服务
        NativeServiceRegistry.register(
            ActionMap::class.java,
            { service[it] },
            { k, v -> service[k] = v }
        )
    }

    /**
     * 注册触发器
     */
    @JvmStatic
    fun register(trigger: Trigger) {
        // 绑定回应者
        trigger.bind(ItemResponder)
        // 注册到注册表中
        for (name in trigger.names) this.registry[name] = trigger
    }

    /**
     * 注册简易的 [SimpleTrigger]
     */
    fun registerSimple(vararg names: String) = SimpleTrigger(names).also { this.register(it) }

    /**
     * 匹配触发器
     */
    @JvmStatic
    fun matchTrigger(name: String): Trigger? {
        val trigger = registry[name]
        if (trigger == null) {
            severe("Unknown trigger: \"$name\" !")
            return null
        } else return trigger
    }

    /**
     * 触发指定物品的动作
     */
    @JvmStatic
    fun Trigger.trigger(identifier: Identifier, vararg values: Any?, action: (ScriptEnvironment).() -> Unit) {
        val environment = SimpleScriptEnvironment()
        action(environment)
        val context = SimpleContext(environment, *values.mapNotNull { it }.toTypedArray())
        this.trigger(ContextualResponse(identifier, context))
    }

}