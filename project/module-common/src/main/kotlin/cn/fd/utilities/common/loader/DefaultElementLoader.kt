package cn.fd.utilities.common.loader

import cn.fd.utilities.common.debug
import cn.fd.utilities.common.util.ConfigUtil
import cn.fd.utilities.core.element.Element
import cn.fd.utilities.core.element.api.loader.FileElementLoader
import cn.fd.utilities.core.element.type.ElementTypeMatcher
import kotlinx.serialization.json.jsonObject
import taboolib.common.platform.function.severe
import taboolib.common.platform.function.warning
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
            debug("Loading file... ${file.name}")
            // 获取 Config (转换成Json)
            ConfigUtil.loadFromFile(file)?.let { ConfigUtil.serializeToJson(it).jsonObject }?.let { conf ->
                // 获取所有元素标识符
                conf.keys.forEach { id ->
                    // 获取当前元素下的所有元素类型
                    conf[id]?.jsonObject?.let { types ->
                        types.keys.forEach { expression ->
                            //匹配元素类型
                            ElementTypeMatcher.match(expression).let { type ->
                                type?.let {
                                    successes.add(
                                        Element(
                                            id, file, it,
                                            property = types[expression]
                                        )
                                    )
                                }
                            } ?: warning("Unknown element type: \"$expression\" !")
                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            severe("Failed to load element form file: ${file.name} !")
        }
        return successes
            .distinctBy { // 防止表达式指向同一类型导致的有多个相同地址的元素
                Pair(it.id, it.type)
            }
    }

}