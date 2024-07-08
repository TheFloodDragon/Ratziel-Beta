package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.feature.action.triggers.*
import cn.fd.ratziel.module.item.impl.service.NativeServiceRegistry
import cn.fd.ratziel.script.ScriptEnvironment
import cn.fd.ratziel.script.SimpleScriptEnv
import java.util.concurrent.ConcurrentHashMap

/**
 * ActionManager
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:20
 */
object ActionManager {

    /**
     * 物品触发器注册表
     */
    val triggers: MutableSet<ItemTrigger> = mutableSetOf(
        AttackTrigger, DamageTrigger, BreakTrigger,
        InteractTrigger.LeftClick, InteractTrigger.RightClick,
        ReleaseTrigger,
    )

    /**
     * 物品唯一标识符 - 触发器表 (存有物品触发器和物品动作)
     */
    val actionMap: MutableMap<Identifier, TriggerMap> = ConcurrentHashMap()

    init {
        // 注册服务
        NativeServiceRegistry.register(TriggerMap::class.java, { actionMap[it] }, { i, m -> actionMap[i] = m })
    }

    /**
     * 触发指定物品的指定触发器, 以处理物品动作
     */
    @JvmStatic
    fun trigger(identifier: Identifier, trigger: ItemTrigger, environment: ScriptEnvironment) {
        // 获取物品动作
        val action = actionMap[identifier]?.get(trigger)
        // 设置绑定键
        environment.context.add(environment.bindings)
        // 执行物品动作
        action?.execute(environment.context)
    }

    @JvmStatic
    fun trigger(identifier: Identifier, trigger: ItemTrigger, function: ScriptEnvironment.() -> Unit) =
        trigger(identifier, trigger, SimpleScriptEnv().apply(function))

    /**
     * 匹配 [TriggerMap]
     */
    @JvmStatic
    fun matchTrigger(name: String): ItemTrigger? = triggers.firstOrNull { it.names.contains(name) }

}