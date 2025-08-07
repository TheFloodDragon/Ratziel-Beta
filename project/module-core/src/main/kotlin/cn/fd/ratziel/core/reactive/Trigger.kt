package cn.fd.ratziel.core.reactive

/**
 * Trigger - 触发器
 *
 * 有关回应者: 每种类型的响应者各只能有一个
 *
 * @author TheFloodDragon
 * @since 2025/8/6 20:43
 */
interface Trigger {

    /**
     * 触发器名称数组
     */
    val names: Array<out String>

    /**
     * 该触发器绑定的所有回应者
     */
    val responders: Iterable<Responder>

    /**
     * 获取指定类型的回应者
     */
    fun <T : Responder> responder(type: Class<T>): T?

    /**
     * 绑定回应者
     */
    fun bind(responder: Responder, priority: Byte = 0)

    /**
     * 触发此触发器
     *
     * @param body 响应体
     */
    fun trigger(body: ResponseBody) {
        for (responder in this.responders) {
            responder.accept(body, this)
        }
    }

}