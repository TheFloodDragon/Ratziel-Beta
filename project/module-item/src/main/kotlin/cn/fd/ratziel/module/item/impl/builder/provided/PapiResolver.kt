package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.popOrNull
import cn.fd.ratziel.module.item.api.builder.SectionTagResolver
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.compat.replacePlaceholder

/**
 * PapiResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/16 19:26
 */
object PapiResolver : SectionTagResolver {

    override val names = arrayOf("papi", "p")

    override fun resolve(element: List<String>, context: ArgumentContext): String? {
        // 获取玩家
        val player = player(context) ?: return null
        // 读取内容
        val content = when {
            element.size == 1 -> element.first()
            element.size > 1 -> element.joinToString("_")
            else -> return null
        }
        // 处理Papi变量
        return content.replacePlaceholder(player)
    }

    fun player(context: ArgumentContext) =
        context.popOrNull<ProxyPlayer>()?.cast() ?: context.popOrNull<OfflinePlayer>()

}