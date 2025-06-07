package cn.fd.ratziel.module.item.impl.builder.provided

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.platform.bukkit.util.player
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import taboolib.platform.compat.replacePlaceholder

/**
 * PapiResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/16 19:26
 */
@AutoRegister
object PapiResolver : ItemSectionResolver, ItemTagResolver {

    override fun resolve(node: JsonTree.Node, context: ArgumentContext) {
        if (node !is JsonTree.PrimitiveNode || !node.value.isString || node.value is JsonNull) return
        // 仅当有玩家参数的时候解析
        val player = context.player() ?: return
        // 解析 PlaceholderAPI 变量
        node.value = JsonPrimitive(node.value.content.replacePlaceholder(player))
    }

    override val alias = arrayOf("papi", "p")

    override fun resolve(args: List<String>, context: ArgumentContext): String? {
        // 获取玩家
        val player = context.player() ?: return null
        // 读取内容
        val content = when {
            args.size == 1 -> args.first()
            args.size > 1 -> args.joinToString("_")
            else -> return null
        }
        // 处理Papi变量
        return "%$content%".replacePlaceholder(player)
    }

}