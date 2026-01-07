package cn.fd.ratziel.platform.bukkit.nms.share

/**
 * NMSShare - 无分组式 NMS 使用共享
 *
 * @author TheFloodDragon
 * @since 2025/12/30 22:37
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NMSShare(val version: Int = 0)
