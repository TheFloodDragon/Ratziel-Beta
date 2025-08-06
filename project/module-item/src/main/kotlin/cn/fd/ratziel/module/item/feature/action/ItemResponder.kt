package cn.fd.ratziel.module.item.feature.action

import cn.fd.ratziel.core.reactive.ContextualResponder
import cn.fd.ratziel.core.reactive.ContextualResponse
import cn.fd.ratziel.core.reactive.Trigger
import taboolib.common.platform.function.debug

/**
 * ItemResponder
 *
 * @author TheFloodDragon
 * @since 2025/8/6 21:09
 */
object ItemResponder : ContextualResponder {

    override fun accept(body: ContextualResponse, trigger: Trigger) {
        // 获取动作表
        val actionsMap = ActionManager.service[body.identifier] ?: return
        // 获取动作
        val action = actionsMap[trigger] ?: return
        // 执行动作
        action.execute(body.context)
        // Debug
        debug("[ItemResponder] '$trigger' trigger action '$this'.")
    }

}