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

    // TODO 写完这个
    override fun resolve(element: Iterable<String>, arguments: ArgumentFactory): String? {
        return random().nextInt().toString()
    }

}