package cn.fd.ratziel.common.element.parser

import cn.fd.ratziel.common.element.parser.DefaultElementParser.buildElement
import cn.fd.ratziel.common.element.parser.DefaultElementParser.matchType
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.ElementType
import cn.fd.ratziel.core.element.api.ElementParser
import cn.fd.ratziel.core.util.callThenRun
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.io.File

/**
 * SpecificElementParser
 * 针对性元素加载器 - 固定元素类型
 *
 * @author TheFloodDragon
 * @since 2023/9/8 20:44
 */
class SpecificElementParser(val type: ElementType) : ElementParser {

    companion object {
        /**
         * 构造针对性元素加载器对象
         */
        fun build(expression: String): SpecificElementParser? =
            matchType(expression)?.let { SpecificElementParser(it) }
    }

    /**
     * 从JsonObject解析成Element
     */
    fun parse(jsonO: JsonObject, file: File? = null): List<Element> = mutableListOf<Element>().also { successes ->
        // 获取所有元素标识符
        jsonO.keys.forEach { id ->
            // 获取当前元素下的所有元素类型
            jsonO[id]?.jsonObject?.let { types ->
                types.keys.forEach { expression ->
                    // 构造元素对象
                    buildElement(
                        id, file, type,
                        property = types[expression]
                    ).callThenRun { successes.add(it.element) }
                }
            }
        }
    }.let { DefaultElementParser.exclude(it) }

    fun parse(jsonE: JsonElement, file: File? = null): List<Element> {
        return parse(jsonE.jsonObject, file)
    }

}