package cn.fd.ratziel.module.item.api.event

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import kotlinx.serialization.json.JsonElement

/**
 * ItemGenerateEvent
 *
 * @author TheFloodDragon
 * @since 2024/7/3 14:26
 */
open class ItemGenerateEvent(
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
) : ItemEvent(identifier) {

    /**
     * 物品生成之前触发
     * 此时物品标识符已生成
     */
    class Pre(
        identifier: Identifier,
        generator: ItemGenerator,
        context: ArgumentContext,
        /**
         * 物品元素
         */
        val element: JsonElement
    ) : ItemGenerateEvent(identifier, generator, context)

    /**
     * 物品生成结束后触发
     */
    class Post(
        identifier: Identifier,
        generator: ItemGenerator,
        context: ArgumentContext,
        /**
         * 构建出的成品
         */
        var item: NeoItem
    ) : ItemGenerateEvent(identifier, generator, context)

}