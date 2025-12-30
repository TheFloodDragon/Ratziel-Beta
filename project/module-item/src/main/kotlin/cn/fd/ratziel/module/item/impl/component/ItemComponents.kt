package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.ItemComponentType.Transformer
import cn.fd.ratziel.module.item.impl.component.internal.ComponentListTransformer
import cn.fd.ratziel.module.item.impl.component.internal.MessageComponentTransformer
import cn.fd.ratziel.module.item.internal.ItemSheet
import cn.fd.ratziel.module.item.internal.nms.NMSComponent
import cn.fd.ratziel.module.item.internal.serializers.MessageComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import taboolib.common.platform.function.debug
import taboolib.module.nms.MinecraftVersion
import java.util.concurrent.CopyOnWriteArraySet

/**
 * ItemComponents
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 22:27
 */
object ItemComponents {

    /**
     * 物品组件注册表
     */
    val registry: MutableCollection<ItemComponentType<*>> = CopyOnWriteArraySet()

    @JvmField
    val CUSTOM_DATA = r("custom-data", NbtCompound.serializer(), NMSComponent.INSTANCE.customDataComponentTransformer())

    @JvmField
    val DISPLAY_NAME = r("display-name", MessageComponentSerializer, MessageComponentTransformer)

    @JvmField
    val ITEM_NAME = r("item-name", MessageComponentSerializer, MessageComponentTransformer)

    @JvmField
    val LORE = r("lore", ListSerializer(MessageComponentSerializer), ComponentListTransformer(MessageComponentTransformer))

    @JvmField
    val DURABILITY = r("max-damage", Int.serializer())


    // TODO .... more ... and .. more

    init {
        debug(registry.map {
            Triple(it.identifier, it.transformer, it.serializer)
        })
    }

    private fun keyName(name: String): Identifier? {
        // 跨版本映射组件 ID
        val mapped = ItemSheet.mappings2[name] ?: name
        // 代表当前版本不支持
        if (mapped.isEmpty()) return null
        // 1.20.5 + 的格式为 minecraft:custom_data (NamespacedIdentifier)
        // 1.20.5- 的格式为 display.Name (NbtNodeIdentifier)
        return if (MinecraftVersion.versionId >= 12005) {
            NamespacedIdentifier.fromString(mapped)
        } else NbtNodeIdentifier(mapped)
    }

    private fun <T : Any> r(key: String, serializer: KSerializer<T>, transformer: Transformer<T> = Transformer.NoTransformation()): ItemComponentType<T> {
        val identifier = keyName(key) ?: return ItemComponentType.Unsupported(key)
        val type = ItemComponentType.Unverified(identifier, serializer, transformer, true)
        this.registry.add(type)
        return type
    }

}