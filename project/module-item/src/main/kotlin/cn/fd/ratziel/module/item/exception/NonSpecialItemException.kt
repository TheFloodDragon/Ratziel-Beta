package cn.fd.ratziel.module.item.exception

/**
 * NonSpecialItemException
 * 在校验物品标签中的信息时, 校验失败(不是特殊物品-RatzielItem)时抛出
 *
 * @author TheFloodDragon
 * @since 2024/1/28 14:13
 */
class NonSpecialItemException() : IllegalStateException() {

    override var message = "It isn't a Special Item!"

    constructor(data: String) : this() {
        message = "It isn't a Special Item! Unsupported data '$data'!"
    }

}