package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.module.item.api.action.ActionMap
import cn.fd.ratziel.module.item.api.action.ItemAction
import cn.fd.ratziel.module.item.api.action.ItemTrigger
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.script.block.ScriptBlockBuilder
import kotlinx.serialization.json.JsonObject
import taboolib.common.io.digest
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
    fun parse(identifier: Identifier, element: JsonObject): ActionMap {
        // 创建触发器表
        val map = LinkedHashMap<ItemTrigger, ItemAction>()
        for ((triggerName, content) in element) {
            // 匹配触发器
            val trigger = ActionManager.registry[triggerName]
            if (trigger == null) {
                severe("Unknown trigger: \"$triggerName\" !")
                continue
            }
            // 构建脚本块
            val block = ScriptBlockBuilder.build(content)
            // 创建脚本动作, 放入表中
            val name = trigger.name + "#" + identifier.content + "@" + content.toString().digest("SHA-256")
            map[trigger] = SimpleAction(name, content, block)
        }
        return ActionMap(map)
    }

    @SubscribeEvent
    fun onGenerate(event: ItemGenerateEvent.Pre) {
        val element = event.element as? JsonObject ?: return
        // 获取原始动作
        val raw = element.getBy(*nodeNames) ?: return
        if (raw is JsonObject) {
            // 解析触发器表
            val triggerMap = parse(event.identifier, raw)
            // 加入到动作表中
            ActionManager.service[event.identifier] = triggerMap
        } else throw IllegalArgumentException("Incorrect action format! Unexpected: $raw")
    }

}