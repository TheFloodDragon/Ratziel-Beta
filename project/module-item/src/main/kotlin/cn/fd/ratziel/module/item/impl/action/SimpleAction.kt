package cn.fd.ratziel.module.item.impl.action

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.module.item.api.action.ItemAction
import cn.fd.ratziel.module.script.block.ExecutableBlock
import kotlinx.serialization.json.JsonElement

/**
 * SimpleAction
 *
 * @author TheFloodDragon
 * @since 2025/5/2 12:10
 */
class SimpleAction(
    /**
     * 动作内容
     */
    override val content: JsonElement,
    /**
     * 动作语句块
     */
    val block: ExecutableBlock,
) : ItemAction {

    override fun execute(context: ArgumentContext) {
        block.execute(context)
    }

    override fun toString() = "SimpleAction(block=$block, content=$content)"

}