package cn.fd.ratziel.module.item.command

import cn.fd.ratziel.function.argument.DefaultArgumentFactory
import cn.fd.ratziel.function.argument.PlayerArgument
import cn.fd.ratziel.module.item.impl.ItemManager
import cn.fd.ratziel.module.item.impl.builder.DefaultItemGenerator
import cn.fd.ratziel.module.item.nms.RefItemStack
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.common.platform.function.submit
import taboolib.expansion.createHelper
import taboolib.platform.util.giveItem

/**
 * ItemCommand
 *
 * @author TheFloodDragon
 * @since 2024/5/18 20:27
 */
@CommandHeader(
    name = "r-item",
    aliases = [],
    permission = "ratziel.command.item",
    description = "物品管理命令"
)
object ItemCommand {

    @CommandBody
    val main = mainCommand { createHelper() }

    /**
     * 命令 - 给予物品
     * 用法: /r-item give <id> (player) (amount)
     */
    @CommandBody
    val give = subCommand {
        dynamic("id") {
            suggest { ItemManager.registry.keys.toList() }
            player(optional = true) {
                int("amount", optional = true) {
                    execute<ProxyPlayer> { sender, ctx, _ ->
                        val id = ctx["id"]
                        val amount = ctx.intOrNull("amount") ?: 1
                        val players = ctx.players("player")
                        players.forEach { player ->
                            val element = ItemManager.registry[id] ?: TODO("ERROR")
                            val args = DefaultArgumentFactory().apply {
                                add(PlayerArgument(player))
                            }
                            DefaultItemGenerator.build(element,args).thenAccept {
                                val item = RefItemStack(it.data).getAsBukkit().apply { setAmount(amount) }
                                println(item)
                                submit {
                                    player.cast<Player>().giveItem(item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}