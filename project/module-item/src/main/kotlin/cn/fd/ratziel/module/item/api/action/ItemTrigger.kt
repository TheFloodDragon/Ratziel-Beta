package cn.fd.ratziel.module.item.api.action

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement

/**
 * ItemTrigger - 物品触发器
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:03
 */
interface ItemTrigger {

    /**
     * 触发器名称
     */
    val name: String

    /**
     * 构建可执行的语句块
     *
     * @param identifier 物品标识符
     * @param element 语句内容
     */
    fun build(identifier: Identifier, element: JsonElement): ExecutableBlock

}