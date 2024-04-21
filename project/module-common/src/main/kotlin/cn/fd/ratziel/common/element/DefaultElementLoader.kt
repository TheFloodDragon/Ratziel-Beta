package cn.fd.ratziel.common.element

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementIdentifier
import cn.fd.ratziel.core.element.api.ElementLoader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

/**
 * DefaultElementLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/22 16:38
 */
object DefaultElementLoader : ElementLoader {

    override fun load(file: File): List<Element> {
        try {
            // 从文件中加载配置
            val config = Configuration.loadFromFile(file)
            // 转化成Json对象
            config.changeType(Type.JSON)
            val json = Json.parseToJsonElement(config.saveToString())
            // 一般解析
            return this.parseDefault(json, file)
        } catch (e: Exception) {
            severe("Failed to load element form file: ${file.name}")
            e.printStackTrace()
            return emptyList() // 失败时返回空列表
        }
    }

    /**
     * 默认解析 - 将[JsonElement]解析成[Element]
     */
    fun parseDefault(json: JsonElement, file: File? = null) = buildList {
        // 检查有效性
        if (json !is JsonObject) {
            warning("Invalid element config: $json")
            return@buildList
        }
        // 获取元素名称
        json.forEach { name, value ->
            // 获取元素类型及其内容
            when (value) {
                is JsonObject -> value.entries
                is JsonArray -> value.mapNotNull { it as? JsonObject }.flatMap { it.entries }
                else -> {
                    warning("Cannot infer element type from: $value"); emptyList()
                }
            }.forEach { (expression, content) ->
                // 匹配元素类型
                val type = try {
                    ElementMatcher.matchType(expression)
                } catch (ex: IllegalStateException) {
                    warning(ex.message);null // 失败时警告
                }
                if (type != null) {
                    // 构造对象
                    add(Element(ElementIdentifier(name, type, file), content))
                }
            }
        }
    }.excludeDuplicates()

    /**
     * 排除重复的元素 (防止表达式指向同一类型导致的有多个相同地址的元素)
     */
    private fun Iterable<Element>.excludeDuplicates() = this.distinctBy { it.name to it.type }

}