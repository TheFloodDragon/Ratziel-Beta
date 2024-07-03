package cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers

import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.function.argument.popOrNull
import cn.fd.ratziel.module.item.impl.builder.resolver.SectionStringResolver
import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer
import taboolib.platform.compat.replacePlaceholder

/**
 * PapiResolver
 *
 * @author TheFloodDragon
 * @since 2024/6/25 20:18
 */
object PapiResolver : SectionStringResolver {

    override fun resolve(element: String, context: ArgumentContext): String {
        // 获取玩家 (没玩家处理啥？)
        val player: OfflinePlayer =
            context.popOrNull<ProxyPlayer>()?.cast()
                ?: context.popOrNull<OfflinePlayer>()
                ?: return element
        // 处理Papi变量
        return element.replacePlaceholder(player)
    }

}