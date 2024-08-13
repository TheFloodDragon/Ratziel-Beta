package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.core.serialization.toBasic
import cn.fd.ratziel.module.item.api.event.ItemResolveEvent
import cn.fd.ratziel.script.ScriptBlockBuilder
import cn.fd.ratziel.script.ScriptManager
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.severe

/**
 * ActionParser
 *
 * @author TheFloodDragon
 * @since 2024/7/3 18:55
 */
@Suppress("unused")
object ActionParser {

    /**
     * 动作节点名称
     */
    val nodeNames = arrayOf("action", "actions", "act", "acts")

    /**
     * 从配置中解析成触发器表
     */
    fun parse(element: JsonObject): TriggerMap {
        // 创建触发器表
        val map = TriggerMap()
        for (raw in element) {
            // 匹配触发器
            val type = ActionManager.matchTrigger(raw.key)
            if (type == null) {
                severe("Unknown trigger: \"${raw.key}\" !")
                continue
            }
            // 构建脚本块
            val block = ScriptBlockBuilder.build(raw.value.toBasic(), ScriptManager.defaultLanguage.executor)
            // 创建脚本动作, 放入表中
            map[type] = ScriptedAction(block)
        }
        return map
    }

    @SubscribeEvent
    fun onResolvingFinished(event: ItemResolveEvent) {
        val element = event.result as? JsonObject ?: return
        // 获取原始动作
        val raw = element.getBy(nodeNames.asIterable()) ?: return
        if (raw is JsonObject) {
            // 解析触发器表
            val triggerMap = parse(raw)
            // 加入到动作表中
            ActionManager.actionMap[event.identifier] = triggerMap
        } else throw IllegalArgumentException("Incorrect action format!")
    }

}