package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.element.parser.DefaultElementParser
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.FileElementLoader
import cn.fd.ratziel.core.serialization.baseJson
import taboolib.common.platform.function.severe
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File

/**
 * DefaultElementLoader
 *
 * @author TheFloodDragon
 * @since 2023/8/22 16:38
 */
object DefaultElementLoader : FileElementLoader {

    override fun load(file: File): List<Element> {
        try {
            // 获取 Config (转换成Json)
            Configuration.loadFromFile(file).serializeToJson().let { json ->
                return DefaultElementParser.parse(json)
            }
        } catch (e: Exception) {
            severe("Failed to load element form file: ${file.name}")
            e.printStackTrace()
        }
        return emptyList()
    }

    fun Configuration.serializeToJson() =
        baseJson.parseToJsonElement(this.apply { changeType(Type.JSON) }.saveToString())

}