package cn.fd.ratziel.module.item.api.event

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.function.ArgumentContext
import kotlinx.serialization.json.JsonElement

/**
 * ItemResolvedEvent
 *
 * 构建物品中的所有解析阶段完成后触发
 *
 * @author TheFloodDragon
 * @since 2024/7/3 15:15
 */
class ItemResolvedEvent(
    /**
     * 物品标识符
     */
    identifier: Identifier,
    /**
     * 解析出的最终结果
     */
    var result: JsonElement,
    /**
     * 上下文
     */
    val context: ArgumentContext
) : ItemEvent(identifier)