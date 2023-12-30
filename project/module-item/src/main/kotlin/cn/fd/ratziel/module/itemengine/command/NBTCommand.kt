package cn.fd.ratziel.module.itemengine.command

import cn.fd.ratziel.bukkit.command.getItemBySlot
import cn.fd.ratziel.bukkit.command.slot
import cn.fd.ratziel.common.util.asComponent
import cn.fd.ratziel.common.util.getType
import cn.fd.ratziel.module.itemengine.nbt.*
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound.Companion.DEEP_SEPARATION
import cn.fd.ratziel.module.itemengine.util.mapping.RefItemStack
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
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
     * 查看 NBT
     */
    @CommandBody
    val view = subCommand {
        slot {
            execute<ProxyPlayer> { player, _, arg ->
                getItemBySlot(arg, player.cast<Player>().inventory)?.let {
                    RefItemStack(it).getNBT() // 获取物品标签
                }?.also { nbt ->
                    // 构建消息组件并发送
                    nbtAsComponent(player, nbt, 0, arg).sendTo(player)
                }
            }
        }
    }

    /**
     * 编辑 NBT
     * TODO 未完成
     */
    @CommandBody
    val edit = subCommand {
        slot {
            literal("node") {
                literal("value") {
                    execute<ProxyCommandSender> { player, ctx, arg ->
                        println(ctx)
                        println(ctx.args())
                        println(arg)
                    }
                }
            }
        }
    }

    fun nbtAsComponent(
        sender: ProxyCommandSender,
        nbt: NBTData,
        level: Int,
        slot: String,
        nodeDeep: String? = null,
        isFirst: Boolean = true,
    ): ComponentText = Components.empty().apply {
        val retractComponent = sender.asLangText("NBTFormat-Retract")
        /*
        列表类型特殊处理:
        - 键1:值1 (类型)
        或
        - 值2 (类型)
         */
        when (nbt) {
            is NBTList -> {
                nbt.content.let { list ->
                    if (list.isEmpty()) append(sender.asLangText("NBTFormat-List-Empty"))
                    else list.forEachIndexed { index, it ->
                        val deep = nodeDeep + NBTTag.LIST_INDEX_START + index + NBTTag.LIST_INDEX_END
                        newLine(); repeat(level) { append(retractComponent) } // 缩进
                        append(sender.asLangText("NBTFormat-Retract-List")) // 列表前缀
                        append(nbtAsComponent(sender, it, level + 1, slot, deep))
                    }
                }
            }
            /*
            复合类型特殊处理:
            键1:值1 (类型)
            键2:值2 (类型)
             */
            is NBTCompound -> {
                var first = isFirst
                nbt.toMapShallow().forEach { (shallow, value) ->
                    val deep = if (nodeDeep == null) shallow else nodeDeep + DEEP_SEPARATION + shallow // 深层节点的合成
                    if (!first) {
                        newLine(); repeat(level) { append(retractComponent) } // 缩进
                    }
                    append(componentKey(sender, deep)) // 添加键
                    append(nbtAsComponent(sender, toNBTData(value), level + 1, slot, deep, isFirst = false))
                    first = false
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
        nodeShallow: String? = nodeDeep?.substringAfterLast(DEEP_SEPARATION),
    ) = unsafeTypeJson(sender, "NBTFormat-Entry-Key").asComponent(sender, nodeShallow.toString(), nodeDeep.toString())

    fun componentValue(sender: ProxyCommandSender, nbt: NBTData, slot: String, nodeDeep: String?) =
        unsafeTypeJson(sender, "NBTFormat-Entry-Value").asComponent(sender, asString(nbt), slot, nodeDeep.toString())

    /**
     * 获取语言文件Json内容 (不安全)
     */
    fun unsafeTypeJson(sender: ProxyCommandSender, node: String) =
        sender.getLocaleFile()?.getType(node).let { if (it is TypeList) it.list.first() else it } as TypeJson

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
        else -> null
    }?.let { Components.parseSimple(it).build { colored() } } ?: Components.empty()


    /**
     * 获取NBTData的字符串形式
     */
    fun asString(nbt: NBTData): String = nbt.toString().let { str ->
        when (nbt) {
            is NBTString -> nbt.content
            is NBTByte -> NumberConversions.toByte(str).let {
                when (it) {
                    NBTBoolean.byteTrue -> true.toString()
                    NBTBoolean.byteFalse -> false.toString()
                    else -> it.toString()
                }
            }

            is NBTInt -> NumberConversions.toInt(str).toString()
            is NBTFloat -> NumberConversions.toFloat(str).toString()
            is NBTDouble -> NumberConversions.toDouble(str).toString()
            is NBTLong -> NumberConversions.toLong(str).toString()
            is NBTShort -> NumberConversions.toShort(str).toString()
            else -> str
        }
    }


}