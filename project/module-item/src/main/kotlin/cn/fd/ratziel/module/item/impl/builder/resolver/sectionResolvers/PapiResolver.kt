package cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers

import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.function.popOrNull
import cn.fd.ratziel.module.item.api.builder.SectionResolver
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.compat.replacePlaceholder

/**
 * PapiResolver
 *
 * @author TheFloodDragon
 * @since 2024/6/25 20:18
 */
@Deprecated("Will be removed")
object PapiResolver : SectionResolver.StringResolver {

    override fun resolve(element: String, context: ArgumentContext): String {
        // 获取玩家 (没玩家处理啥？)
        val player = player(context) ?: return element
        // 处理Papi变量
        return element.replacePlaceholder(player)
    }

    private fun player(context: ArgumentContext) = context.popOrNull<ProxyPlayer>()?.cast() ?: context.popOrNull<OfflinePlayer>()

    object TagResolver : SectionResolver.TagResolver {

        override val names = arrayOf("papi")

        override fun resolve(element: Iterable<String>, context: ArgumentContext): String? {
            // 获取玩家 (没玩家处理啥？)
            val player = player(context) ?: return null
            // 组合变量
            val papi = element.joinToString("_", "%", "%")
            // 解析变量
            return papi.replacePlaceholder(player)
        }

    }

}