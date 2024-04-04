package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.api.builder.ItemStream

/**
 * DefaultItemStream
 *
 * @author TheFloodDragon
 * @since 2024/4/4 20:44
 */
object DefaultItemStream : ItemStream {

    override val serializers = arrayOf<ItemSerializer<*, *>>()

}