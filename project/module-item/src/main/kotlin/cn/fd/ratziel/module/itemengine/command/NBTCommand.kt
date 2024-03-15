package cn.fd.ratziel.module.itemengine.command

import cn.fd.ratziel.common.function.executeAsync
import cn.fd.ratziel.module.item.command.getItemBySlot
import cn.fd.ratziel.module.item.command.slot
import cn.fd.ratziel.module.item.reflex.RefItemStack
import cn.fd.ratziel.module.itemengine.nbt.*
import cn.fd.ratziel.module.itemengine.nbt.NBTCompound.Companion.DEEP_SEPARATION
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.lang.*

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
     * 命令 - 查看 NBT
     * 用法: /nbt view <slot>
     */
    @CommandBody
    val view = subCommand {
        slot {
            executeAsync<ProxyPlayer> { player, _, arg ->
                getItemBySlot(arg, player.cast<Player>().inventory)?.let {
                    RefItemStack(it).getNBT() // 获取物品标签
                }?.also { nbt ->
                    // 构建消息组件并发送
                    nbtAsComponent(player, nbt, 0, arg).sendTo(player)
                } ?: player.sendLang("NBTAction-EmptyTag")
            }
        }
    }

    val NBT_REMOVE_SIGNS = arrayOf(":r", ":rm", ":remove")

    /**
     * 命令 - 编辑 NBT
     * 用法: /nbt edit <slot> <node> <value>
     * 注: 当 <value> 为 [NBT_REMOVE_SIGNS] 时, 删除该节点下的NBT标签
     */
    @CommandBody
    val edit = subCommand {
        slot {
            dynamic {
                dynamic {
                    executeAsync<ProxyPlayer> { player, ctx, _ ->
                        // 获取基本信息
                        val rawNode = ctx.args()[2]
                        val rawValue = ctx.args()[3]
                        val item = getItemBySlot(ctx.args()[1], player.cast<Player>().inventory)
                        // 获取物品标签并进行操作
                        item?.let { RefItemStack(it).getNBT() }?.also {
                            if (NBT_REMOVE_SIGNS.contains(rawNode.lowercase())) {
                                it.removeDeep(rawNode)
                                player.sendLang("NBTAction-Remove", rawNode)
                            } else {
                                val value = NBTMapper.deserializeBasic(rawValue)
                                it.putDeep(rawNode, value)
                                player.sendLang(
                                    "NBTAction-Set",
                                    rawNode, asString(value),
                                    translateType(player, value).toLegacyText()
                                )
                            }
                        } ?: player.sendLang("NBTAction-EmptyTag")
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
                    append(componentKey(sender, deep, slot)) // 添加键
                    append(nbtAsComponent(sender, toNBTData(value), level + 1, slot, deep, isFirst = false))
                    first = false
                }
            }
            /*
            基本类型处理:
            值 (类型)
             */
            else -> append(componentValue(sender, nbt)).append(translateType(sender, nbt))
        }
    }

    /**
     * 快捷创建键或值组件
     */
    fun componentKey(
        sender: ProxyCommandSender,
        nodeDeep: String?,
        slot: String,
        nodeShallow: String? = nodeDeep?.substringAfterLast(DEEP_SEPARATION),
    ) = getTypeJson(sender, "NBTFormat-Entry-Key")
        .buildMessage(sender, nodeShallow.toString(), slot, nodeDeep.toString())

    fun componentValue(sender: ProxyCommandSender, nbt: NBTData) =
        getTypeJson(sender, "NBTFormat-Entry-Value")
            .buildMessage(sender, asString(nbt), NBTMapper.serializeToString(nbt))

    /**
     * 获取语言文件Json内容
     */
    fun getTypeJson(sender: ProxyCommandSender, node: String): TypeJson =
        sender.getLocaleFile()?.nodes?.get(node)?.let { if (it is TypeList) it.list.first() else it } as? TypeJson
            ?: TypeJson().apply { text = listOf("{$node}") }

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
     * 获取 [NBTData] 的字符串形式
     * 注: 除了 [String] 和 [Int], 其它转换成 [String] 都要去掉某位一位
     */
    fun asString(nbt: NBTData): String = nbt.toString().let { str ->
        when (nbt) {
            is NBTString -> nbt.content
            is NBTByte -> str.dropLast(1).toByte().let {
                when (it) {
                    NBTBoolean.BYTE_TRUE -> true.toString()
                    NBTBoolean.BYTE_FALSE -> false.toString()
                    else -> it.toString()
                }
            }

            is NBTInt -> str.toInt().toString()
            is NBTFloat -> str.dropLast(1).toFloat().toString()
            is NBTDouble -> str.dropLast(1).toDouble().toString()
            is NBTLong -> str.dropLast(1).toLong().toString()
            is NBTShort -> str.dropLast(1).toShort().toString()
            // 通解
            else -> NBTMapper.serializeToString(nbt).dropLast(1).drop(1)
        }
    }


}