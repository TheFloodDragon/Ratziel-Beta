package taboolib.module.kether.action.game

import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.platformLocation
import taboolib.common.util.Location
import taboolib.common.util.isConsole
import taboolib.module.kether.*

internal object Actions {

    @KetherParser(["perm", "permission"])
    fun actionPermission() = combinationParser {
        it.group(text()).apply(it) { perm -> now { player().hasPermission(perm) } }
    }

    @KetherParser(["players"])
    fun actionPlayers() = scriptParser {
        actionNow { onlinePlayers().map { it.name } }
    }

    @KetherParser(["sender"])
    fun actionSender() = scriptParser {
        actionNow { if (script().sender.isConsole()) "console" else script().sender?.name.toString() }
    }

    @KetherParser(["switch"])
    fun actionSwitch() = combinationParser {
        it.group(text()).apply(it) { to ->
            now { script().sender = if (to == "console" || to == "server") console() else getProxyPlayer(to) }
        }
    }

    @KetherParser(["loc", "location"])
    fun actionLocation() = combinationParser {
        it.group(
            text(),
            double(),
            double(),
            double(),
            command("and", then = float().and(float())).option().defaultsTo(0f to 0f)
        ).apply(it) { world, x, y, z, (yaw, pitch) ->
            now { platformLocation<Any>(Location(world, x, y, z, yaw, pitch)) }
        }
    }

    @PlatformSide([Platform.BUKKIT])
    @KetherParser(["sound"])
    fun actionSound() = combinationParser {
        it.group(
            text(),
            command("by", "with", then = float().and(float())).option().defaultsTo(0f to 0f)
        ).apply(it) { sound, vp ->
            val (v, p) = vp
            now {
                if (sound.startsWith("resource:")) {
                    player().playSoundResource(player().location, sound.substringAfter("resource:"), v, p)
                } else {
                    player().playSound(player().location, sound.replace('.', '_').uppercase(), v, p)
                }
            }
        }
    }
}