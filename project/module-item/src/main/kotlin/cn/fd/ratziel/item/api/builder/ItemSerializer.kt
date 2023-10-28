package cn.fd.ratziel.item.api.builder

import cn.fd.ratziel.item.api.ItemPart
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * ItemSerializer - 用于从配置文件中序列成物品
 *
 * @author TheFloodDragon
 * @since 2023/10/28 13:24
 */
interface ItemSerializer {

    /**
     * 通过Json序列化器序列化
     */
    fun serializeByJson(json: Json, element: JsonElement): ItemPart?

}