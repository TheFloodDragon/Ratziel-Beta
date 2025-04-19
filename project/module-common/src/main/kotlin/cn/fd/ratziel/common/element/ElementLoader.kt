package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import java.util.*

/**
 * ElementLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/22 16:38
 */
object ElementLoader {

    fun load(workspace: Workspace, file: File): List<Element> {
        try {
            // 从文件中加载配置
            val config = Configuration.loadFromFile(file)
            // 转化成Json对象
            config.changeType(Type.JSON)
            val json = Json.parseToJsonElement(config.saveToString())
            // 一般解析
            return this.parseElements(workspace, json, file)
        } catch (e: Exception) {
            severe("Failed to load element form file: ${file.name}")
            e.printStackTrace()
            return emptyList() // 失败时返回空列表
        }
    }

    /**
     * 解析 - 将 [JsonElement] 解析成 [Element]
     */
    fun parseElements(workspace: Workspace, json: JsonElement, file: File): List<Element> {
        // 检查有效性
        if (json !is JsonObject) {
            warning("Invalid element config: $json")
            return emptyList()
        }

        if (workspace.useFileNameAsElementName) {
            // 获取文件名称
            val name = file.nameWithoutExtension
            // 匹配元素类型
            val pair = parseType(workspace, json) ?: return emptyList()
            return Collections.singletonList(Element(name, pair.first, file, pair.second))
        } else {
            val list = ArrayList<Element>()
            for (entry in json) {
                val value = entry.value as? JsonObject
                val pair = value?.let { parseType(workspace, it) }
                if (pair != null) {
                    val element = Element(entry.key, pair.first, file, pair.second)
                    list.add(element)
                } else warning("Cannot infer element type from: $value")
            }
            return list
        }
    }

    private fun parseType(workspace: Workspace, element: JsonObject): Pair<ElementType, JsonElement>? {
        if (workspace.unifiedElementType != null) {
            return workspace.unifiedElementType to element
        } else {
            val entry = element.entries.first()
            try {
                return ElementMatcher.matchType(entry.key) to entry.value
            } catch (ex: IllegalStateException) {
                warning(ex.message) // 失败时警告
                return null
            }
        }
    }

}