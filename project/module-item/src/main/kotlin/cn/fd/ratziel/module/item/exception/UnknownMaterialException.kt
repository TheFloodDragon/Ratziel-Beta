package cn.fd.ratziel.module.item.exception

/**
 * UnknownMaterialException
 * 在获取 [org.bukkit.Material] 时, 无法找到对应名称的材料时抛出
 *
 * @author TheFloodDragon
 * @since 2024/1/29 13:33
 */
class UnknownMaterialException() : IllegalArgumentException() {

    override var message = "Unknown Material!"

    constructor(name: String) : this() {
        message = "Unknown Material Name: '$name' !"
    }

    constructor(id: Int) : this() {
        message = "Unknown Material Id: '$id' !"
    }

}