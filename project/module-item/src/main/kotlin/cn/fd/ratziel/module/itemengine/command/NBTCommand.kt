package cn.fd.ratziel.module.itemengine.command

import cn.fd.ratziel.bukkit.command.inferSlotToInt
import cn.fd.ratziel.bukkit.command.slot
import cn.fd.ratziel.common.util.asComponent
import cn.fd.ratziel.common.util.getType
import cn.fd.ratziel.module.itemengine.nbt.*
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound.Companion.DEEP_SEPARATION
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
import taboolib.module.lang.TypeList
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

    fun nbtAsComponent(
        sender: ProxyCommandSender, nbt: NBTData, level: Int, slot: Int, nodeDeep: String? = null,
    ): ComponentText = Components.empty().apply {
        // 获取格式
        val retractComponent = sender.asLangText("NBTFormat-Retract")
        /*
        列表类型特殊处理:
        - 键1:值1 (类型)
        或
        - 值2 (类型)
         */
        when (nbt) {
            is NBTList -> {
                var first = true
                nbt.content.forEach {
                    if (!first) newLine() else first = false
                    repeat(level) { append(retractComponent) } // 缩进
                    append(sender.asLangText("NBTFormat-Retract-List")) // 列表前缀
                    append(nbtAsComponent(sender, it, level + 1, slot, nodeDeep))
                }
            }
            /*
            复合类型特殊处理:
            键1:值1 (类型)
            键2:值2 (类型)
             */
            is NBTCompound -> {
                var first = true
                nbt.toMapShallow().forEach { (shallow, value) ->
                    val deep = if (nodeDeep == null) shallow else nodeDeep + DEEP_SEPARATION + shallow // 深层节点的合成
                    if (!first) newLine() else first = false
                    append(componentKey(sender, deep)) // 添加键
                    repeat(level) { append(retractComponent) } // 缩进
                    append(nbtAsComponent(sender, toNBTData(value), level + 1, slot, deep))
                }
            }
            /*
            基本类型处理:
            值 (类型)
             */
            else -> append(componentValue(sender, nbt, slot, nodeDeep)).append(translateType(sender, nbt))
        }
    }

    /**
     * 快捷创建键或值组件
     */

    fun componentKey(
        sender: ProxyCommandSender,
        nodeDeep: String?,
        nodeShallow: String? = nodeDeep?.substringAfter(DEEP_SEPARATION),
    ) = unsafeTypeJson(sender, "NBTFormat-Entry-Key").asComponent(sender, nodeShallow.toString(), nodeDeep.toString())

    fun componentValue(sender: ProxyCommandSender, nbt: NBTData, slot: Int, nodeDeep: String?) =
        unsafeTypeJson(sender, "NBTFormat-Entry-Value").asComponent(sender, nbt.toString(), slot, nodeDeep.toString())

    /**
     * 获取语言文件Json内容 (不安全)
     */
    fun unsafeTypeJson(sender: ProxyCommandSender, node: String) = sender.getLocaleFile()?.getType(node).let {
        if (it is TypeList) it.list.first() else it
    } as TypeJson

    /**
     * 快捷匹配类型组件
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

}