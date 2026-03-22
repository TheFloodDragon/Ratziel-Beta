@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.contextual

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.serializer
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * AttachedProperties
 *
 * @author TheFloodDragon
 * @since 2025/11/14 18:16
 */
@Serializable(with = AttachedPropertiesKSerializer::class)
open class AttachedProperties(protected open val properties: Map<Key<*>, Any?> = emptyMap()) {

    constructor(another: AttachedProperties) : this(another.properties.toMap())

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

    /**
     * 可序列化的属性键
     */
    open class SerialKey<T>(
        name: String,
        /** 序列化名称 **/
        val serialName: String,
        /** 反序列化别名 **/
        val alias: Set<String> = emptySet(),
        /** 序列化器 **/
        val serializer: KSerializer<T>,
        getDefaultValue: AttachedProperties.() -> T,
    ) : Key<T>(name, getDefaultValue) {
        override fun toString() = "SerialKey($name:$serialName, aliases=$alias)"
    }

    /**
     * 获取对应属性键的值
     * @return 不存在时返回默认值
     */
    operator fun <T> get(key: Key<T>): T {
        return if (properties.containsKey(key)) {
            @Suppress("UNCHECKED_CAST")
            properties[key] as T
        } else key.getDefaultValue(this)
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

    class PropertyKeyDelegate<T>(private val getDefaultValue: AttachedProperties.() -> T) : ReadOnlyProperty<Any?, Key<T>> {
        override operator fun getValue(thisRef: Any?, property: KProperty<*>) = Key(property.name, getDefaultValue)
    }

    class PropertySerialKeyDelegate<T>(
        private val serializer: KSerializer<T>,
        private val getDefaultValue: AttachedProperties.() -> T,
    ) : ReadOnlyProperty<Any?, SerialKey<T>> {

        private lateinit var key: SerialKey<T>

        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): PropertySerialKeyDelegate<T> {
            key = AttachedPropertiesSerialRegistry.register(createKey(property))
            return this
        }

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): SerialKey<T> = key

        private fun createKey(property: KProperty<*>) = SerialKey(
            name = property.name,
            serialName = property.annotations.filterIsInstance<SerialName>().firstOrNull()?.value ?: property.name,
            alias = property.annotations
                .filterIsInstance<JsonNames>()
                .flatMapTo(LinkedHashSet()) { it.names.asIterable() },
            serializer = serializer,
            getDefaultValue = getDefaultValue,
        )
    }

    class PropertyValueDelegate<T>(private val getDefaultValue: AttachedProperties.() -> T) : ReadWriteProperty<Mutable, T> {
        override operator fun getValue(thisRef: Mutable, property: KProperty<*>) = thisRef[Key(property.name, getDefaultValue)]
        override fun setValue(thisRef: Mutable, property: KProperty<*>, value: T) = thisRef.set(Key(property.name, getDefaultValue), value)
    }

    companion object {

        @JvmStatic
        fun <T> key(defaultValue: T) = PropertyKeyDelegate { defaultValue }

        @JvmStatic
        fun <T> key(getDefaultValue: AttachedProperties.() -> T) = PropertyKeyDelegate(getDefaultValue)

        @JvmStatic
        fun <T> serialKey(serializer: KSerializer<T>, defaultValue: T) =
            PropertySerialKeyDelegate(serializer) { defaultValue }

        @JvmStatic
        fun <T> serialKey(
            serializer: KSerializer<T>,
            getDefaultValue: AttachedProperties.() -> T,
        ) = PropertySerialKeyDelegate(serializer, getDefaultValue)

        @JvmStatic
        inline fun <reified T> serialKey(defaultValue: T) =
            serialKey(serializer<T>(), defaultValue)

        @JvmStatic
        inline fun <reified T> serialKey(
            noinline getDefaultValue: AttachedProperties.() -> T,
        ) = serialKey(serializer<T>(), getDefaultValue)

        @JvmStatic
        fun <T> value(getDefaultValue: AttachedProperties.() -> T) = PropertyValueDelegate(getDefaultValue)

    }

    open class Mutable(baseConfigurations: Iterable<AttachedProperties> = emptyList()) : AttachedProperties() {

        /** 可变属性 **/
        override val properties: MutableMap<Key<*>, Any?> = LinkedHashMap<Key<*>, Any?>().apply {
            baseConfigurations.forEach { putAll(it.properties) }
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
