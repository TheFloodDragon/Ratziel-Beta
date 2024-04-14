package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
class DefaultItemGenerator(override val origin: Element) : ItemGenerator {

    override val resolvers = ConcurrentLinkedDeque<Priority<ItemResolver>>()

    override val serializers = ConcurrentLinkedDeque<Priority<ItemSerializer<*, *>>>()

}