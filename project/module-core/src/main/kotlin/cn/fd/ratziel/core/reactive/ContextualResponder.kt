package cn.fd.ratziel.core.reactive

/**
 * ContextualResponder
 *
 * @author TheFloodDragon
 * @since 2025/8/6 20:54
 */
interface ContextualResponder : Responder {

    /**
     * 接受 [ContextualResponse]
     */
    fun accept(body: ContextualResponse, trigger: Trigger)

    /**
     * 接受 [ResponseBody]
     * 无重写情况下只通过  [ContextualResponse]
     */
    override fun accept(body: ResponseBody, trigger: Trigger) {
        if (body is ContextualResponse) this.accept(body, trigger)
    }

}