package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.serialization.json.getBy
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.action.ActionMap
import cn.fd.ratziel.module.item.api.action.ItemAction
import cn.fd.ratziel.module.item.api.action.ItemTrigger
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.function.severe

/**
 * ActionInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/5/10 16:38
 */
object ActionInterpreter : ItemInterpreter.ElementInterpreter {

    /**
     * 动作节点名称
     */
    val nodeNames = arrayOf("action", "actions", "event", "events")

    /**
     * 从配置中解析成触发器表
     */
    fun parse(identifier: Identifier, element: JsonObject): ActionMap {
        // 创建触发器表
        val map = LinkedHashMap<ItemTrigger, ItemAction>()
        runBlocking(ItemElement.coroutineContext) {
            for ((triggerName, content) in element) {
                // 匹配触发器
                val trigger = ActionManager.registry[triggerName]
                if (trigger == null) {
                    severe("Unknown trigger: \"$triggerName\" !")
                    continue
                }
                launch {
                    // 构建脚本块
                    val block = trigger.build(identifier, content)
                    // 创建脚本动作, 放入表中
                    map[trigger] = SimpleAction(content, block)
                }
            }
        }
        return ActionMap(map)
    }

    override fun interpret(identifier: Identifier, element: Element) {
        val property = element.property
        // 仅处理 JsonObject 类型
        if (property !is JsonObject) return
        // 获取原始动作
        val raw = property.getBy(*nodeNames) ?: return
        if (raw is JsonObject) {
            // 解析触发器表
            val triggerMap = parse(identifier, raw)
            // 加入到动作表中
            ActionManager.service[identifier] = triggerMap
        } else throw IllegalArgumentException("Incorrect action format! Unexpected: $raw")
    }

}