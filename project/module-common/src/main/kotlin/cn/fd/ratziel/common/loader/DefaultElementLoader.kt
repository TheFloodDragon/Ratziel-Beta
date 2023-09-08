package cn.fd.ratziel.common.loader

import cn.fd.ratziel.common.LogLevel
import cn.fd.ratziel.common.debug
import cn.fd.ratziel.common.util.serializeToJson
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.common.event.ElementLoadEvent
import cn.fd.ratziel.common.event.ElementTypeMatchEvent
import cn.fd.ratziel.core.element.loader.FileElementLoader
import cn.fd.ratziel.core.element.type.ElementTypeMatcher
import cn.fd.ratziel.core.util.callThenRun
import kotlinx.serialization.json.jsonObject
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import java.io.File

/**
 * DefaultElementLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/22 16:38
 */
object DefaultElementLoader : FileElementLoader {

    override fun load(file: File): List<Element> {
        // 成功加载的所有元素
        val successes: MutableList<Element> = mutableListOf()
        try {
            debug("Loading file... ${file.name}", level = LogLevel.Higher)
            // 获取 Config (转换成Json)
            Configuration.loadFromFile(file).serializeToJson().jsonObject.let { conf ->
                // 获取所有元素标识符
                conf.keys.forEach { id ->
                    // 获取当前元素下的所有元素类型
                    conf[id]?.jsonObject?.let { types ->
                        types.keys.forEach { expression ->
                            // 匹配元素类型 (元素类型匹配事件)
                            ElementTypeMatchEvent(expression)
                                .callThenRun { ElementTypeMatcher.match(expression) }
                                ?.let { type ->
                                    // 元素加载事件
                                    ElementLoadEvent(
                                        // 初始化元素对象
                                        Element(
                                            id, file, type,
                                            property = types[expression]
                                        )
                                    ).callThenRun { successes.add(it.element) }
                                } ?: warning("Unknown element type: \"$expression\" !")
                        }
                    }
                }
            }

        } catch (e: Exception) {
            severe("Failed to load element form file: ${file.name}")
            e.printStackTrace()
        }
        return successes
            .distinctBy { // 防止表达式指向同一类型导致的有多个相同地址的元素
                Pair(it.id, it.type)
            }
    }

}