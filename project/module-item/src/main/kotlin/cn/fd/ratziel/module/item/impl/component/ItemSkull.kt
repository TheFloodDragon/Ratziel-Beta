@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.DataProcessor
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.impl.component.serializers.MetaComponentSerializer
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.BukkitSkull
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemSkull
 *
 * @author TheFloodDragon
 * @since 2024/10/1 13:48
 */
@Serializable(ItemSkull.Serializer::class)
class ItemSkull(itemStack: ItemStack?) : MetaComponent<SkullMeta>(itemStack) {

    object Processor : DataProcessor {
        override fun process(data: ItemData) = data.apply {
            if (tag.isNotEmpty()) {
                material = SimpleMaterial(XMaterial.PLAYER_HEAD) // 设置材质
            }
        }
    }

    companion object {

        /**
         * 头颅缓存
         */
        private val CACHE: MutableMap<String, ItemStack> = ConcurrentHashMap()

        /**
         * 获取头颅数据
         */
        fun fetchSkullData(value: String): ItemStack {
            return CACHE.computeIfAbsent(value.trim()) { generateSkullItem(it) }.clone()
        }

        /**
         * 生成纯头颅数据的 [ItemStack]
         */
        fun generateSkullItem(value: String): ItemStack {
            return BukkitSkull.applySkull(value)
        }

        /**
         * 读取头颅数据
         */
        fun getSkullValue(skullMeta: SkullMeta): String {
            return BukkitSkull.getSkullValue(skullMeta)
        }

        /**
         * 读取头颅数据
         */
        fun fetchSkullData(tag: NbtCompound): ItemStack {
            val item = RefItemStack.of(XMaterial.PLAYER_HEAD.parseItem()!!)
            item.tag = tag
            return item.bukkitStack
        }

    }

    internal object Serializer : MetaComponentSerializer<ItemSkull>(
        "head", "skull"
    ) {

        override fun decode(element: JsonElement?): ItemSkull {
            val value = (element as? JsonPrimitive)?.contentOrNull
            return ItemSkull(if (value != null) fetchSkullData(value) else null)
        }

        override fun encode(value: ItemSkull): JsonElement {
            val value = value.meta?.let { getSkullValue(it) }
            return if (value != null) JsonPrimitive(value) else JsonNull
        }

        override fun decode(tag: NbtCompound): ItemSkull {
            return ItemSkull(fetchSkullData(tag))
        }

    }

}