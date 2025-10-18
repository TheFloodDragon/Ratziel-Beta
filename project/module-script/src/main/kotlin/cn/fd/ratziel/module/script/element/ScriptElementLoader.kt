package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.common.element.registry.ElementRegistry
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.script.api.ScriptType
import kotlinx.serialization.json.JsonNull
import java.io.File

/**
 * ScriptElementLoader
 *
 * @author TheFloodDragon
 * @since 2025/8/11 14:54
 */
object ScriptElementLoader : ElementLoader {

    /** 脚本元素类型 **/
    val elementType by lazy { ElementRegistry.findType(ScriptElementHandler::class.java) }

    override fun accepts(file: File, workspace: Workspace): Boolean {
        return matchType(file.extension) != null || ScriptDescription.isDescriptionFile(file)
    }

    override fun load(file: File, workspace: Workspace): Result<List<Element>> {
        return Result.success(listOf(Element(file.path, elementType, file, JsonNull)))
    }

    /**
     * 匹配脚本文件类型
     */
    @JvmStatic
    fun matchType(extension: String) = ScriptType.registry.find { lang ->
        lang.extensions.any { it.equals(extension, true) }
    }

}