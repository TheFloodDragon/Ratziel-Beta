package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.module.item.event.ItemBuildEvent
import cn.fd.ratziel.module.item.event.ItemEvent
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


/**
 * 注册的动作类型列表
 */
val actionTypes: Array<ItemAction.ActionType> = arrayOf(PreBuild, PostBuild)

@Awake
private fun registerActions() {
    ActionManager.registry.addAll(actionTypes)
}

private abstract class ActionType(
    override val name: String,
    override val alias: Array<String>
) : ItemAction.ActionType {
    constructor(vararg names: String) : this(names.first(), names.drop(1).toTypedArray())

    fun act(event: ItemEvent) = ActionManager.map[event.identifier]?.get(this)
}

private object PreBuild : ActionType(
    "onStart", "onBegin", "onBuild", "build",
) {
    @SubscribeEvent(EventPriority.LOW)
    fun preBuild(event: ItemBuildEvent.Pre) = act(event)?.handle()
}

private object PostBuild : ActionType(
    "onFinish", "onFinished",
) {
    @SubscribeEvent(EventPriority.LOW)
    fun preBuild(event: ItemBuildEvent.Post) = act(event)?.handle()
}