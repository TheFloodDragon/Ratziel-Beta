package cn.fd.fdutilities.util

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.console
import taboolib.common5.mirrorNow
import taboolib.library.kether.LocalizedException
import taboolib.module.kether.KetherShell
import taboolib.module.lang.sendLang
import java.util.concurrent.CompletableFuture

object KetherParser {

    @JvmStatic
    fun Player.eval(script: String): CompletableFuture<Any?> {
        return mirrorNow("Handler:Script:Evaluation") {
            return@mirrorNow try {
                KetherShell.eval(script, namespace = listOf("fdutilities")) {
                    sender = adaptPlayer(this@eval)
                }
            } catch (e: LocalizedException) {
                e.localizedMessage.split("\n").forEach {
                    console().sendLang("Kether-Shell-Exception", it)
                }
                CompletableFuture.completedFuture(false)
            }
        }
    }

}