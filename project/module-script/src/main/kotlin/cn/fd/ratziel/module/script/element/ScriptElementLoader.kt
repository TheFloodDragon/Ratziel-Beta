package cn.fd.ratziel.module.script.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.common.element.ElementLoader
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.module.script.ScriptType
import java.io.File

/**
 * ScriptElementLoader
 *
 * @author TheFloodDragon
 * @since 2025/8/11 14:54
 */
object ScriptElementLoader : ElementLoader {

    override fun accepts(workspace: Workspace, file: File): Boolean {
        return ScriptType.registry.any { it.extensions.contains(file.extension) }
    }

    override fun load(workspace: Workspace, file: File): Result<List<Element>> {
        TODO("Not yet implemented")
    }

}