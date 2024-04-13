package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.builder.ItemSerializer

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
 */
class DefaultItemGenerator(override val origin: Element) : ItemGenerator {

    override val serializers = arrayOf<ItemSerializer<*, *>>()

}