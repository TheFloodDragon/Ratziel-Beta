package cn.fd.ratziel.module.item.internal.command

import cn.fd.ratziel.core.contextual.SimpleContext
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.feature.update.ItemUpdate
import cn.fd.ratziel.module.item.impl.RatzielItem
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

    /**
     * 命令 - 更新物品
     * 用法: /r-item update [<player>] [hand|all]
     * - 默认为执行者主手 (hand)
     * - 参数 'all' 会尝试更新玩家背包内的所有物品
     */
    @CommandBody
    val update = subCommand {
        // 可选玩家参数
        player(optional = true) {
            // 可选模式参数 (hand/all)
            dynamic("mode", optional = true) {
                suggest { listOf("hand", "all") }
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    cmdUpdate(sender, ctx.players("player").first().cast(), ctx.getOrNull("mode") ?: "hand")
                }
            }
            execute<ProxyCommandSender> { sender, ctx, _ ->
                cmdUpdate(sender, ctx.players("player").first().cast(), "hand")
            }
        }
        // 如果未提供 player, 支持直接提供 mode (作为第一个参数)
        dynamic("mode", optional = true) {
            suggest { listOf("hand", "all") }
            execute<ProxyPlayer> { sender, ctx, _ ->
                cmdUpdate(sender, sender.cast(), ctx.getOrNull("mode") ?: "hand")
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
            giveById(player.cast(), id, amount).thenRun {
                // 发送给命名发送者
                sender.sendLang("Item-Give", player.name, id, amount)
                // 发送给物品接收者
                if (sender.name != player.name) player.sendLang("Item-Get", id, amount)
            }
        } else {
            val futures = players.map { player ->
                giveById(player.cast(), id, amount).thenRun {
                    // 发送给物品接收者
                    if (sender.name != player.name) player.sendLang("Item-Get", id, amount)
                }
            }
            CompletableFuture.allOf(*futures.toTypedArray())
                .thenRun { sender.sendLang("Item-Give-All", id, amount) }
        }
    }

    // 新增：更新物品的实现
    private fun cmdUpdate(sender: ProxyCommandSender, player: Player, mode: String) {
        if (mode.equals("all", ignoreCase = true)) {
            var successCount = 0
            // 遍历玩家背包，包括主手/副手
            val inv = player.inventory
            val items = inv.contents.filterNotNull()
            for (item in items) {
                val sourced = RatzielItem.sourced(item, true) ?: continue
                val update = sourced.service[ItemUpdate::class.java] ?: continue
                val success = update.update(sourced, player)
                if (success) successCount++
            }
            sender.sendLang("Item-Update-All-Result", player.name, successCount)
        } else {
            // 默认处理主手物品
            val itemInHand = player.inventory.itemInMainHand
            val sourced = RatzielItem.sourced(itemInHand, true)
            if (sourced == null) {
                sender.sendLang("Item-Update-NotFound")
                return
            }
            val update = sourced.service[ItemUpdate::class.java]
            if (update == null) {
                sender.sendLang("Item-Update-Disabled", sourced.identifier.content)
                return
            }
            val success = update.update(sourced, player)
            if (success) {
                // 覆写已在 update 内部执行
                sender.sendLang("Item-Update-Success", player.name, sourced.identifier.content)
            } else {
                sender.sendLang("Item-Update-Failed", player.name, sourced.identifier.content)
            }
        }
    }

}