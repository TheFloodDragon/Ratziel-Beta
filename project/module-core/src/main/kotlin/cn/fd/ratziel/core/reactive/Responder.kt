package cn.fd.ratziel.core.reactive

/**
 * Responder
 *
 * 不建议其实现类再被继承
 *
 * @author TheFloodDragon
 * @since 2025/8/6 20:45
 */
interface Responder {

    /**
     * 接受响应体
     *
     * @param body 响应体
     * @param trigger 传递响应体的触发器
     */
    fun accept(body: ResponseBody, trigger: Trigger)

}