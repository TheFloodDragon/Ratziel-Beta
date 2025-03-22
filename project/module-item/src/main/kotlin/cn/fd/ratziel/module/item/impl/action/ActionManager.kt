package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.action.TriggerType
import cn.fd.ratziel.module.item.internal.action.provided.Triggers
import cn.fd.ratziel.module.item.impl.service.NativeServiceRegistry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

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
    val triggers: MutableSet<TriggerType> = CopyOnWriteArraySet(Triggers.entries)

    /**
     * 物品唯一标识符 - 触发器表 (存有物品触发器和物品动作)
     */
    val actionMap: MutableMap<Identifier, TriggerMap> = ConcurrentHashMap()

    init {
        // 注册服务
        NativeServiceRegistry.register(TriggerMap::class.java, { actionMap[it] }, { i, m -> actionMap[i] = m })
    }

    /**
     * 触发指定物品的指定触发器, 并执行物品动作
     */
    @JvmStatic
    fun trigger(identifier: Identifier, trigger: TriggerType, context: ArgumentContext) {
        // 获取物品动作 (无动作时返回)
        val action = actionMap[identifier]?.get(trigger) ?: return
        // 执行物品动作
        action.execute(trigger, context)
    }

    /**
     * 匹配 [TriggerMap]
     */
    @JvmStatic
    fun matchTrigger(name: String): TriggerType? = triggers.firstOrNull { it.names.contains(name) }

}