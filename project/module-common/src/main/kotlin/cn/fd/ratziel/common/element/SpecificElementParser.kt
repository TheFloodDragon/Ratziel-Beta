package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.event.ElementLoadEvent
import cn.fd.ratziel.core.element.*
import cn.fd.ratziel.core.element.loader.ElementParser
import cn.fd.ratziel.core.util.callThenRun
import kotlinx.serialization.json.*
import java.io.File

/**
 * SpecificElementParser
 * 针对性元素加载器 - 固定元素类型
 *
 * @author TheFloodDragon
 * @since 2023/9/8 20:44
 */
class SpecificElementParser(val type: ElementType) : ElementParser {

    /**
     * 从JsonObject解析成Element
     */
    fun parse(jsonO: JsonObject, file: File? = null): List<Element> {
        // 成功加载的所有元素
        val successes: MutableList<Element> = mutableListOf()
        // 获取所有元素标识符
        jsonO.keys.forEach { id ->
            // 获取当前元素下的所有元素类型
            jsonO[id]?.jsonObject?.let { types ->
                types.keys.forEach { expression ->
                    // 元素加载事件
                    ElementLoadEvent(
                        // 初始化元素对象
                        Element(
                            id, file, type,
                            property = types[expression]
                        )
                    ).callThenRun { successes.add(it.element) }
                }
            }
        }
        return DefaultElementParser.exclude(successes)
    }

    fun parse(jsonE: JsonElement, file: File? = null): List<Element> {
        return parse(jsonE.jsonObject, file)
    }

}