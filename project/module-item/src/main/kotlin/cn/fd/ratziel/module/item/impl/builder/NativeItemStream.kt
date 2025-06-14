package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SynchronizedValue
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.RatzielItem
import kotlinx.serialization.json.JsonElement

/**
 * NativeItemStream
 *
 * @author TheFloodDragon
 * @since 2025/5/15 20:46
 */
class NativeItemStream(
    override val origin: Element,
    val item: RatzielItem,
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
    suspend fun copyWith(newContext: ArgumentContext): NativeItemStream {
        return NativeItemStream(
            this.origin,
            this.item.clone(),
            newContext,
            fetchElement()
        )
    }

}