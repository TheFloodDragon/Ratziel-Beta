package cn.fd.ratziel.kether

import cn.fd.ratziel.core.serialization.JsonAdaptBuilder.Companion.adaptBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * KetherCompiler
 *
 * @author TheFloodDragon
 * @since 2023/9/2 13:01
 */
@Deprecated("Use BlockBuilder")
object KetherCompiler {

    fun buildSection(section: Any, builder: StringBuilder = StringBuilder()): StringBuilder {
        when (section) {
            is JsonElement -> section.adaptBuilder {
                objectScope {
                    val type = it["type"]?.jsonPrimitive?.content ?: "kether"
                    val content = it["value"]?.let { value -> buildSection(value).toString() }?.let { context ->
                        buildContent(context, type)
                    } ?: return@objectScope

                    if (builder.isNotEmpty()) {
                        // 此前含有其他内容，换行隔开
                        builder.append('\n')
                    }
                    builder.append(content)
                }
                arrayScope {
                    it.forEach { jsonElement ->
                        buildSection(jsonElement, builder)
                    }
                }
                primitiveScope {
                    if (it.isString)
                        buildSection(it.content, builder)
                }
            }.run()

            is String -> {
                if (builder.isNotEmpty()) {
                    // 此前含有其他内容，换行隔开
                    builder.append('\n')
                }
                builder.append(section)
            }
        }
        return builder
    }

    fun buildContent(content: String, type: String = "kether"): String? {
        return when (type.lowercase()) {
            "ke", "kether" -> content
            "js", "javascript" -> "js '$content'"
            else -> null
        }
    }


}