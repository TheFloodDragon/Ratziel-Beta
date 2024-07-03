package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.function.argument.SimpleArgumentContext
import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.impl.feature.action.triggers.AttackTrigger
import cn.fd.ratziel.module.item.impl.service.NativeServiceRegistry
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
    val triggers: MutableSet<ItemTrigger> = mutableSetOf(AttackTrigger)

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
    @JvmOverloads
    fun trigger(identifier: Identifier, trigger: ItemTrigger, context: ArgumentContext = SimpleArgumentContext()) {
        // 获取物品动作
        val action = actionMap[identifier]?.get(trigger)
        // 执行物品动作
        action?.execute(context)
    }

    @JvmStatic
    fun trigger(identifier: Identifier, trigger: ItemTrigger, function: ArgumentContext.() -> Unit) =
        trigger(identifier, trigger, SimpleArgumentContext().apply(function))

    /**
     * 匹配 [TriggerMap]
     */
    @JvmStatic
    fun matchTrigger(name: String): ItemTrigger? = triggers.firstOrNull { it.names.contains(name) }

}