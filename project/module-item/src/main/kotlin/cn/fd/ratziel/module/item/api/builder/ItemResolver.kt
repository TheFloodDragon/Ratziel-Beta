package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.DefaultArgumentFactory
import cn.fd.ratziel.module.item.api.Resolver
import kotlinx.serialization.json.JsonElement

/**
 * ItemResolver - 物品配置解析
 *
 * @author TheFloodDragon
 * @since 2024/4/14 12:01
 */
interface ItemResolver : Resolver<JsonElement, JsonElement> {

    /**
     * 解析元素 (带参数)
     */
    fun resolve(element: JsonElement, arguments: ArgumentFactory): JsonElement

    /**
     * 解析元素 (带空参数)
     */
    override fun resolve(element: JsonElement): JsonElement = resolve(element, DefaultArgumentFactory())

}