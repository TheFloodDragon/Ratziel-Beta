package cn.fd.ratziel.folia.lib

import org.bukkit.plugin.Plugin

/**
 * ProxyTask
 *
 * @author TheFloodDragon
 * @since 2023/9/23 12:11
 */
interface ProxyTask {
    fun getOwningPlugin(): Plugin?
    fun cancel()
    fun isCancelled(): Boolean
    fun isTimerTask(): Boolean
    fun isAsyncTask(): Boolean
}