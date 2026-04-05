package cn.fd.ratziel.test

import cn.altawk.nbt.tag.*
import cn.fd.ratziel.core.exception.UnsupportedVersionException
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.impl.SimpleMaterial
import cn.fd.ratziel.module.item.impl.component.ItemComponents
import cn.fd.ratziel.module.item.internal.RefItemStack
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import taboolib.common.Test
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * ComponentCompatibilityTest
 *
 * 在 Bukkit ENABLE 阶段执行内置组件的 Minecraft/NBT 转换一致性检查，
 * 并将结果输出到 /outs。
 */
object ComponentCompatibilityTest : Test() {

    private val reportFile: File
        get() = File("outs", "module-item-transformer-compatibility.txt")

    override fun check(): List<Result> {
        return ItemComponents.registry
            .sortedBy { it.id }
            .map { type ->
                val componentId = type.id
                val sample = sampleValue(componentId)
                    ?: return@map Test.Unsupported("$componentId (暂未提供稳定样例值)")
                try {
                    @Suppress("UNCHECKED_CAST")
                    assertTransformerConsistency(type as ItemComponentType<Any>, sample)
                    Test.Success(componentId)
                } catch (ex: UnsupportedVersionException) {
                    Test.Unsupported("$componentId (${ex.message ?: "当前版本不支持"})")
                } catch (ex: Throwable) {
                    Test.Failure.Companion.of(componentId, ex)
                }
            }
    }

    internal fun runAtEnable() {
        val batch = Test.Companion.check(this)
        batch.print(true)
        val report = buildReport(batch)
        reportFile.parentFile?.mkdirs()
        reportFile.writeText(report)
        if (batch.failure > 0) {
            warning("module-item 组件一致性测试失败 ${batch.failure} 项，详见 ${reportFile.path}")
        } else {
            info("module-item 组件一致性测试完成，结果已输出到 ${reportFile.path}")
        }
    }

    private fun assertTransformerConsistency(type: ItemComponentType<Any>, component: Any) {
        val baseline = createBaseline()
        val originalTag = baseline.originalTag.clone()

        val minecraftSandbox = RefItemStack.of(baseline.data.clone())
        val nbtSandbox = baseline.data.clone()

        val transforming = type.transforming
        val nmsItem = requireNotNull(minecraftSandbox.nmsStack) {
            "组件 '${type.id}' 的 Minecraft 沙箱未能创建 NMS ItemStack。"
        }

        transforming.minecraftTransformer.write(nmsItem, component)
        val nbtWriteRoot = nbtSandbox.tag.clone()
        transforming.nbtTransformer.writeTo(nbtWriteRoot, component)
        nbtSandbox.tag = nbtWriteRoot

        val minecraftWrittenTag = minecraftSandbox.tag
        val nbtWrittenTag = nbtSandbox.tag
        val writeDiff = diffTags(minecraftWrittenTag, nbtWrittenTag)
        check(writeDiff.isEmpty()) {
            buildWriteFailureMessage(type.id, component, minecraftWrittenTag, nbtWrittenTag, writeDiff)
        }

        transforming.minecraftTransformer.remove(nmsItem)
        val nbtRemoveRoot = nbtSandbox.tag.clone()
        transforming.nbtTransformer.removeFrom(nbtRemoveRoot)
        nbtSandbox.tag = nbtRemoveRoot

        val minecraftRemovedTag = minecraftSandbox.tag
        val nbtRemovedTag = nbtSandbox.tag
        val minecraftRemoveDiff = diffTags(minecraftRemovedTag, originalTag)
        val nbtRemoveDiff = diffTags(nbtRemovedTag, originalTag)

        check(minecraftRemoveDiff.isEmpty() && nbtRemoveDiff.isEmpty()) {
            buildRemoveFailureMessage(
                componentId = type.id,
                component = component,
                originalTag = originalTag,
                minecraftTag = minecraftRemovedTag,
                nbtTag = nbtRemovedTag,
                minecraftDiff = minecraftRemoveDiff,
                nbtDiff = nbtRemoveDiff,
            )
        }
    }

    private fun createBaseline(): Baseline {
        val baselineRef = RefItemStack.of(SimpleMaterial("DIAMOND_SWORD"))
        val baselineData = baselineRef.extractData().let {
            SimpleData(it.material, it.tag.clone(), it.amount)
        }
        return Baseline(
            data = baselineData,
            originalTag = baselineRef.tag.clone(),
        )
    }

    private fun sampleValue(componentId: String): Any? = when (componentId) {
        "custom-data" -> NbtCompound {
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
        }

        "display-name" -> Component.text("compat-display-name")
        "item-name" -> Component.text("compat-item-name")
        "lore" -> listOf(
            Component.text("compat-lore-line-1"),
            Component.text("compat-lore-line-2"),
        )

        "max-damage" -> 233
        "repair-cost" -> 17
        "glint-override" -> true
        "unbreakable" -> true
        else -> null
    }

    private fun buildReport(batch: Test.Companion.BatchResult): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return buildString {
            appendLine("# module-item 组件转换一致性测试")
            appendLine("时间: $timestamp")
            appendLine("Bukkit: ${Bukkit.getVersion()}")
            appendLine("Bukkit API: ${Bukkit.getBukkitVersion()}")
            appendLine("总计: ${batch.results.size}")
            appendLine("成功: ${batch.success}")
            appendLine("失败: ${batch.failure}")
            appendLine("不支持: ${batch.unsupported}")
            appendLine()
            appendLine("## 结果详情")
            batch.results.forEach { result ->
                when (result) {
                    is Test.Success -> appendLine("[SUCCESS] ${result.reason}")
                    is Test.Unsupported -> appendLine("[UNSUPPORTED] ${result.reason}")
                    is Test.Failure -> {
                        appendLine("[FAILURE] ${result.reason}")
                        appendLine(result.error.stackTraceToString().trimEnd())
                    }
                }
                appendLine()
            }
        }
    }

    private fun buildWriteFailureMessage(
        componentId: String,
        component: Any,
        minecraftTag: NbtCompound,
        nbtTag: NbtCompound,
        diff: List<String>,
    ): String {
        return buildString {
            appendLine("组件 '$componentId' 写入后一致性检查失败。")
            appendLine("样例值: $component")
            appendLine("Minecraft tag: $minecraftTag")
            appendLine("NBT tag: $nbtTag")
            appendLine("差异:")
            append(formatDiff(diff))
        }
    }

    private fun buildRemoveFailureMessage(
        componentId: String,
        component: Any,
        originalTag: NbtCompound,
        minecraftTag: NbtCompound,
        nbtTag: NbtCompound,
        minecraftDiff: List<String>,
        nbtDiff: List<String>,
    ): String {
        return buildString {
            appendLine("组件 '$componentId' 删除后未能恢复到原始 tag。")
            appendLine("样例值: $component")
            appendLine("原始 tag: $originalTag")
            appendLine("Minecraft 删除后 tag: $minecraftTag")
            appendLine("NBT 删除后 tag: $nbtTag")
            if (minecraftDiff.isNotEmpty()) {
                appendLine("Minecraft -> 原始 tag 差异:")
                appendLine(formatDiff(minecraftDiff))
            }
            if (nbtDiff.isNotEmpty()) {
                appendLine("NBT -> 原始 tag 差异:")
                appendLine(formatDiff(nbtDiff))
            }
        }
    }

    private fun formatDiff(diff: List<String>): String {
        return if (diff.isEmpty()) {
            "  (无差异)"
        } else {
            diff.joinToString(separator = "\n") { "  - $it" }
        }
    }

    private fun diffTags(left: NbtTag?, right: NbtTag?, path: String = "\$root"): List<String> {
        if (left == null && right == null) return emptyList()
        if (left == null) return listOf("$path 缺失左值，右值=${renderTag(right)}")
        if (right == null) return listOf("$path 缺失右值，左值=${renderTag(left)}")
        if (left::class != right::class) {
            return listOf("$path 类型不同：${typeName(left)} != ${typeName(right)}")
        }
        return when (left) {
            is NbtCompound -> {
                right as NbtCompound
                val keys = (left.entries.map { it.key } + right.entries.map { it.key }).toSortedSet()
                keys.flatMap { key -> diffTags(left[key], right[key], "$path.$key") }
            }

            is NbtList -> {
                right as NbtList
                buildList {
                    if (left.size != right.size) {
                        add("$path 列表长度不同：${left.size} != ${right.size}")
                    }
                    val size = minOf(left.size, right.size)
                    repeat(size) { index ->
                        addAll(diffTags(left.content[index], right.content[index], "$path[$index]"))
                    }
                }
            }

            is NbtByteArray -> compareArray(path, left.content.contentToString(), (right as NbtByteArray).content.contentToString())
            is NbtIntArray -> compareArray(path, left.content.contentToString(), (right as NbtIntArray).content.contentToString())
            is NbtLongArray -> compareArray(path, left.content.contentToString(), (right as NbtLongArray).content.contentToString())
            is NbtString -> compareValue(path, left.content, (right as NbtString).content)
            is NbtByte -> compareValue(path, left.content, (right as NbtByte).content)
            is NbtShort -> compareValue(path, left.content, (right as NbtShort).content)
            is NbtInt -> compareValue(path, left.content, (right as NbtInt).content)
            is NbtLong -> compareValue(path, left.content, (right as NbtLong).content)
            is NbtFloat -> compareValue(path, left.content, (right as NbtFloat).content)
            is NbtDouble -> compareValue(path, left.content, (right as NbtDouble).content)
        }
    }

    private fun compareArray(path: String, left: String, right: String): List<String> {
        return if (left == right) emptyList() else listOf("$path 数组值不同：$left != $right")
    }

    private fun compareValue(path: String, left: Any?, right: Any?): List<String> {
        return if (left == right) emptyList() else listOf("$path 值不同：$left != $right")
    }

    private fun renderTag(tag: NbtTag?): String = tag?.toString() ?: "null"

    private fun typeName(tag: NbtTag): String = tag::class.simpleName ?: tag.javaClass.name

    private data class Baseline(
        val data: SimpleData,
        val originalTag: NbtCompound,
    )
}
