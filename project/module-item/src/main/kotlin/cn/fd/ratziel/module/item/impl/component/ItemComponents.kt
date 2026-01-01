@file:Suppress("NOTHING_TO_INLINE", "unused")

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.transformers.MinecraftObjListTransformer
import cn.fd.ratziel.module.item.impl.component.transformers.MinecraftObjMessageTransformer
import cn.fd.ratziel.module.item.impl.component.transformers.MinecraftObjNoTransformation
import cn.fd.ratziel.module.item.internal.serializers.MessageComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ItemComponents
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 22:27
 */
object ItemComponents {

    /**
     * 物品组件注册表
     */
    val registry: MutableCollection<ItemComponentType<*>> = CopyOnWriteArraySet()

    @JvmField
    val CUSTOM_DATA = r("custom-data", NbtCompound.serializer()) {
        jsonEntry(); nbtEntry()
    }

    @JvmField
    val DISPLAY_NAME = r("display-name", MessageComponentSerializer) {
        jsonEntry("displayName", "name"); nbtEntry();
        nms(MinecraftObjMessageTransformer)
    }

    @JvmField
    val ITEM_NAME = r("item-name", MessageComponentSerializer) {
        jsonEntry("itemName", "localized-name", "localizedName"); nbtEntry();
        nms(MinecraftObjMessageTransformer)
    }

    @JvmField
    val LORE = r("lore", ListSerializer(MessageComponentSerializer)) {
        jsonEntry("lores"); nbtEntry();
        nms(MinecraftObjListTransformer(MinecraftObjMessageTransformer))
    }

    @JvmField // TODO 低版本处理
    val MAX_DAMAGE = r("max-damage", Int.serializer()) {
        jsonEntry("maxDamage", "maxDurability", "max-durability", "durability"); nbtEntry()
        nms(MinecraftObjNoTransformation())
    }

    // TODO .... more ... and .. more

    private fun <T : Any> r(key: String, serializer: KSerializer<T>, builder: TransformerBuilder<T>.() -> Unit = {}): ItemComponentType<T> {
        val componentType = object : ItemComponentType<T> {
            override val key = key
            override val transformer = TransformerBuilder(key, serializer).apply(builder).build()
        }
        registry.add(componentType)
        return componentType
    }

}