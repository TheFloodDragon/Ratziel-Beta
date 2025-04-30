package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.impl.component.serializers.MetaComponentSerializer
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.library.xseries.XMaterial

/**
 * ItemHideFlag
 *
 * @author TheFloodDragon
 * @since 2025/4/30 21:55
 */
@Serializable(ItemHideFlag.Serializer::class)
class ItemHideFlag(itemStack: ItemStack?) : MetaComponent<ItemMeta>(itemStack) {

    internal object Serializer : MetaComponentSerializer<ItemHideFlag>(
        "hideFlags", "hideflag", "hideflag", "hideflags", "hideFlag"
    ) {

        override fun decode(element: JsonElement?): ItemHideFlag {
            val flags = (element as? JsonArray)
                ?.map { MetaMatcher.matchHideFlag((it as JsonPrimitive).content) }
                ?: return ItemHideFlag(null)

            val itemStack = XMaterial.AIR.parseItem()!!.apply {
                itemMeta!!.addItemFlags(*flags.toTypedArray())
            }

            return ItemHideFlag(itemStack)
        }

        override fun encode(value: ItemHideFlag): JsonElement {
            val flags = value.meta?.itemFlags
                ?.map { JsonPrimitive(it.name) }
                ?: emptyList()
            return JsonArray(flags)
        }

        override fun decode(tag: NbtCompound): ItemHideFlag {
            val ref = RefItemStack.of(XMaterial.AIR.parseItem()!!)
            ref.tag = tag
            return ItemHideFlag(ref.bukkitStack)
        }

    }

}