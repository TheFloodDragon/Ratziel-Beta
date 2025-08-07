package cn.fd.ratziel.module.item.feature.template

import cn.fd.ratziel.core.reactive.ContextualResponder
import cn.fd.ratziel.core.reactive.ContextualResponse
import cn.fd.ratziel.core.reactive.Trigger
import cn.fd.ratziel.module.item.feature.action.ItemResponder

/**
 * InheritResponder
 *
 * @author TheFloodDragon
 * @since 2025/8/6 22:30
 */
object InheritResponder : ContextualResponder {

    override fun accept(body: ContextualResponse, trigger: Trigger) {
        // 获取动作链表
        val chain = InheritInterpreter.actionsChain[body.context]
        // 空的就爬去
        if (chain.isEmpty()) return
        // 执行动作
        for ((_, actionMap) in chain) {
            if (actionMap != null) {
                ItemResponder.run(actionMap, body.context, trigger)
            }
        }
    }

}