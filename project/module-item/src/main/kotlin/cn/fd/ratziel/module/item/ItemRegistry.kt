package cn.fd.ratziel.module.item

import cn.fd.ratziel.core.Priority
import cn.fd.ratziel.core.util.sortPriority
import cn.fd.ratziel.function.uncheck
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.api.builder.ItemTransformer
import cn.fd.ratziel.module.item.api.registry.ComponentRegistry
import cn.fd.ratziel.module.item.api.registry.ResolverRegistry
import cn.fd.ratziel.module.item.api.registry.SerializerRegistry
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * ItemRegistry - 物品注册表
 *
 * @author TheFloodDragon
 * @since 2024/6/25 13:17
 */
object ItemRegistry {

    /**
     * 注册组件
     */
    fun <T> register(type: Class<T>, transformer: ItemTransformer<out T>) = Component.register(type, transformer)

    /**
     * 注册解析器
     */
    fun register(resolver: ItemResolver) = Resolver.register(resolver)

    /**
     * 注册序列化器
     */
    fun register(serializer: ItemSerializer<*>) = Serializer.register(serializer)

    object Component : ComponentRegistry {

        /**
         * 物品组件注册表
         */
        internal val registry: MutableMap<Class<*>, ItemTransformer<*>> = ConcurrentHashMap()

        override fun <T> register(type: Class<T>, transformer: ItemTransformer<out T>) {
            registry[type] = transformer
        }

        override fun unregister(type: Class<*>) {
            registry.remove(type)
        }

        override fun <T> get(type: Class<T>): ItemTransformer<out T>? = uncheck(registry[type])

        @Suppress("UNCHECKED_CAST")
        fun getUnsafe(type: Class<*>): ItemTransformer<in Any>? = registry[type] as? ItemTransformer<in Any>

        override fun isRegistered(type: Class<*>) = registry.containsKey(type)

        override fun getRegistry() = registry

    }

    object Resolver : ResolverRegistry {

        /**
         * 物品序列化器注册表
         */
        internal val registry: MutableList<Priority<ItemResolver>> = CopyOnWriteArrayList()

        override fun register(resolver: ItemResolver, priority: Byte) {
            registry.add(Priority(priority, resolver))
        }

        override fun unregister(type: Class<out ItemResolver>) {
            for (element in registry) {
                if (element.value::class.java == type) registry.remove(element)
            }
        }

        override fun unregister(resolver: ItemResolver) {
            for (element in registry) {
                if (element.value == resolver) registry.remove(element)
            }
        }

        override fun <T : ItemResolver> getWithPriority(type: Class<T>): Priority<T>? = uncheck(registry.find { it::class.java == type })

        override fun isRegistered(type: Class<out ItemResolver>) = registry.find { it.value::class.java == type } != null

        override fun isRegistered(resolver: ItemResolver) = registry.find { it.value == resolver } != null

        override fun getResolvers() = registry.map { it.value }

        override fun getResolversWithPriority(): Collection<Priority<ItemResolver>> = registry

        fun getResolversSorted(): List<ItemResolver> = registry.sortPriority()

    }

    object Serializer : SerializerRegistry {

        /**
         * 物品序列化器注册表
         */
        internal val registry: MutableList<ItemSerializer<*>> = CopyOnWriteArrayList()

        override fun register(serializer: ItemSerializer<*>) {
            registry.add(serializer)
        }

        override fun unregister(type: Class<out ItemSerializer<*>>) {
            for (element in registry) {
                if (element::class.java == type) registry.remove(element)
            }
        }

        override fun unregister(serializer: ItemSerializer<*>) {
            registry.remove(serializer)
        }

        override fun <T : ItemSerializer<*>> get(type: Class<T>): T? = uncheck(registry.find { it::class.java == type })

        override fun isRegistered(type: Class<out ItemSerializer<*>>) = registry.find { it::class.java == type } != null

        override fun isRegistered(serializer: ItemSerializer<*>) = registry.contains(serializer)

        override fun getSerializers(): List<ItemSerializer<*>> = registry

    }

}