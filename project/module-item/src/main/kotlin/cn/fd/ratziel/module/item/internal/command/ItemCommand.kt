package cn.fd.ratziel.module.item.internal.command

import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.util.toItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.common.platform.function.debug
import taboolib.common.platform.function.submit
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.giveItem
import java.util.concurrent.CompletableFuture
import kotlin.time.TimeSource

/**
 * ItemCommand
 *
 * @author TheFloodDragon
 * @since 2024/5/18 20:27
 */
@CommandHeader(
    name = "r-item",
    aliases = ["ri"],
    permission = "ratziel.command.item",
    description = "物品管理命令"
)
object ItemCommand {

    @CommandBody
    val main = mainCommand { createHelper() }

    /**
     * 命令 - 给予物品
     * 用法: /r-item give <id> [<player>] [<amount>]
     */
    @CommandBody
    val give = subCommand {
        dynamic("id") {
            suggest { ItemManager.registry.map { it.key } }
            execute<ProxyPlayer> { sender, ctx, _ ->
                cmdGive(sender, listOf(sender), ctx["id"], 1)
            }
            player(optional = true) {
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    cmdGive(sender, ctx.players("player"), ctx["id"], 1)
                }
                int("amount", optional = true) {
                    execute<ProxyPlayer> { sender, ctx, _ ->
                        cmdGive(sender, ctx.players("player"), ctx["id"], ctx.int("amount"))
                    }
                }
            }
        }
    }

    private fun giveById(player: Player, id: String, amount: Int): CompletableFuture<ItemStack?> {
        val future = CompletableFuture<ItemStack?>()
        // 获取物品生成器
        val generator = ItemManager.registry[id] ?: return CompletableFuture.completedFuture(null)
        // 上下文参数
        val args = SimpleContext(player)
        // 开始生成物品
        val time = TimeSource.Monotonic.markNow()
        generator.build(args).handle { item, throwable ->
            val duration = time.elapsedNow()
            debug("[TIME MARK] Generated item '$id' in ${duration.inWholeMilliseconds}ms.")
            if (throwable == null) {
                // 将生成结果打包成 BukkitItemStack
                val itemStack = item.toItemStack().apply { setAmount(amount) }
                submit {
                    // 给予物品
                    player.giveItem(itemStack)
                    future.complete(itemStack)
                }
            } else {
                throwable.printStackTrace()
            }
        }
        return future
    }

    private fun cmdGive(sender: ProxyCommandSender, players: List<ProxyPlayer>, id: String, amount: Int) {
        if (players.size == 1) {
            val player = players[0]
            giveById(player.cast(), id, amount).join()
            // 发送给命名发送者
            sender.sendLang("Item-Give", player.name, id, amount)
            // 发送给物品接收者
            if (sender.name != player.name) player.sendLang("Item-Get", id, amount)
        } else {
            val futures = players.map { player ->
                giveById(player.cast(), id, amount).thenRun {
                    // 发送给物品接收者
                    if (sender.name != player.name) player.sendLang("Item-Get", id, amount)
                }
            }
            CompletableFuture.allOf(*futures.toTypedArray()).join()
            sender.sendLang("Item-Give-All", id, amount)
        }
    }


}