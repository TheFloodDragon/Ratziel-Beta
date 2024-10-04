package cn.fd.ratziel.module.item.feature.action.provided

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.SimpleArgumentContext
import cn.fd.ratziel.module.item.api.action.TriggerType
import cn.fd.ratziel.module.item.feature.action.ActionManager
import cn.fd.ratziel.script.SimpleScriptEnv
import cn.fd.ratziel.script.api.ScriptEnvironment

/**
 * Triggers
 *
 * @author TheFloodDragon
 * @since 2024/8/13 14:29
 */
@Deprecated("需要重构")
enum class Triggers(
    override vararg val names: String,
    val parent: TriggerType? = null
) : TriggerType {

    // 物品完成构建
    RELEASE("onRelease", "release", "onReleased", "released"),

    // 攻击
    ATTACK("onAttack", "onAtk", "attack", "atk"),

    // 被损坏
    BREAK("onBreak", "break"),

    // 耐久损伤
    DAMAGED("onDamaged", "damaged", "onDamage", "damage"),

    // 交互
    INTERACT_LEFT_CLICK("onLeft", "left", "onLeftClick", "leftClick"),
    INTERACT_RIGHT_CLICK("onLeft", "left", "onLeftClick", "leftClick"),
    INTERACT_LEFT_CLICK_AIR("onLeftClickedAir", "left-air", "left-click-air", parent = INTERACT_LEFT_CLICK),
    INTERACT_LEFT_CLICK_BLOCK("onLeftClickedBlock", "left-block", "left-click-block", parent = INTERACT_LEFT_CLICK),
    INTERACT_RIGHT_CLICK_AIR("onRightClickedAir", "right-air", "right-click-air", parent = INTERACT_RIGHT_CLICK),
    INTERACT_RIGHT_CLICK_BLOCK("onRightClickedBlock", "right-block", "right-click-block", parent = INTERACT_RIGHT_CLICK);

    fun trigger(identifier: Identifier, context: ArgumentContext) {
        // Trigger myself
        ActionManager.trigger(identifier, this, context)
        // Trigger parent
        if (parent != null) ActionManager.trigger(identifier, parent, context)
    }

    fun trigger(identifier: Identifier, environment: ScriptEnvironment) = trigger(identifier, SimpleArgumentContext(environment))

    fun trigger(identifier: Identifier, envAction: ScriptEnvironment.() -> Unit) = trigger(identifier, SimpleScriptEnv()
        .also { envAction(it) })

}