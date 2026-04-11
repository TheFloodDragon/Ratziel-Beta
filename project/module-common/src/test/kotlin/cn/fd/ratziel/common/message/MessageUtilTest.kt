package cn.fd.ratziel.common.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.KeybindComponent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MessageUtilTest {

    /**
     * 按照换行符将 Adventure [Component] 拆分为多行。
     */
    fun Component.splitByNewline(): List<Component> {
        return splitBy("\n")
    }

    @Test
    fun `splits plain text component by newline`() {
        val lines = Component.text("first\nsecond").splitByNewline()

        assertEquals(listOf("first", "second"), lines.map(::plainText))
    }

    @Test
    fun `preserves text component style after splitting`() {
        val lines = Component.text("first\nsecond")
            .color(NamedTextColor.GOLD)
            .decoration(TextDecoration.BOLD, true)
            .splitByNewline()

        assertEquals(listOf("first", "second"), lines.map(::plainText))
        assertEquals(NamedTextColor.GOLD, lines[0].style().color())
        assertEquals(NamedTextColor.GOLD, lines[1].style().color())
        assertEquals(TextDecoration.State.TRUE, lines[0].style().decoration(TextDecoration.BOLD))
        assertEquals(TextDecoration.State.TRUE, lines[1].style().decoration(TextDecoration.BOLD))
    }

    @Test
    fun `inherits parent style for lines created from child newline`() {
        val component = Component.empty()
            .color(NamedTextColor.BLUE)
            .append(Component.text("first\nsecond"))
            .append(Component.text("!"))

        val lines = component.splitByNewline()

        assertEquals(listOf("first", "second!"), lines.map(::plainText))
        assertEquals(NamedTextColor.BLUE, lines[0].style().color())
        assertEquals(NamedTextColor.BLUE, lines[1].style().color())
    }

    @Test
    fun `keeps empty lines created by consecutive or trailing newlines`() {
        val lines = Component.text("first\n\nsecond\n").splitByNewline()

        assertEquals(listOf("first", "", "second", ""), lines.map(::plainText))
    }

    @Test
    fun `preserves different styles before and after separator`() {
        val lines = Component.empty()
            .append(Component.text("left").color(NamedTextColor.RED))
            .append(Component.text("|")).append(Component.text("right").color(NamedTextColor.GREEN))
            .splitBy("|")

        assertEquals(listOf("left", "right"), lines.map(::plainText))
        assertEquals(listOf(NamedTextColor.RED), textComponents(lines[0]).map { it.style().color() })
        assertEquals(listOf(NamedTextColor.GREEN), textComponents(lines[1]).map { it.style().color() })
    }

    @Test
    fun `supports multi character separator`() {
        val lines = Component.text("left<split>middle<split>right")
            .color(NamedTextColor.LIGHT_PURPLE)
            .splitBy("<split>")

        assertEquals(listOf("left", "middle", "right"), lines.map(::plainText))
        assertEquals(
            listOf(NamedTextColor.LIGHT_PURPLE, NamedTextColor.LIGHT_PURPLE, NamedTextColor.LIGHT_PURPLE),
            lines.map { it.style().color() }
        )
    }

    @Test
    fun `keeps translatable payload only on the first split result`() {
        val lines = Component.translatable("item.minecraft.diamond_sword")
            .color(NamedTextColor.AQUA)
            .append(Component.text("first\nsecond").color(NamedTextColor.RED))
            .splitByNewline()

        assertEquals(2, lines.size)
        assertIs<TranslatableComponent>(lines[0])
        assertEquals(
            Component.translatable("item.minecraft.diamond_sword")
                .color(NamedTextColor.AQUA)
                .append(Component.text("first").color(NamedTextColor.RED)),
            lines[0]
        )
        assertEquals(
            Component.empty()
                .color(NamedTextColor.AQUA)
                .append(Component.text("second").color(NamedTextColor.RED)),
            lines[1]
        )
    }

    @Test
    fun `keeps keybind payload only on the first split result`() {
        val lines = Component.keybind("key.jump")
            .color(NamedTextColor.YELLOW)
            .append(Component.text("|after").color(NamedTextColor.GREEN))
            .splitBy("|")

        assertEquals(2, lines.size)
        assertIs<KeybindComponent>(lines[0])
        assertEquals(
            Component.keybind("key.jump")
                .color(NamedTextColor.YELLOW)
                .append(Component.text("").color(NamedTextColor.GREEN)),
            lines[0]
        )
        assertEquals(
            Component.empty()
                .color(NamedTextColor.YELLOW)
                .append(Component.text("after").color(NamedTextColor.GREEN)),
            lines[1]
        )
    }

    private fun plainText(component: Component): String {

        return buildString { appendPlain(component) }
    }

    private fun StringBuilder.appendPlain(component: Component) {
        if (component is TextComponent) {
            append(component.content())
        }
        component.children().forEach { child ->
            appendPlain(child)
        }
    }

    private fun textComponents(component: Component): List<TextComponent> {
        return buildList { collectTextComponents(component) }
            .filter { it.content().isNotEmpty() }
    }

    private fun MutableList<TextComponent>.collectTextComponents(component: Component) {
        if (component is TextComponent) {
            add(component)
        }
        component.children().forEach { child ->
            collectTextComponents(child)
        }
    }
}
