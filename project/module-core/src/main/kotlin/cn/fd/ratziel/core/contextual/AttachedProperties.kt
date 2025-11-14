@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.contextual

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * AttachedProperties
 *
 * @author TheFloodDragon
 * @since 2025/11/14 18:16
 */
open class AttachedProperties(protected open val properties: Map<Key<*>, Any?> = emptyMap()) {

    /**
     * 属性键
     */
    open class Key<T>(
        /** 属性键名称 **/
        val name: String,
        /** 获取默认值的函数 **/
        val getDefaultValue: AttachedProperties.() -> T,
    ) {
        override fun equals(other: Any?) = if (other is Key<*>) name == other.name else false
        override fun hashCode() = name.hashCode()
        override fun toString() = "Key($name)"
    }

    class CopiedKey<T>(
        source: Key<T>,
        getSourceProperties: AttachedProperties.() -> AttachedProperties?,
    ) : Key<T>(
        source.name,
        {
            val sourceProperties = getSourceProperties()
            if (sourceProperties == null) source.getDefaultValue(this)
            else sourceProperties[source]
        }
    )

    class PropertyKeyDelegate<T>(private val getDefaultValue: AttachedProperties.() -> T) : ReadOnlyProperty<Any?, Key<T>> {
        constructor(defaultValue: T) : this({ defaultValue })

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): Key<T> = Key(property.name, getDefaultValue)
    }

    class PropertyKeyCopyDelegate<T>(
        val source: Key<T>, val getSourceProperties: AttachedProperties.() -> AttachedProperties? = { null },
    ) : ReadOnlyProperty<Any?, Key<T>> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): Key<T> = CopiedKey(source, getSourceProperties)
    }

    /**
     * 获取对应属性键的值
     * @return 不存在时返回默认值
     */
    operator fun <T> get(key: Key<T>): T {
        @Suppress("UNCHECKED_CAST")
        return properties.getOrDefault(key, key.getDefaultValue) as T
    }

    /**
     * 获取对应属性键的值
     * @return 不存在时返回null
     */
    fun <T> getNoDefault(key: Key<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return properties[key]?.let { it as T }
    }

    /**
     * 获取对应属性键的值
     */
    inline operator fun <T> Key<T>.invoke(): T = get(this)

    /**
     * 创建可供编辑的副本
     */
    open fun toMutable(): Mutable = Mutable(listOf(this))

    fun <T> containsKey(key: Key<T>) = properties.containsKey(key)

    val entries get() = properties.entries

    fun isEmpty() = properties.isEmpty()

    override fun equals(other: Any?) = (other as? AttachedProperties)?.let { it.properties == properties } == true

    override fun hashCode() = properties.hashCode()

    companion object {

        @JvmStatic
        fun <T> key(defaultValue: T) = PropertyKeyDelegate(defaultValue)

        @JvmStatic
        fun <T> key(getDefaultValue: AttachedProperties.() -> T) = PropertyKeyDelegate(getDefaultValue)

        @JvmStatic
        fun <T> keyCopy(
            source: Key<T>, getSourceProperties: AttachedProperties.() -> AttachedProperties? = { null },
        ) = PropertyKeyCopyDelegate(source, getSourceProperties)

    }

    open class Mutable(baseProperties: Iterable<AttachedProperties> = emptyList()) : AttachedProperties() {

        /** 可变属性 **/
        override val properties: MutableMap<Key<*>, Any?> = LinkedHashMap<Key<*>, Any?>().apply {
            baseProperties.forEach { putAll(it.properties) }
        }

        /**
         * 创建不可变副本
         */
        open fun toImmutable() = AttachedProperties(HashMap(properties))

        // generic for all properties

        operator fun <T> Key<T>.invoke(v: T) {
            properties[this] = v
        }

        fun <T> Key<T>.put(v: T) {
            properties[this] = v
        }

        fun <T> Key<T>.putIfNotNull(v: T?) {
            if (v != null) {
                properties[this] = v
            }
        }

        fun <T> Key<T>.replaceOnlyDefault(v: T?) {
            if (!properties.containsKey(this) || properties[this] == this.getDefaultValue(AttachedProperties(properties))) {
                properties[this] = v
            }
        }

        fun <T> Key<T>.update(body: (T?) -> T?) {
            putIfNotNull(body(properties[this]?.let {
                @Suppress("UNCHECKED_CAST")
                it as T
            }))
        }

        // generic for lists

        fun <T> Key<in List<T>>.putIfAny(vals: Iterable<T>?) {
            if (vals?.any() == true) {
                properties[this] = vals as? List ?: vals.toList()
            }
        }

        operator fun <T> Key<in List<T>>.invoke(vararg vals: T) {
            append(vals.asIterable())
        }

        // generic for maps:

        @JvmName("putIfAny_map")
        fun <K, V> Key<in Map<K, V>>.putIfAny(vals: Iterable<Pair<K, V>>?) {
            if (vals?.any() == true) {
                properties[this] = vals.toMap()
            }
        }

        fun <K, V> Key<in Map<K, V>>.putIfAny(vals: Map<K, V>?) {
            if (vals?.isNotEmpty() == true) {
                properties[this] = vals
            }
        }

        operator fun <K, V> Key<Map<K, V>>.invoke(vararg vs: Pair<K, V>) {
            append(vs.asIterable())
        }

        // for strings and list of strings that could be converted from other types

        @JvmName("invoke_string_fqn_from_reflected_class")
        operator fun Key<String>.invoke(kclass: KClass<*>) {
            properties[this] = kclass.java.name
        }

        @JvmName("invoke_string_list_fqn_from_reflected_class")
        operator fun Key<in List<String>>.invoke(vararg kclasses: KClass<*>) {
            append(kclasses.map { it.java.name })
        }

        // direct manipulation - public - for usage in inline dsl methods and for extending dsl

        operator fun <T> set(key: Key<in T>, value: T) {
            properties[key] = value
        }

        fun <T> reset(key: Key<in T>) {
            properties.remove(key)
        }

        // appenders to list and map properties

        @JvmName("appendToList")
        fun <V> Key<in List<V>>.append(values: Iterable<V>) {
            val newValues = tolerantGet(this)?.let { it + values } ?: values.toList()
            properties[this] = newValues
        }

        fun <V> Key<in List<V>>.append(vararg values: V) {
            val newValues = tolerantGet(this)?.let { it + values } ?: values.toList()
            properties[this] = newValues
        }

        fun <K, V> Key<in Map<K, V>>.append(values: Map<K, V>) {
            val newValues = tolerantGet(this)?.let { it + values } ?: values
            properties[this] = newValues
        }

        @JvmName("appendToMap")
        fun <K, V> Key<in Map<K, V>>.append(values: Iterable<Pair<K, V>>) {
            val newValues = tolerantGet(this)?.let { it + values } ?: values.toMap()
            properties[this] = newValues
        }

        fun <V> Key<in List<V>>.transform(action: (V) -> V) {
            val newValues = tolerantGet(this)?.map(action) ?: emptyList()
            properties[this] = newValues
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T : Any> tolerantGet(key: Key<in T>): T? = properties[key]?.let { it as T }


        // include another builder
        operator fun <T : Mutable> T.invoke(body: T.() -> Unit) {
            this.body()
            this@Mutable.properties.putAll(this.properties)
        }

    }

}
