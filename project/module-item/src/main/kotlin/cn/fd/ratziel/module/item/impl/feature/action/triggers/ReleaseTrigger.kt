package cn.fd.ratziel.module.item.impl.feature.action.triggers

import cn.fd.ratziel.module.item.api.feature.ItemTrigger
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.feature.action.ActionManager
import cn.fd.ratziel.module.item.nms.RefItemStack
import taboolib.common.platform.event.SubscribeEvent

/**
 * ReleaseTrigger
 *
 * @author TheFloodDragon
 * @since 2024/7/8 16:00
 */
internal object ReleaseTrigger : ItemTrigger {

    override val names = arrayOf("onRelease", "release", "onFinished", "onFinish", "finish", "onEnd", "end")

    @SubscribeEvent
    fun onRelease(event: ItemGenerateEvent.Post) {
        val neoItem = event.item as? RatzielItem ?: return
        // 获取攻击时的物品
        val item = RefItemStack(event.item.data).getAsBukkit()
        // 触发触发器
        ActionManager.trigger(neoItem.identifier, this) {
            set("event", event)
            set("item", item)
            set("neoItem", neoItem)
        }
    }

}