@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.SimpleItemMaterial
import cn.fd.ratziel.module.item.impl.component.util.SkullData
import cn.fd.ratziel.module.item.impl.component.util.SkullUtil
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.nms.RefItemMeta
import cn.fd.ratziel.module.item.nms.RefItemStack
import cn.fd.ratziel.module.item.util.read
import cn.fd.ratziel.module.item.util.write
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.Color
import org.bukkit.DyeColor

/**
 * ItemCharacteristic
 *
 * @author TheFloodDragon
 * @since 2024/6/29 15:20
 */
@Serializable
data class ItemCharacteristic(
    /**
     * 头颅数据
     */
    @JsonNames("head", "skull-meta", "skullMeta", "head", "head-meta")
    var skull: SkullData? = null,
    /**
     * 染色皮革物品和药水的颜色
     */
    @JsonNames("hexColor")
    var color: String? = null,
) {

    companion object : ItemTransformer<ItemCharacteristic> {

        override fun transform(data: ItemData.Mutable, component: ItemCharacteristic) {
            // 头颅处理 (当源数据的材料为空或者是PLAYER_HEAD时, 才处理相关)
            if (data.material.isEmpty() || SimpleItemMaterial.equal(data.material, BukkitMaterial.PLAYER_HEAD)) {
                val skullTag = component.skull?.let { RefItemStack(it) }?.getTag()
                if (skullTag != null) {
                    // 设置材质
                    data.material = SimpleItemMaterial(BukkitMaterial.PLAYER_HEAD)
                    // 应用标签
                    data.tag.merge(skullTag, true)
                }
            }
            // 颜色处理
            val node = when {
                SimpleItemMaterial.isLeatherArmor(data.material) -> ItemSheet.DYED_COLOR
                SimpleItemMaterial.isPotion(data.material) -> ItemSheet.POTION_COLOR
                else -> return
            }
            data.write(node, component.color?.let { parseColor(it) }?.let { NBTInt(it) })
        }

        override fun detransform(data: ItemData): ItemCharacteristic {
            val impl = ItemCharacteristic()
            when {
                // 头颅处理 (需要对应材质为PLAYER_HEAD)
                data.material.name == BukkitMaterial.PLAYER_HEAD.name -> {
                    val skullMeta = RefItemMeta.of(RefItemMeta.META_SKULL, data.tag).handle
                    if (skullMeta.hasOwner()) impl.skull = SkullUtil.fetchSkull(skullMeta)
                }
                // 皮革颜色处理
                SimpleItemMaterial.isLeatherArmor(data.material) ->
                    data.read<NBTInt>(ItemSheet.DYED_COLOR) { impl.color = it.content.toString() }
                // 药水颜色处理
                SimpleItemMaterial.isPotion(data.material) ->
                    data.read<NBTInt>(ItemSheet.POTION_COLOR) { impl.color = it.content.toString() }
            }
            // 返回
            return impl
        }

        fun parseColor(content: String): Int = try {
            // Red
            DyeColor.valueOf(content.uppercase()).color.asRGB()
        } catch (_: IllegalArgumentException) {
            when {
                // #42b983
                content.startsWith("#") -> Integer.parseInt(content, 16)
                // 42b983
                content.length == 6 -> Integer.parseInt(content, 16)
                content.contains(",") -> {
                    val split = content.trim().split(",")
                    Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt()).asRGB()
                }

                else -> Integer.parseInt(content)
            }
        }

    }

}