package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.core.contextual.toAttachedProperties
import cn.fd.ratziel.core.element.*
import cn.fd.ratziel.core.util.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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
object DefaultElementLoader : ElementLoader {

    private const val INTERNAL_NODE = "_internal"

    override fun accepts(file: File, workspace: Workspace): Boolean {
        return Configuration.getTypeFromExtensionOrNull(file.extension) != null
    }

    override fun load(file: File, workspace: Workspace): Result<List<Element>> {
        return runCatching {
            parseElements(workspace, Configuration.loadFromFile(file).toJsonElement(), file)
        }.onFailure {
            severe("Failed to load element form file: ${file.name}")
            it.printStackTrace()
        }
    }

    /**
     * 解析 - 将 [JsonElement] 解析成 [Element]
     */
    fun parseElements(workspace: Workspace, json: JsonElement, file: File): List<Element> {
        if (json !is JsonObject) {
            warning("Invalid element config: $json")
            return emptyList()
        }

        val configuration = parseConfiguration(workspace, json[INTERNAL_NODE], file)
        val content = json.removeInternalNode()
        val configuredElementName = configuration[ElementConfiguration.elementName]?.let {
            if (it == FILE_NAME_ELEMENT_NAME) file.nameWithoutExtension else it
        }
        val resolvedConfiguredType = configuration[ElementConfiguration.elementType]?.takeUnless {
            it.equals("None", true)
        }?.let {
            resolveConfiguredType(it, file) ?: return emptyList()
        }

        if (configuredElementName != null) {
            if (resolvedConfiguredType == null) {
                warning("Element file '${file.name}' declares elementName but misses elementType after merging workspace and _internal configuration.")
                return emptyList()
            }
            return listOf(Element(configuredElementName, resolvedConfiguredType, file, content))
        }

        if (resolvedConfiguredType != null) {
            return content.map { (name, property) -> Element(name, resolvedConfiguredType, file, property) }
        }

        return buildList {
            for ((name, value) in content) {
                val element = value as? JsonObject ?: run {
                    warning("Cannot infer element type from: $value")
                    continue
                }
                val (type, property) = parseType(element) ?: continue
                add(Element(name, type, file, property))
            }
        }
    }

    private fun parseConfiguration(workspace: Workspace, internal: JsonElement?, file: File): ElementConfiguration {
        val internalObject = internal ?: return workspace.configuration
        if (internalObject !is JsonObject) {
            warning("Invalid _internal config in '${file.name}': $internalObject")
            return workspace.configuration
        }
        return ElementConfiguration(listOf(workspace.configuration, internalObject.toAttachedProperties(ElementConfiguration.GROUP)))
    }

    private fun resolveConfiguredType(expression: String, file: File): ElementType? {
        return try {
            ElementMatcher.matchType(expression)
        } catch (ex: IllegalStateException) {
            warning("Unknown merged elementType '$expression' in '${file.name}': ${ex.message}")
            null
        }
    }

    private fun JsonObject.removeInternalNode(): JsonObject {
        return if (INTERNAL_NODE in this) JsonObject(filterKeys { it != INTERNAL_NODE }) else this
    }

    private fun parseType(element: JsonObject): Pair<ElementType, JsonElement>? {
        val entry = element.entries.firstOrNull()
        if (entry == null) {
            warning("Cannot infer element type from empty element config: $element")
            return null
        }

        return try {
            ElementMatcher.matchType(entry.key) to entry.value
        } catch (ex: IllegalStateException) {
            warning(ex.message)
            null
        }
    }

}
