package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.MutexedValue
import cn.fd.ratziel.core.functional.synchronized
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.builder.ItemCompositor
import cn.fd.ratziel.module.item.api.builder.ItemStream
import kotlinx.serialization.json.JsonElement

/**
 * NativeItemStream
 *
 * @author TheFloodDragon
 * @since 2025/5/15 20:46
 */
class NativeItemStream(
    override val origin: Element,
    itemInstance: IdentifiedItem,
    context: ArgumentContext,
    rawElement: JsonElement = origin.property,
) : ItemStream {

    override val identifier = itemInstance.identifier

    override lateinit var compositor: ItemCompositor
        internal set

    override val tree = MutexedValue.initial(JsonTree(rawElement))

    override val item = MutexedValue.getter { itemInstance }

    override var context: ArgumentContext by synchronized { context }

    /**
     * 复制一份新的 [NativeItemStream]
     */
    override suspend fun copy(): NativeItemStream = NativeItemStream(
        origin,
        item.withValue { it.clone() },
        context,
        fetchProperty()
    ).also { it.compositor = compositor } // 编排器传递

}