package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import net.kyori.adventure.text.Component
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 直接将 [ItemComponentHolder] 转化为 [ComponentHolderDSL] 以便使用
 */
fun ItemComponentHolder.dsl() = ComponentHolderDSL(this)

/**
 * 直接在 [ItemComponentHolder] 上使用 DSL 块
 */
inline fun ItemComponentHolder.dsl(block: ComponentHolderDSL.() -> Unit) = dsl().apply(block)

/**
 * ComponentHolderDSL
 * 
 * @author TheFloodDragon
 * @since 2026/3/22 00:07
 */
open class ComponentHolderDSL(holder: ItemComponentHolder) : ItemComponentHolder by holder {

    /** 物品自定义数据 **/
    var customData: NbtCompound by composed(ItemComponents.CUSTOM_DATA, setIfAbsent = true) { NbtCompound() }

    /** 物品自定义名称（覆盖默认名称，斜体显示）**/
    var displayName: Component? by composed(ItemComponents.DISPLAY_NAME)

    /** 物品基础名称（不覆盖，不显示为斜体）**/
    var itemName: Component? by composed(ItemComponents.ITEM_NAME)

    /** 物品描述 Lore **/
    var lore: List<Component> by composed(ItemComponents.LORE) { emptyList() }

    /** 物品最大耐久值 **/
    var maxDamage: Int? by composed(ItemComponents.MAX_DAMAGE)


    /**
     * 非空属性委托（有默认值）
     */
    class ComponentDelegate<T : Any>(
        val type: ItemComponentType<T>,
        val default: () -> T,
        private val setIfAbsent: Boolean = false,
    ) : ReadWriteProperty<ItemComponentHolder, T> {
        override fun setValue(thisRef: ItemComponentHolder, property: KProperty<*>, value: T) = thisRef.set(type, value)
        override fun getValue(thisRef: ItemComponentHolder, property: KProperty<*>): T {
            thisRef[type]?.let { return it }
            val def = default()
            if (setIfAbsent) thisRef[type] = def // 没有时设置
            return def
        }
    }

    /**
     * 可空属性委托（无默认值）
     */
    class NullableComponentDelegate<T : Any>(
        val type: ItemComponentType<T>,
    ) : ReadWriteProperty<ItemComponentHolder, T?> {
        override fun getValue(thisRef: ItemComponentHolder, property: KProperty<*>): T? = thisRef[type]
        override fun setValue(thisRef: ItemComponentHolder, property: KProperty<*>, value: T?) =
            if (value != null) thisRef.set(type, value) else thisRef.remove(type)
    }

    // DSL 函数
    fun <T : Any> composed(type: ItemComponentType<T>) = NullableComponentDelegate(type)
    fun <T : Any> composed(type: ItemComponentType<T>, setIfAbsent: Boolean = false, default: () -> T) = ComponentDelegate(type, default, setIfAbsent)

}