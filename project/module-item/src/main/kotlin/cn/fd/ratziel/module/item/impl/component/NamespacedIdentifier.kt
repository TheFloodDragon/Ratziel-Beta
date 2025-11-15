package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.internal.serializers.NamespacedIdentifierSerializer
import kotlinx.serialization.Serializable
import taboolib.module.nms.MinecraftVersion

/**
 * NamespacedIdentifier
 *
 * Copied from [org.bukkit.NamespacedKey].
 *
 * @author TheFloodDragon
 * @since 2025/6/7 08:28
 */
@Serializable(NamespacedIdentifierSerializer::class)
data class NamespacedIdentifier(
    /**
     * 命名空间
     */
    val namespace: String,
    /**
     * ID 标识符
     */
    val key: String,
) {

    val content get() = "$namespace:$key"

    constructor(namespacedKey: org.bukkit.NamespacedKey) : this(namespacedKey.namespace, namespacedKey.key)

    override fun toString() = content

    companion object {

        const val MINECRAFT: String = "minecraft"

        /**
         * Minecraft 默认命名空间的 [NamespacedIdentifier]
         */
        @JvmStatic
        fun minecraft(identifier: String): NamespacedIdentifier {
            return NamespacedIdentifier(MINECRAFT, identifier)
        }

        /**
         * 从字符串中解析 [NamespacedIdentifier]
         */
        @JvmStatic
        fun fromString(identifier: String): NamespacedIdentifier {
            return fromString(identifier, MINECRAFT)
                ?: throw IllegalArgumentException("Unknown namespaced identifier: '$identifier'")
        }

        /**
         * 从字符串中解析 [NamespacedIdentifier]
         */
        @JvmStatic
        fun fromStringOrLegacy(identifier: String, version: Int): NamespacedIdentifier {
            return if (MinecraftVersion.versionId >= version) {
                fromString(identifier)
            } else minecraft(identifier)
        }

        @JvmStatic
        fun fromString(string: String, defaultNamespace: String): NamespacedIdentifier? {
            val components = string.split(":".toRegex(), limit = 3).toTypedArray()
            if (components.size > 2) {
                return null
            }
            val key = if (components.size == 2) components[1] else ""
            when (components.size) {
                1 -> {
                    val value = components[0]
                    return if (!value.isEmpty() && isValidKey(value)) {
                        NamespacedIdentifier(defaultNamespace, value)
                    } else {
                        null
                    }
                }

                2 if !isValidKey(key) -> return null
                else -> {
                    val namespace = components[0]
                    return if (namespace.isEmpty()) {
                        NamespacedIdentifier(defaultNamespace, key)
                    } else {
                        if (!isValidNamespace(namespace)) null else NamespacedIdentifier(namespace, key)
                    }
                }
            }
        }

        @JvmStatic
        private fun isValidKey(key: String): Boolean {
            val len = key.length
            if (len == 0) {
                return false
            } else {
                for (i in 0..<len) {
                    if (!isValidKeyChar(key[i])) {
                        return false
                    }
                }
                return true
            }
        }

        @JvmStatic
        private fun isValidNamespace(namespace: String): Boolean {
            val len = namespace.length
            if (len == 0) {
                return false
            } else {
                for (i in 0..<len) {
                    if (!isValidNamespaceChar(namespace[i])) {
                        return false
                    }
                }
                return true
            }
        }

        @JvmStatic
        private fun isValidNamespaceChar(c: Char): Boolean {
            return c in 'a'..'z' || c in '0'..'9' || c == '.' || c == '_' || c == '-'
        }

        @JvmStatic
        private fun isValidKeyChar(c: Char): Boolean {
            return isValidNamespaceChar(c) || c == '/'
        }

    }

}