package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.internal.serializers.NamespacedIdentifierSerializer
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey

/**
 * NamespacedIdentifier
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
    val identifier: String,
) {

    constructor(namespacedKey: NamespacedKey) : this(namespacedKey.namespace, namespacedKey.key)

    override fun toString() = "$namespace:$identifier"

    companion object {

        const val MINECRAFT: String = "minecraft"

        /**
         * 从字符串中解析 [NamespacedIdentifier]
         */
        fun fromString(identifier: String): NamespacedIdentifier {
            val namespacedKey = NamespacedKey.fromString(identifier)
                ?: throw IllegalArgumentException("Unknown namespaced identifier: '$identifier'")
            return NamespacedIdentifier(namespacedKey)
        }

        /**
         * Minecraft 默认命名空间的 [NamespacedIdentifier]
         */
        fun minecraft(identifier: String): NamespacedIdentifier {
            return NamespacedIdentifier(MINECRAFT, identifier)
        }

    }

}