package cn.fd.ratziel.kether

import kotlinx.serialization.json.*

/**
 * KetherCompiler
 *
 * @author TheFloodDragon
 * @since 2023/9/2 13:01
 */
//@Deprecated("Use BlockBuilder")
object KetherCompiler {

    fun buildSection(section: Any, builder: StringBuilder = StringBuilder()): StringBuilder {
        when (section) {
            is JsonElement -> when (section) {
                is JsonObject -> {
                    val type = section["type"]?.jsonPrimitive?.content ?: "kether"
                    val content = section["value"]?.let { value -> buildSection(value).toString() }?.let { context ->
                        buildContent(context, type)
                    } ?: return builder

                    if (builder.isNotEmpty()) {
                        // 此前含有其他内容，换行隔开
                        builder.append('\n')
                    }
                    builder.append(content)
                }

                is JsonArray -> {
                    section.forEach { jsonElement ->
                        buildSection(jsonElement, builder)
                    }
                }

                is JsonPrimitive -> {
                    if (section.isString)
                        buildSection(section.content, builder)
                }
            }

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