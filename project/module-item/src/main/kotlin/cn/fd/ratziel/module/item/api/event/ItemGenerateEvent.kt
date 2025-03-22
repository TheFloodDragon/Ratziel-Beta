package cn.fd.ratziel.module.item.api.event

import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.api.ItemData
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
        val element: JsonElement,
        /**
         * 物品数据
         */
        val data: ItemData
    ) : ItemGenerateEvent(identifier, generator, context)

    /**
     * 通过组件生成 [NbtTag] 后触发
     */
    class DataGenerate(
        identifier: Identifier,
        generator: ItemGenerator,
        context: ArgumentContext,
        /**
         * 组件的类型
         */
        val componentType: Class<*>,
        /**
         * 生成的 [NbtTag]
         */
        var generatedTag: NbtTag?
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