package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.module.item.impl.builder.SectionTagResolver
import cn.fd.ratziel.platform.bukkit.util.player
import taboolib.common.platform.Awake
import taboolib.platform.compat.replacePlaceholder

/**
 * PapiTagResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/16 19:26
 */
@Awake
object PapiTagResolver : SectionTagResolver("papi", "p") {

    override fun resolve(element: List<String>, context: ArgumentContext): String? {
        // 获取玩家
        val player = context.player() ?: return null
        // 读取内容
        val content = when {
            element.size == 1 -> element.first()
            element.size > 1 -> element.joinToString("_")
            else -> return null
        }
        // 处理Papi变量
        return "%$content%".replacePlaceholder(player)
    }

}