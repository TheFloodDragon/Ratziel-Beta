package cn.fd.ratziel.module.item.impl.builder.resolver.sectionResolvers

import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.module.item.impl.builder.resolver.SectionResolver
import taboolib.common.util.random

/**
 * RandomResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/18 16:22
 */
object RandomResolver : SectionResolver.TagResolver {

    override val names = arrayOf("random", "ran", "rd")

    // TODO 写完这个
    override fun resolve(element: Iterable<String>, context: ArgumentContext): String {
        return random().nextInt().toString()
    }

}