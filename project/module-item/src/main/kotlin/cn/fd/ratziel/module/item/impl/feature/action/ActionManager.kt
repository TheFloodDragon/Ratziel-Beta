package cn.fd.ratziel.module.item.impl.feature.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.core.serialization.toBasic
import cn.fd.ratziel.module.item.api.feature.ItemAction
import cn.fd.ratziel.module.item.event.ItemResolvedEvent
import cn.fd.ratziel.module.item.impl.service.NativeServiceRegistry
import cn.fd.ratziel.script.ScriptBlockBuilder
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.severe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ActionManager
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:20
 */
object ActionManager {

    /**
     * 物品动作类型注册表
     */
    val registry: MutableSet<ItemAction.ActionType> = CopyOnWriteArraySet()

    /**
     * 物品唯一标识符 - 物品动作表
     */
    val map: MutableMap<Identifier, ItemAction.ActionMap> = ConcurrentHashMap()

    init {
        // 注册服务
        NativeServiceRegistry.register(ItemAction.ActionMap::class.java, { map[it] }, { id, m -> map[id] = m })
    }

    /**
     * 匹配 [ItemAction.ActionType]
     */
    fun matchType(name: String): ItemAction.ActionType? = registry.firstOrNull { it.name == name || it.alias.contains(name) }

    /**
     * 从配置中读取动作原数据
     */
    fun read(rawActions: JsonObject): ItemAction.ActionMap {
        val map = ItemAction.ActionMap()
        for (raw in rawActions) {
            // 匹配类型
            val type = matchType(raw.key)
            if (type == null) {
                severe("Unknown action type: \"${raw.key}\" !")
                continue
            }
            // 创建脚本块
            val block = ScriptBlockBuilder.build(raw.value.toBasic())
            // 设置
            map[type] = ScriptedAction(block)
        }
        return map
    }

    val nodeNames = arrayOf("action", "actions", "act", "acts")

    @SubscribeEvent
    fun listen(event: ItemResolvedEvent) {
        val element = event.result as? JsonObject ?: return
        // 获取动作
        val rawActions = element.getBy(nodeNames.asIterable()) as? JsonObject ?: throw IllegalArgumentException("Incorrect action format!")
        // 读取并注册
        this.map[event.identifier] = read(rawActions)
    }

}