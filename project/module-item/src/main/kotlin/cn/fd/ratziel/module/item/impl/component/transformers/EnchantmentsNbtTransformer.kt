package cn.fd.ratziel.module.item.impl.component.transformers

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtByte
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtInt
import cn.altawk.nbt.tag.NbtList
import cn.altawk.nbt.tag.NbtLong
import cn.altawk.nbt.tag.NbtShort
import cn.altawk.nbt.tag.NbtString
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.api.component.transformer.NbtTransformer
import cn.fd.ratziel.module.item.impl.component.type.EnchantmentType
import cn.fd.ratziel.module.item.impl.component.type.ItemEnchantmentMap
import cn.fd.ratziel.module.item.util.MetaMatcher
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.write
import taboolib.module.nms.MinecraftVersion.versionId
import java.util.LinkedHashMap

/**
 * EnchantmentsNbtTransformer
 *
 * 直接处理附魔组件在各版本中的原始 NBT 结构：
 *
 * - 1.20.4 及以下：`Enchantments: [{id, lvl}]`
 * - 1.20.5 ~ 1.21.4：`minecraft:enchantments: {levels:{sharpness:1}}`
 * - 1.21.5 及以上：`minecraft:enchantments: {sharpness:1}`
 *
 * 读取时额外兼容 1.20.5+ 的简洁写法与旧的 `show_in_tooltip` 字段。
 *
 * @author TheFloodDragon
 * @since 2026/4/6 02:31
 */
object EnchantmentsNbtTransformer : NbtTransformer<ItemEnchantmentMap> {

    private val path: NbtPath
        get() = NbtPath(if (versionId >= 12005) "minecraft:enchantments" else "Enchantments")

    override fun writeTo(root: NbtCompound, component: ItemEnchantmentMap) {
        val tag = when {
            versionId >= 12105 -> component.toModernFlatTag()
            versionId >= 12005 -> component.toModernWrappedTag()
            else -> component.toLegacyTag()
        }
        root.write(path, tag, true)
    }

    override fun readFrom(root: NbtCompound): ItemEnchantmentMap? {
        val tag = root.read(path, false) ?: return null
        val mapped = when {
            versionId >= 12105 -> readModernFlat(tag) ?: readModernWrapped(tag)
            versionId >= 12005 -> readModernWrapped(tag) ?: readModernFlat(tag)
            else -> readLegacy(tag)
        }
        return mapped?.takeIf { it.isNotEmpty() }?.let(::ItemEnchantmentMap)
    }

    override fun removeFrom(root: NbtCompound) {
        root.delete(path)
    }

    private fun ItemEnchantmentMap.toLegacyTag(): NbtList {
        return NbtList().apply {
            this@toLegacyTag.forEach { (enchantment, level) ->
                add(
                    NbtCompound().apply {
                        put("id", NbtString(minecraftKey(enchantment)))
                        put("lvl", NbtShort(level.toShort()))
                    },
                )
            }
        }
    }

    private fun ItemEnchantmentMap.toModernFlatTag(): NbtCompound {
        return NbtCompound().apply {
            this@toModernFlatTag.forEach { (enchantment, level) ->
                put(minecraftKey(enchantment), NbtInt(level))
            }
        }
    }

    private fun ItemEnchantmentMap.toModernWrappedTag(): NbtCompound {
        return NbtCompound().apply {
            put("levels", this@toModernWrappedTag.toModernFlatTag())
        }
    }

    private fun readLegacy(tag: NbtTag): MutableMap<EnchantmentType, Int>? {
        val list = tag as? NbtList ?: return null
        return LinkedHashMap<EnchantmentType, Int>(list.size).apply {
            list.forEach { entryTag ->
                val entry = entryTag as? NbtCompound ?: return@forEach
                val key = (entry["id"] as? NbtString)?.content ?: return@forEach
                val level = readNumber(entry["lvl"]) ?: return@forEach
                put(MetaMatcher.matchEnchantment(key), level)
            }
        }
    }

    private fun readModernWrapped(tag: NbtTag): MutableMap<EnchantmentType, Int>? {
        val compound = tag as? NbtCompound ?: return null
        val levels = compound["levels"] as? NbtCompound ?: return null
        return decodeFlatLevels(levels)
    }

    private fun readModernFlat(tag: NbtTag): MutableMap<EnchantmentType, Int>? {
        val compound = tag as? NbtCompound ?: return null
        if ("levels" in compound) {
            return null
        }
        return decodeFlatLevels(compound)
    }

    private fun decodeFlatLevels(levels: NbtCompound): MutableMap<EnchantmentType, Int> {
        return LinkedHashMap<EnchantmentType, Int>(levels.size).apply {
            levels.forEach { entry ->
                if (entry.key == "show_in_tooltip") {
                    return@forEach
                }
                val level = readNumber(entry.value) ?: return@forEach
                put(MetaMatcher.matchEnchantment(entry.key), level)
            }
        }
    }

    private fun readNumber(tag: NbtTag?): Int? {
        return when (tag) {
            is NbtByte -> tag.content.toInt()
            is NbtShort -> tag.content.toInt()
            is NbtInt -> tag.content
            is NbtLong -> tag.content.toInt()
            else -> null
        }
    }

    private fun minecraftKey(enchantment: EnchantmentType): String {
        val resolved = requireNotNull(enchantment.get()) {
            "Unable to resolve Bukkit enchantment from XEnchantment '$enchantment'"
        }
        return try {
            resolved.key.toString()
        } catch (_: NoSuchMethodError) {
            @Suppress("DEPRECATION")
            resolved.name
        }
    }

}
