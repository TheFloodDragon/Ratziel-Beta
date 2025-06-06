package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.api.action.ActionMap
import cn.fd.ratziel.module.item.api.action.ItemTrigger
import cn.fd.ratziel.module.item.impl.service.NativeServiceRegistry
import cn.fd.ratziel.module.script.api.ScriptEnvironment
import cn.fd.ratziel.module.script.impl.SimpleScriptEnvironment
import taboolib.common.platform.function.debug
import java.util.concurrent.ConcurrentHashMap

/**
 * ActionManager
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:20
 */
object ActionManager {

    /**
     * [ItemTrigger] 注册表
     */
    val registry: MutableMap<String, ItemTrigger> = ConcurrentHashMap()

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
     * 触发指定物品的动作
     */
    @JvmStatic
    fun ItemTrigger.trigger(identifier: Identifier, context: ArgumentContext) {
        // 获取物品动作 (无动作时返回)
        val action = service[identifier]?.get(this) ?: return
        // 执行物品动作
        action.execute(context)
        // Debug
        debug("[ActionManager] '$this' trigger action '$action'.")
    }

    /**
     * 触发指定物品的动作
     */
    @JvmStatic
    fun ItemTrigger.trigger(identifier: Identifier, vararg values: Any, action: (ScriptEnvironment).() -> Unit) {
        val environment = SimpleScriptEnvironment()
        action(environment)
        val context = SimpleContext(environment, *values)
        this.trigger(identifier, context)
    }

}