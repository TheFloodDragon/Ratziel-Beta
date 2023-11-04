package cn.fd.ratziel.module.item.util.meta

import cn.fd.ratziel.common.adventure.serializeLegacy
import cn.fd.ratziel.common.adventure.toJsonFormat
import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.api.meta.ItemCharacteristic
import cn.fd.ratziel.module.item.item.meta.VItemCharacteristic
import cn.fd.ratziel.module.item.util.ref.RefItemMeta
import net.kyori.adventure.text.Component
import taboolib.module.nms.MinecraftVersion

typealias ItemChar = ItemCharacteristic
typealias VItemChar = VItemCharacteristic

/**
 * NMS:
 *   1.13+ > Json Format
 *   1.13- > Original Format (§)
 */
fun nmsComponent(component: Component?): String? =
    if (MinecraftVersion.isLower(MinecraftVersion.V1_13)) {
        component?.let { serializeLegacy(it) }
    } else component?.toJsonFormat()

/**
 * 创建一个空的CraftItemMeta
 */
fun emptyCraftItemMeta() = RefItemMeta.createInstance(NBTCompound.createInstance())