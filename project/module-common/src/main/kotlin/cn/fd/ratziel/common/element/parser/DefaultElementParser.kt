package cn.fd.ratziel.common.element.parser

import cn.fd.ratziel.common.element.ElementTypeMatcher
import cn.fd.ratziel.common.event.ElementLoadEvent
import cn.fd.ratziel.common.event.ElementTypeMatchEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.api.ElementParser
import cn.fd.ratziel.core.serialization.emptyJson
import cn.fd.ratziel.core.util.callThenRun
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import taboolib.common.platform.function.warning
import java.io.File

/**
 * DefaultElementParser
 * 默认元素解析器 - Json
 *
 * @author TheFloodDragon
 * @since 2023/9/8 20:34
 */
object DefaultElementParser : ElementParser {

    /**
     * 从JsonObject解析成Element
     */
    fun parse(jsonO: JsonObject, file: File? = null): List<Element> = mutableListOf<Element>().also { successes ->
        // 获取所有元素标识符
        jsonO.keys.forEach { id ->
            // 获取当前元素下的所有元素类型
            jsonO[id]?.jsonObject?.let { types ->
                types.keys.forEach { expression ->
                    // 匹配元素类型
                    matchType(expression)?.let { type ->
                        // 构造元素对象
                        buildElement(
                            id, file, type,
                            property = types[expression]
                        ).callThenRun { successes.add(it.element) }
                    }
                }
            }
        }
    }.let { exclude(it) }

    fun parse(jsonE: JsonElement, file: File? = null): List<Element> {
        return parse(jsonE.jsonObject, file)
    }

    /**
     * 匹配元素类型  (元素类型匹配事件)
     */
    fun matchType(expression: String) =
        ElementTypeMatchEvent(expression)
            .callThenRun { ElementTypeMatcher.match(expression) }
            ?: null.also { warning("Unknown element type: \"$expression\" !") }

    /**
     * 创建元素对象 (实际上就是包装了事件)
     */
    fun buildElement(id: String, file: File?, type: ElementType, property: JsonElement?) =
        // 元素加载事件
        ElementLoadEvent(
            // 初始化元素对象
            Element(id, type, file, property ?: emptyJson())
        )

    /**
     * 防止表达式指向同一类型导致的有多个相同地址的元素
     */
    fun exclude(list: Iterable<Element>): List<Element> {
        return list.distinctBy { Pair(it.name, it.type) }
    }

}