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
        val templateActions = InheritInterpreter.templateActions[body.context]
        // 空的就爬去
        if (templateActions.isEmpty()) return
        // 执行动作
        for (actionMaps in templateActions.values) {
            for (actionMap in actionMaps) {
                ItemResponder.run(actionMap, body.context, trigger)
            }
        }
    }

}