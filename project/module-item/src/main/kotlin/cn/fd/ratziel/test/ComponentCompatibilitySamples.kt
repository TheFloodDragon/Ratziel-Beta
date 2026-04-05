package cn.fd.ratziel.test

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.ItemComponents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import taboolib.module.nms.MinecraftVersion

internal fun sampleValues(type: ItemComponentType<*>): List<SampleCase> = when (type) {
    ItemComponents.CUSTOM_DATA -> listOf(
        SampleCase(
            name = "nested-compound",
            value = NbtCompound {
                put("marker", NbtString("component-consistency"))
                put("level", NbtInt(7))
                put("nested", NbtCompound {
                    put("enabled", NbtByte(true))
                    put("note", NbtString("write-remove"))
                })
                put("entries", NbtList {
                    add(NbtCompound {
                        put("kind", NbtString("alpha"))
                        put("index", NbtInt(1))
                    })
                    add(NbtCompound {
                        put("kind", NbtString("beta"))
                        put("index", NbtInt(2))
                    })
                    add(NbtCompound {
                        put("kind", NbtString("leaf"))
                        put("index", NbtInt(3))
                    })
                })
            },
        ),
        SampleCase(
            name = "flat-values",
            value = NbtCompound {
                put("marker", NbtString("component-consistency-2"))
                put("enabled", NbtByte(false))
                put("count", NbtInt(0))
            },
        ),
    )

    ItemComponents.DISPLAY_NAME -> listOf(
        SampleCase("plain-text", Component.text("compat-display-name")),
        SampleCase(
            "styled-text",
            Component.text("compat-display-name-styled")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false),
        ),
        SampleCase("translatable", translatableComponent("item.minecraft.diamond_sword")),
        SampleCase("composite", compositeComponent("compat", "display", "name")),
    )

    ItemComponents.ITEM_NAME -> listOf(
        SampleCase("plain-text", Component.text("compat-item-name")),
        SampleCase(
            "styled-text",
            Component.text("compat-item-name-styled")
                .color(NamedTextColor.AQUA)
                .decoration(TextDecoration.UNDERLINED, true)
                .decoration(TextDecoration.ITALIC, false),
        ),
        SampleCase("translatable", translatableComponent("block.minecraft.anvil")),
        SampleCase("composite", compositeComponent("compat", "item", "name")),
    )

    ItemComponents.LORE -> listOf(
        SampleCase(
            name = "single-line",
            value = listOf(Component.text("compat-lore-single")),
        ),
        SampleCase(
            name = "multi-line",
            value = listOf(
                Component.text("compat-lore-line-1"),
                Component.text("compat-lore-line-2"),
            ),
        ),
        SampleCase(
            name = "rich-components",
            value = listOf(
                Component.text("compat-lore-colored")
                    .color(NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false),
                translatableComponent("item.minecraft.shield"),
                compositeComponent("compat", "lore", "composite"),
                keybindComponent("key.jump"),
            ),
        ),
    )

    ItemComponents.MAX_DAMAGE -> listOf(
        SampleCase("minimum", 1),
        SampleCase("common", 233),
    )

    ItemComponents.REPAIR_COST -> listOf(
        SampleCase("zero", 0),
        SampleCase("common", 17),
    )

    ItemComponents.GLINT_OVERRIDE -> listOf(
        SampleCase("enabled", true),
        SampleCase("disabled", false),
    )

    ItemComponents.UNBREAKABLE -> listOf(
        SampleCase("enabled", true),
        SampleCase("disabled", false),
    )

    else -> emptyList()
}

internal fun cleanRemoved(tag: NbtTag): NbtTag {
    if (tag !is NbtCompound) {
        return tag
    }

    val cleaned = NbtCompound(tag.filterNot { it.key.startsWith("!") }.toMutableMap())
    if (MinecraftVersion.versionId < 12005) {
        val display = cleaned["display"] as? NbtCompound
        if (display != null && display.entries.isEmpty()) {
            cleaned.remove("display")
        }
    }
    return cleaned
}

private fun compositeComponent(prefix: String, middle: String, suffix: String): Component {
    return Component.empty()
        .append(Component.text("$prefix-"))
        .append(
            Component.text(middle)
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.BOLD, true),
        )
        .append(Component.text("-$suffix").color(NamedTextColor.GRAY))
}

private fun keybindComponent(keybind: String): Component {
    return Component.keybind(keybind)
        .color(NamedTextColor.YELLOW)
        .decoration(TextDecoration.ITALIC, false)
}

private fun translatableComponent(key: String): Component {
    return Component.translatable(key)
        .color(NamedTextColor.BLUE)
        .decoration(TextDecoration.ITALIC, false)
}

internal data class SampleCase(
    val name: String,
    val value: Any,
)
