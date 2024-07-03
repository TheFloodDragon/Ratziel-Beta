@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("DEPRECATION")

package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.ItemNode
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.impl.BukkitMaterial
import cn.fd.ratziel.module.item.impl.ItemMaterialImpl
import cn.fd.ratziel.module.item.impl.component.util.HeadUtil
import cn.fd.ratziel.module.item.nbt.NBTInt
import cn.fd.ratziel.module.item.nbt.read
import cn.fd.ratziel.module.item.nbt.write
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.nms.RefItemMeta
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.XSkull
import taboolib.platform.util.getSkullValue

/**
 * ItemCharacteristic
 *
 * @author TheFloodDragon
 * @since 2024/6/29 15:20
 */
@Serializable
data class ItemCharacteristic(
    /**
     * 头颅元数据:
     * 使用字符串存储, 值的类型详见 [XSkull.ValueType]
     */
    @JsonNames("skull", "skull-meta", "skullMeta", "head", "head-meta")
    var headMeta: String? = null,
    /**
     * 染色皮革物品和药水的颜色
     */
    @JsonNames("hexColor")
    var color: String? = null,
) {

    companion object : ItemTransformer<ItemCharacteristic> {

        override val node = ItemNode.ROOT

        override fun transform(data: ItemData, component: ItemCharacteristic) {
            // 头颅处理 (当源数据的材料为空或者是PLAYER_HEAD时, 才处理相关)
            if (data.material.isEmpty() || ItemMaterialImpl.equal(data.material, BukkitMaterial.PLAYER_HEAD)) {
                component.headMeta?.let { HeadUtil.getHeadTag(it) }?.let {
                    // 设置材质
                    data.material = ItemMaterialImpl(BukkitMaterial.PLAYER_HEAD)
                    // 应用标签
                    data.tag.merge(it, true)
                }
            }
            // 颜色处理
            val node = when {
                ItemMaterialImpl.isLeatherArmor(data.material) -> ItemSheet.DYED_COLOR
                ItemMaterialImpl.isPotion(data.material) -> ItemSheet.POTION_COLOR
                else -> return
            }
            data.tag.write(node, component.color?.let { parseColor(it) }?.let { NBTInt(it) })
        }

        override fun detransform(data: ItemData): ItemCharacteristic {
            val impl = ItemCharacteristic()
            when {
                // 头颅处理 (需要对应材质为PLAYER_HEAD)
                data.material.name == BukkitMaterial.PLAYER_HEAD.name -> {
                    val skullMeta = RefItemMeta(RefItemMeta.skullClass, data.tag).handle as? SkullMeta
                    impl.headMeta = skullMeta?.owner ?: skullMeta?.getSkullValue() ?: return impl
                }
                // 皮革颜色处理
                ItemMaterialImpl.isLeatherArmor(data.material) ->
                    data.tag.read<NBTInt>(ItemSheet.DYED_COLOR) { impl.color = it.content.toString() }
                // 药水颜色处理
                ItemMaterialImpl.isPotion(data.material) ->
                    data.tag.read<NBTInt>(ItemSheet.POTION_COLOR) { impl.color = it.content.toString() }
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