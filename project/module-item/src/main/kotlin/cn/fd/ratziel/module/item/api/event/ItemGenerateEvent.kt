package cn.fd.ratziel.module.item.api.event

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import kotlinx.serialization.json.JsonElement
import taboolib.platform.type.BukkitProxyEvent
import java.util.*
import java.util.function.BiConsumer

/**
 * ItemGenerateEvent
 *
 * @author TheFloodDragon
 * @since 2024/7/3 14:26
 */
class ItemGenerateEvent {

    /**
     * 物品生成之前触发
     */
    class Pre(
        /**
         * 物品标识符
         */
        identifier: Identifier,
        /**
         * 构建物品的生成器
         */
        val generator: ItemGenerator,
        /**
         * 构建时的参数列表
         */
        val context: ArgumentContext
    ) : ItemEvent(identifier)

    /**
     * 物品生成结束后触发
     */
    class Post(
        /**
         * 物品标识符
         */
        identifier: Identifier,
        /**
         * 构建物品的生成器
         */
        val generator: ItemGenerator,
        /**
         * 构建出的成品
         */
        var item: NeoItem,
        /**
         * 构建时的参数列表
         */
        val context: ArgumentContext
    ) : ItemEvent(identifier)

    /**
     * 用于自定义生成任务
     */
    @Deprecated("Necessary?")
    class TaskAdder(
        /**
         * 扩展任务列表
         */
        val extendTasks: MutableList<BiConsumer<JsonElement, ItemData>> = LinkedList(),
    ) : BukkitProxyEvent()

}