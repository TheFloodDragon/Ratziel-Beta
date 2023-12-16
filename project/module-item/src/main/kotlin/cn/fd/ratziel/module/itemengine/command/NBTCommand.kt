package cn.fd.ratziel.module.itemengine.command

import cn.fd.ratziel.bukkit.command.inferSlotToInt
import cn.fd.ratziel.bukkit.command.slot
import cn.fd.ratziel.common.util.asComponent
import cn.fd.ratziel.common.util.getType
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound.Companion.DEEP_SEPARATION
import cn.fd.ratziel.module.itemengine.nbt.NBTData
import cn.fd.ratziel.module.itemengine.nbt.NBTDataType
import cn.fd.ratziel.module.itemengine.nbt.NBTList
import cn.fd.ratziel.module.itemengine.util.mapping.RefItemStack
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.lang.TypeJson
import taboolib.module.lang.asLangText
import taboolib.module.lang.getLocaleFile
import taboolib.platform.util.sendLang

/**
 * NBTCommand
 *
 * @author TheFloodDragon
 * @since 2023/12/15 21:39
 */
@CommandHeader(
    name = "r-nbt",
    aliases = ["nbt"],
    permission = "ratziel.command.nbt",
    description = "物品NBT管理命令"
)
@Deprecated("操你妈什么傻逼玩意写你妈")
object NBTCommand {

    @CommandBody
    val main = mainCommand { createHelper() }


    /**
     * 查看NBT
     */
    @CommandBody
    val view = subCommand {
        slot {
            execute<Player> { sender, _, arg ->
                sender.inventory.getItem(inferSlotToInt(arg))?.let {
                    RefItemStack.tagFromObc(it) // 获取物品标签
                }.also {
                    sender.sendLang("NBTFormat-Entry-Value", it.toString())
                }
            }
        }

    }

    /**
     * 构建组件
     */
    fun buildComponent(sender: ProxyCommandSender, nbt: NBTCompound, base: ComponentText = Components.empty()) =
        base.apply {
            val formatKey = getTypeJson(sender, "NBTFormat-Entry-Key")
            val formatValue = getTypeJson(sender, "NBTFormat-Entry-Value")
            val formatRetract = getTypeJson(sender, "NBTFormat-Retract")
            nbt.toMapDeep().forEach { parent ->
                // 如果为深层节点
                if (parent.key.contains(DEEP_SEPARATION)) {

                } else { // 单层节点
                    newLine() // 换行
                    append(formatKey.asComponent(sender, parent.key))
                    append(formatValue.asComponent(sender, parent.value.toString()))
                    append(translateType(sender, parent.value))
                }
            }
        }

    /**
     * 类型组件
     */
    fun translateType(sender: ProxyCommandSender, nbt: NBTData): ComponentText = when (nbt.type) {
        NBTDataType.STRING -> sender.asLangText("NBTFormat-Type-String")
        NBTDataType.BYTE -> sender.asLangText("NBTFormat-Type-Byte")
        NBTDataType.SHORT -> sender.asLangText("NBTFormat-Type-Short")
        NBTDataType.INT -> sender.asLangText("NBTFormat-Type-Int")
        NBTDataType.LONG -> sender.asLangText("NBTFormat-Type-Long")
        NBTDataType.FLOAT -> sender.asLangText("NBTFormat-Type-Float")
        NBTDataType.DOUBLE -> sender.asLangText("NBTFormat-Type-Double")
        NBTDataType.BYTE_ARRAY -> sender.asLangText("NBTFormat-Type-ByteArray")
        NBTDataType.INT_ARRAY -> sender.asLangText("NBTFormat-Type-IntArray")
        NBTDataType.LONG_ARRAY -> sender.asLangText("NBTFormat-Type-LongArray")
        NBTDataType.COMPOUND -> sender.asLangText("NBTFormat-Type-Compound")
        NBTDataType.LIST -> sender.asLangText("NBTFormat-Type-List")
        else -> null
    }?.let { Components.parseSimple(it).build { colored() } } ?: Components.empty()
//
//
//    fun formatKey(sender: ProxyCommandSender) = getTypeJson(sender, "NBTFormat-Entry-Key")
//    fun formatValue(sender: ProxyCommandSender) =getTypeJson(sender, "NBTFormat-Entry-Value")

    fun getTypeJson(sender: ProxyCommandSender, node: String) = sender.getLocaleFile()?.getType(node) as TypeJson

    fun nbtAsComponent(sender: ProxyCommandSender, nbt: NBTData, level: Int): ComponentText = Components.empty().apply {
        // 获取格式
        val formatKey = getTypeJson(sender, "NBTFormat-Entry-Key")
        val formatValue = getTypeJson(sender, "NBTFormat-Entry-Value")
        val formatRetract = getTypeJson(sender, "NBTFormat-Retract")
        // 遇事不决先换行
        newLine(); repeat(level) { this.append(sender.asLangText("NBTFormat-Retract")) }
        // 具体解析
        when (nbt) {
            is NBTList -> {
                nbt.content.forEach {
                    this.append(sender.asLangText("NBTFormat-Retract-List"))
                    this.append(nbtAsComponent(sender, it, level))
                }
            }

            is NBTCompound -> {
                /* 一开始
                Test:
                  cnm: 1
                  rnm: "???"
                  anm:
                    a: 2
                    b:
                      c: "fw"
                 */
                nbt.toMapDeep().forEach { deep ->
                    /* 到这后
                    Test.cnm: 1
                    Test.rnm: "???"
                    Test.anm.a: 2
                    Test.anm.b.c: "fw"
                     */
                    append(formatKey.asComponent(sender, deep.key, deep.key))
                }
            }
        }
    }

}