@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.module.item.impl.component

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.type.HideFlag
import cn.fd.ratziel.module.item.impl.component.type.ItemEnchantmentMap
import net.kyori.adventure.text.Component
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 直接将 [ItemComponentHolder] 转化为 [ComponentComposer] 以便使用
 */
inline fun ItemComponentHolder.compose() = ComponentComposer(this)

/**
 * 直接在 [ItemComponentHolder] 上使用 DSL 块
 */
inline fun ItemComponentHolder.compose(block: ComponentComposer.() -> Unit) = ComponentComposer(this).apply(block)

/**
 * ComponentComposer
 * 
 * @author TheFloodDragon
 * @since 2026/3/22 00:07
 */
open class ComponentComposer(holder: ItemComponentHolder) : ItemComponentHolder by holder {

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

    /** 物品修复消耗 **/
    var repairCost: Int? by composed(ItemComponents.REPAIR_COST)

    /** 物品附魔 **/
    var enchantments: ItemEnchantmentMap by composed(ItemComponents.ENCHANTMENTS) { ItemEnchantmentMap() }

    /** 物品隐藏标签 **/
    var hideFlags: Set<HideFlag> by composed(ItemComponents.HIDE_FLAGS) { emptySet() }

    /** 是否覆盖附魔光效显示 (仅 1.20.5+) **/
    var glintOverride: Boolean by composed(ItemComponents.GLINT_OVERRIDE) { false }

    /** 物品是否不可破坏 **/
    var unbreakable: Boolean by composed(ItemComponents.UNBREAKABLE) { false }

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