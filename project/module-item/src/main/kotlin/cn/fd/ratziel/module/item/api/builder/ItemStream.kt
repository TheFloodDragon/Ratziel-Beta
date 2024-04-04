package cn.fd.ratziel.module.item.api.builder

/**
 * ItemStream
 *
 * @author TheFloodDragon
 * @since 2024/4/4 19:49
 */
interface ItemStream {

    /**
     * 物品序列化器集合
     */
    val serializers: Array<ItemSerializer<*, *>>


}