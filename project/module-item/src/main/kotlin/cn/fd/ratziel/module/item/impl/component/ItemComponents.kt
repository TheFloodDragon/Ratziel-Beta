package cn.fd.ratziel.module.item.impl.component

import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.ItemComponentType.Transformer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
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
    val DURABILITY = r("minecraft:max_damage", Int.serializer())


    // TODO .... more ... and .. more


    private fun <T : Any> r(name: String, serializer: KSerializer<T>, transformer: Transformer<T> = Transformer.NoTransformation()): ItemComponentType<T> {
        val type = ItemComponentType.Unverified(NamespacedIdentifier.fromString(name), serializer, transformer)
        this.registry.add(type)
        return type
    }

}