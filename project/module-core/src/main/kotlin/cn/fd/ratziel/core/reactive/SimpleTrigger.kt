package cn.fd.ratziel.core.reactive

import cn.fd.ratziel.core.Prioritized
import cn.fd.ratziel.core.util.sortPriority
import java.util.concurrent.ConcurrentHashMap

/**
 * SimpleTrigger
 *
 * @author TheFloodDragon
 * @since 2025/8/6 20:47
 */
open class SimpleTrigger(
    /**
     * 触发器名称数组
     */
    override val names: Array<out String>,
) : Trigger {

    /**
     * 回应者表
     */
    val respondersMap: MutableMap<Class<*>, Prioritized<Responder>> = ConcurrentHashMap()

    /**
     * 获取指定类型的回应者
     */
    override fun <T : Responder> responder(type: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return respondersMap[type] as? T
    }

    /**
     * 绑定回应者
     */
    override fun bind(responder: Responder, priority: Byte) {
        respondersMap[responder::class.java] = Prioritized(priority, responder)
    }

    /**
     * 该触发器绑定的所有回应者
     */
    override val responders get() = respondersMap.values.sortPriority()

    override fun toString() = "Trigger(names=${names.contentToString()}, responders=$responders)"

}