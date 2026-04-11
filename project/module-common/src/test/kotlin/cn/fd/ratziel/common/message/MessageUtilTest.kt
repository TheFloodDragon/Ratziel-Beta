package cn.fd.ratziel.common.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageUtilTest {

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
            .append(Component.text("|").color(NamedTextColor.BLUE))
            .append(Component.text("right").color(NamedTextColor.GREEN))
            .splitBy('|')

        assertEquals(listOf("left", "right"), lines.map(::plainText))
        assertEquals(listOf(NamedTextColor.RED), textComponents(lines[0]).map { it.style().color() })
        assertEquals(listOf(NamedTextColor.GREEN), textComponents(lines[1]).map { it.style().color() })
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
