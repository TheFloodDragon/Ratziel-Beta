//@file:OptIn(ExperimentalSerializationApi::class)
//
//package cn.fd.ratziel.module.item.impl.component
//
//import cn.fd.ratziel.module.item.api.ItemData
//import cn.fd.ratziel.module.item.api.ItemMaterial
//import cn.fd.ratziel.module.item.api.builder.ItemTransformer
//import cn.fd.ratziel.module.item.impl.BukkitMaterial
//import cn.fd.ratziel.module.item.impl.SimpleMaterial
//import cn.fd.ratziel.module.item.impl.component.util.SkullData
//import cn.fd.ratziel.module.item.util.SkullUtil
//import cn.fd.ratziel.module.item.nms.ItemSheet
//import cn.fd.ratziel.module.item.nms.RefItemMeta
//import cn.fd.ratziel.module.item.nms.RefItemStack
//import cn.fd.ratziel.module.item.util.read
//import cn.fd.ratziel.module.item.util.write
//import cn.fd.ratziel.module.nbt.NBTInt
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.JsonNames
//import org.bukkit.Color
//import org.bukkit.DyeColor
//import java.util.concurrent.CompletableFuture
//
///**
// * ItemCharacteristic
// *
// * @author TheFloodDragon
// * @since 2024/6/29 15:20
// */
//@Serializable
//data class ItemCharacteristic(
//    /**
//     * 头颅数据
//     */
//    @JsonNames("head", "skull-meta", "skullMeta", "headMeta", "head-meta")
//    var skull: SkullData? = null,
//    /**
//     * 染色皮革物品和药水的颜色
//     */
//    @JsonNames("hexColor")
//    var color: String? = null,
//) {
//
//    companion object : ItemTransformer<ItemCharacteristic> {
//
//        @JvmField
//        val PLAYER_HEAD: ItemMaterial = SimpleMaterial(BukkitMaterial.PLAYER_HEAD)
//
//        override fun transform(data: ItemData, component: ItemCharacteristic) {
//            // 头颅处理 (当源数据的材料为空或者是PLAYER_HEAD时, 才处理相关)
//            if (data.material.isEmpty() || data.material == PLAYER_HEAD) {
//                val skullTag = component.skull?.get()?.let { RefItemStack.of(it) }?.tag
//                if (skullTag != null) {
//                    // 设置材质
//                    data.material = PLAYER_HEAD
//                    // 应用标签
//                    data.tag.merge(skullTag, true)
//                }
//            } else {
//                // 颜色处理
//                val node = when {
//                    isLeatherArmor(data.material) -> ItemSheet.DYED_COLOR
//                    isPotion(data.material) -> ItemSheet.POTION_COLOR
//                    else -> return
//                }
//                data.write(node, component.color?.let { parseColor(it) }?.let { NBTInt(it) })
//            }
//        }
//
//        override fun detransform(data: ItemData): ItemCharacteristic {
//            val impl = ItemCharacteristic()
//            when {
//                // 头颅处理 (需要对应材质为PLAYER_HEAD)
//                data.material.name == BukkitMaterial.PLAYER_HEAD.name -> {
//                    val skullMeta = RefItemMeta.of(RefItemMeta.META_SKULL, data.tag).handle
//                    if (skullMeta.hasOwner()) impl.skull = CompletableFuture.completedFuture(SkullUtil.fetchSkull(skullMeta))
//                }
//                // 皮革颜色处理
//                isLeatherArmor(data.material) ->
//                    data.read<NBTInt>(ItemSheet.DYED_COLOR) { impl.color = it.content.toString() }
//                // 药水颜色处理
//                isPotion(data.material) ->
//                    data.read<NBTInt>(ItemSheet.POTION_COLOR) { impl.color = it.content.toString() }
//            }
//            // 返回
//            return impl
//        }
//
//        fun parseColor(content: String): Int = try {
//            // Red
//            DyeColor.valueOf(content.uppercase()).color.asRGB()
//        } catch (_: IllegalArgumentException) {
//            when {
//                // #42b983
//                content.startsWith("#") -> Integer.parseInt(content.drop(1), 16)
//                // 42b983
//                content.length == 6 -> Integer.parseInt(content, 16)
//                content.contains(",") -> {
//                    val split = content.trim().split(",")
//                    Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt()).asRGB()
//                }
//
//                else -> Integer.parseInt(content)
//            }
//        }
//
//        fun isPotion(material: ItemMaterial) = material.name.contains("POTION", true)
//
//        private val leatherArmors by lazy {
//            arrayOf(
//                BukkitMaterial.LEATHER_HELMET.name,
//                BukkitMaterial.LEATHER_CHESTPLATE.name,
//                BukkitMaterial.LEATHER_LEGGINGS.name,
//                BukkitMaterial.LEATHER_BOOTS.name
//            )
//        }
//
//        fun isLeatherArmor(material: ItemMaterial) = leatherArmors.contains(material.name.uppercase())
//
//    }
//
//}