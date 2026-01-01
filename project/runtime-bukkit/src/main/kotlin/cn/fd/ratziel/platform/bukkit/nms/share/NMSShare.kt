package cn.fd.ratziel.platform.bukkit.nms.share

/**
 * NMSShare
 *
 * @author TheFloodDragon
 * @since 2025/12/30 22:37
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NMSShare(val version: Int = 0)
