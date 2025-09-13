package cn.fd.ratziel.module.item.internal.command

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtByte
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtList
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.util.modifyTag
import cn.fd.ratziel.module.nbt.NbtAdapter
import cn.fd.ratziel.module.nbt.NbtSerializer
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.write
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
            execute<ProxyPlayer> { player, _, arg ->
                val slot = PlayerInventorySlot.infer(arg) ?: return@execute
                slot.getItemFrom(player.cast())?.modifyTag { tag ->
                    if (tag.isNotEmpty()) {
                        // 构建消息组件并发送
                        nbtAsComponent(player, tag, 0, arg).sendTo(player)
                    } else player.sendLang("NBTAction-EmptyTag")
                } ?: player.sendLang("NBTAction-EmptyTag")
            }
        }
    }

    /**
     * 命令 - 编辑 NBT
     * 用法: /nbt edit <slot> <node> <value>
     */
    @CommandBody
    val edit = subCommand {
        slot {
            dynamic("node") {
                dynamic("value") {
                    execute<ProxyPlayer> { player, ctx, _ ->
                        // 获取基本信息
                        val path = NbtPath(ctx.args()[2])
                        val rawValue = ctx.args()[3]
                        val slot = PlayerInventorySlot.infer(ctx.args()[1]) ?: return@execute
                        slot.getItemFrom(player.cast())?.modifyTag { tag ->
                            val value = NbtSerializer.deserializeFromString(rawValue)
                            tag.write(path, value, true)
                            player.sendLang(
                                "NBTAction-Set",
                                path.toString(), asString(value),
                            )
                        } ?: player.sendLang("NBTAction-EmptyTag")
                    }
                }
            }
        }
    }

    /**
     * 命令 - 删除 NBT
     * 用法: /nbt remove <slot> <node>
     */
    @CommandBody
    val remove = subCommand {
        slot {
            dynamic("node") {
                execute<ProxyPlayer> { player, ctx, _ ->
                    // 获取基本信息
                    val path = NbtPath(ctx.args()[2])
                    val slot = PlayerInventorySlot.infer(ctx.args()[1]) ?: return@execute
                    slot.getItemFrom(player.cast())?.modifyTag { tag ->
                        tag.delete(path)
                        player.sendLang("NBTAction-Remove", path.toString())
                    } ?: player.sendLang("NBTAction-EmptyTag")
                }
            }
        }
    }

    fun nbtAsComponent(
        sender: ProxyCommandSender,
        nbt: NbtTag,
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
            is NbtList -> {
                nbt.content.let { list ->
                    if (list.isEmpty()) append(sender.asLangText("NBTFormat-List-Empty"))
                    else list.forEachIndexed { index, it ->
                        val deep = "$nodeDeep[$index]"
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
            is NbtCompound -> {
                var first = isFirst
                nbt.forEach { (shallow, value) ->
                    val deep = if (nodeDeep == null) shallow else "$nodeDeep.$shallow" // 深层节点的合成
                    if (!first) {
                        newLine(); repeat(level) { append(retractComponent) } // 缩进
                    }
                    append(componentKey(sender, deep, slot)) // 添加键
                    append(nbtAsComponent(sender, NbtAdapter.box(value), level + 1, slot, deep, isFirst = false))
                    first = false
                }
            }
            /*
            基本类型处理:
            值 (类型)
             */
            else -> append(componentValue(sender, nbt))
        }
    }

    /**
     * 快捷创建键或值组件
     */
    private fun componentKey(
        sender: ProxyCommandSender,
        nodeDeep: String?,
        slot: String,
        nodeShallow: String? = nodeDeep?.substringAfterLast("."),
    ) = getTypeJson(sender, "NBTFormat-Entry-Key")
        .buildMessage(sender, nodeShallow.toString(), slot, nodeDeep.toString())

    private fun componentValue(sender: ProxyCommandSender, nbt: NbtTag) =
        getTypeJson(sender, "NBTFormat-Entry-Value")
            .buildMessage(sender, asString(nbt), NbtSerializer.serializeToString(nbt))

    /**
     * 获取语言文件Json内容
     */
    private fun getTypeJson(sender: ProxyCommandSender, node: String): TypeJson =
        sender.getLocaleFile()?.nodes?.get(node)?.let { if (it is TypeList) it.list.first() else it } as? TypeJson
            ?: TypeJson().apply { text = listOf("{$node}") }

    /**
     * 获取 [NbtTag] 的字符串形式
     */
    private fun asString(nbt: NbtTag): String = when (nbt) {
        is NbtByte -> (NbtByte(nbt.content).toBoolean() ?: nbt.content).toString()
        else -> NbtSerializer.serializeToString(nbt)
    }

}