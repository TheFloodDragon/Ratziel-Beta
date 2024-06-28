package cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers

import cn.fd.ratziel.function.argument.ContextArgument
import cn.fd.ratziel.function.argument.popOrNull
import cn.fd.ratziel.module.item.impl.builder.resolver.SectionStringResolver
import org.bukkit.OfflinePlayer
import taboolib.platform.compat.replacePlaceholder

/**
 * PapiResolver
 *
 * @author TheFloodDragon
 * @since 2024/6/25 20:18
 */
object PapiResolver : SectionStringResolver {

    override fun resolve(element: String, arguments: ContextArgument): String {
        // 获取玩家
        val player = arguments.popOrNull<OfflinePlayer>() ?: return element // 没玩家处理啥？
        // 处理Papi变量
        return element.replacePlaceholder(player)
    }

}