package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.SynchronizedValue
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.ItemData
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
    val item: IdentifiedItem,
    context: ArgumentContext,
    rawElement: JsonElement = origin.property,
) : ItemStream {

    override val identifier: Identifier get() = item.identifier

    override val tree: SynchronizedValue.Mutable<JsonTree> = SynchronizedValue.initial(JsonTree(rawElement))

    override val data: SynchronizedValue<ItemData> = SynchronizedValue.getter { item.data }

    override var context: ArgumentContext = context
        @Synchronized get
        @Synchronized set

    /**
     * 复制一份新的 [NativeItemStream]
     */
    suspend fun copy(newContext: ArgumentContext? = null): NativeItemStream {
        return NativeItemStream(
            this.origin,
            this.item.clone(),
            newContext ?: this.context,
            fetchElement()
        )
    }

}