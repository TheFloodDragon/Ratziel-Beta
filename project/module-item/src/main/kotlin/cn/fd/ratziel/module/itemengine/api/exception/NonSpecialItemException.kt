package cn.fd.ratziel.module.itemengine.api.exception

import cn.fd.ratziel.module.itemengine.nbt.NBTData

/**
 * NonSpecialItemException
 * 在校验物品标签中的信息时, 校验失败(不是特殊物品-RatzielItem)时抛出
 *
 * @author TheFloodDragon
 * @since 2024/1/28 14:13
 */
class NonSpecialItemException internal constructor(message: String) : IllegalStateException(message) {

    constructor() : this("It isn't a Special Item!")

    constructor(data: NBTData) : this("It isn't a Special Item! Unsupported Data '$data'!")

}