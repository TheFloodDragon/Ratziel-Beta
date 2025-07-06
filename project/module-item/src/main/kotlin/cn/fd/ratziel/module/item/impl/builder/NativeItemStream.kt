package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SynchronizedValue
import cn.fd.ratziel.core.serialization.json.JsonTree
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.impl.RatzielItem
import kotlinx.serialization.json.JsonElement

/**
 * NativeItemStream
 *
 * @author TheFloodDragon
 * @since 2025/5/15 20:46
 */
class NativeItemStream(
    origin: Element,
    val item: RatzielItem,
    context: ArgumentContext,
    rawElement: JsonElement = origin.property,
) : BaseItemStream(origin, context) {

    override val identifier: Identifier get() = item.identifier

    override val tree: SynchronizedValue.Mutable<JsonTree> = SynchronizedValue.initial(JsonTree(rawElement))

    override val data: SynchronizedValue<ItemData> = SynchronizedValue.getter { item.data }

    companion object {

        /**
         * 创建一个新的原生物品流 [NativeItemStream]
         */
        @JvmStatic
        suspend fun create(
            base: BaseItemStream,
            newItem: RatzielItem,
            newContext: ArgumentContext? = null,
        ): NativeItemStream {
            // 创建物品流
            return NativeItemStream(
                base.origin,
                newItem,
                newContext ?: base.context,
                base.fetchElement() // 使用基流中的元素
            )
        }

    }

}