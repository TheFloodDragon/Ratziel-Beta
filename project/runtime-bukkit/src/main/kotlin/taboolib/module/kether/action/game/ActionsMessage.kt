package taboolib.module.kether.action.game

import cn.fd.ratziel.common.adventure.castAudience
import cn.fd.ratziel.common.adventure.sendActionBar
import cn.fd.ratziel.common.adventure.sendMessage
import cn.fd.ratziel.common.adventure.sendTitle
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.chat.colored
import taboolib.module.chat.uncolored
import taboolib.module.kether.KetherParser
import taboolib.module.kether.combinationParser
import taboolib.module.kether.player
import taboolib.module.kether.script

/**
 * ActionsMessage
 *
 * @author TheFloodDragon
 * @since 2023/10/2 10:22
 */
internal object ActionsMessage {

    @KetherParser(["tell", "send", "message"])
    fun actionTell() = combinationParser {
        it.group(text()).apply(it) { str ->
            now { script().sender?.castAudience?.sendMessage(str.replace("@sender", script().sender?.name.toString())) ?: error("No sender") }
        }
    }

    @KetherParser(["actionbar"])
    fun actionActionBar() = combinationParser {
        it.group(text()).apply(it) { str ->
            now { player().castAudience.sendActionBar(str.replace("@sender", script().sender?.name.toString())) }
        }
    }

    @KetherParser(["broadcast", "bc"])
    fun actionBroadcast() = combinationParser {
        it.group(text()).apply(it) { str ->
            now { onlinePlayers().forEach { p -> p.castAudience.sendMessage(str.replace("@sender", script().sender?.name.toString())) } }
        }
    }

    @KetherParser(["title"])
    fun actionTitle() = combinationParser {
        it.group(
            text(),
            command("subtitle", then = text()).option(),
            command("by", "with", then = int().and(int(), int())).option().defaultsTo(Triple(0, 20, 0))
        ).apply(it) { t1, t2, time ->
            val (i, s, o) = time
            now { player().castAudience.sendTitle(t1.replace("@sender", player().name), t2?.replace("@sender", player().name) ?: "§r", i, s, o) }
        }
    }

    @KetherParser(["subtitle"])
    fun actionSubtitle() = combinationParser {
        it.group(
            text(),
            command("by", "with", then = int().and(int(), int())).option().defaultsTo(Triple(0, 20, 0))
        ).apply(it) { text, time ->
            val (i, s, o) = time
            now { player().castAudience.sendTitle("§r", text.replace("@sender", player().name), i, s, o) }
        }
    }

    /**
     * TODO 是否要保留
     */
    @KetherParser(["color", "colored"])
    fun actionColor() = combinationParser {
        it.group(text()).apply(it) { str -> now { str.colored() } }
    }

    @Suppress("SpellCheckingInspection")
    @KetherParser(["uncolor", "uncolored"])
    fun actionUncolored() = combinationParser {
        it.group(text()).apply(it) { str -> now { str.uncolored() } }
    }

}