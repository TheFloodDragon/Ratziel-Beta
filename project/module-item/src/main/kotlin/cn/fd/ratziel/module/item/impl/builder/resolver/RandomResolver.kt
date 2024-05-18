package cn.fd.ratziel.module.item.impl.builder.resolver

import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.api.common.StringResolver
import taboolib.common.util.random

/**
 * RandomResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/18 16:22
 */
object RandomResolver : StringResolver {

    override val name = "random"

    override val alias = arrayOf("ran", "rd")

    override fun resolve(element: Array<String>, arguments: ArgumentFactory): String? {
        if (element.isEmpty()) return null
        // TODO 写完这个
        return random().nextInt().toString()
    }

}