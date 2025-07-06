package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.core.function.SynchronizedValue
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.SimpleData

/**
 * BaseItemStream
 *
 * @author TheFloodDragon
 * @since 2025/7/6 18:08
 */
open class BaseItemStream(
    override val origin: Element,
    context: ArgumentContext = SimpleContext(),
) : ItemStream {

    override val identifier: Identifier get() = throw UnsupportedOperationException("BaseItemStream is not supported for identifier!")

    override val tree: SynchronizedValue.Mutable<JsonTree> = SynchronizedValue.initial(JsonTree(origin.property))

    override val data: SynchronizedValue<ItemData> = SynchronizedValue.initial(SimpleData())

    override var context: ArgumentContext = context
        @Synchronized get
        @Synchronized set

}