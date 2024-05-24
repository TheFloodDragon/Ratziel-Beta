package cn.fd.ratziel.module.item.impl.builder.tagResolvers

import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.module.item.api.builder.ItemSectionResolver
import taboolib.common.util.random

/**
 * RandomResolver
 *
 * @author TheFloodDragon
 * @since 2024/5/18 16:22
 */
object RandomResolver : ItemSectionResolver.TagResolver {

    override val name = "random"

    override val alias = arrayOf("ran", "rd")

    // TODO 写完这个
    override fun resolve(element: Iterable<String>, arguments: ArgumentFactory): String {
        return random().nextInt().toString()
    }

}