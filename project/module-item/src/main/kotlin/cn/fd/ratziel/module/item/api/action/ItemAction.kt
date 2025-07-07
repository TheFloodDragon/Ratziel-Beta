package cn.fd.ratziel.module.item.api.action

import cn.fd.ratziel.core.functional.ArgumentContext
import kotlinx.serialization.json.JsonElement

/**
 * ItemAction - 物品动作
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:01
 */
interface ItemAction {

    /**
     * 动作内容
     */
    val content: JsonElement

    /**
     * 执行动作
     * @param context 动作参数
     */
    fun execute(context: ArgumentContext)

}