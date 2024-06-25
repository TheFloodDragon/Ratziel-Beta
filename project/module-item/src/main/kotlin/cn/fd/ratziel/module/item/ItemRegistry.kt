package cn.fd.ratziel.module.item

import cn.fd.ratziel.function.util.uncheck
import cn.fd.ratziel.module.item.api.ItemTransformer
import cn.fd.ratziel.module.item.api.builder.ItemSerializer
import cn.fd.ratziel.module.item.api.registry.ComponentRegistry
import cn.fd.ratziel.module.item.api.registry.SerializerRegistry
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemRegistry - 物品注册表
 *
 * @author TheFloodDragon
 * @since 2024/6/25 13:17
 */
object ItemRegistry {

    object Component : ComponentRegistry {

        /**
         * 物品组件注册表
         */
        internal val componentRegistry: MutableMap<Class<*>, ItemTransformer<*>> = ConcurrentHashMap()


        override fun <T> register(type: Class<T>, transformer: ItemTransformer<T>) {
            componentRegistry[type] = transformer
        }

        /**
         * 取消注册组件以及其转换器
         * @param type 组件类型 (组件类)
         */
        override fun unregister(type: Class<*>) {
            componentRegistry.remove(type)
        }

        /**
         * 判断此组件是否被注册过
         * @param type 组件类型 (组件类)
         */
        override fun isRegistered(type: Class<*>): Boolean {
            return componentRegistry.containsKey(type)
        }

        /**
         * 获取对应组件类型的转化器
         * @param type 组件类型 (组件类)
         * @return 对应组件类型的转化器, 当该组件未注册时, 返回空
         */
        override fun <T> getTransformer(type: Class<T>): ItemTransformer<T>? {
            val transformer = componentRegistry[type] ?: return null
            return uncheck(transformer)
        }

        override fun getMap(): Map<Class<*>, ItemTransformer<*>> {
            return componentRegistry
        }

    }

    object Serializer : SerializerRegistry {

        /**
         * 物品序列化器注册表
         */
        internal val serializerRegistry: MutableMap<Class<out ItemSerializer<*>>, ItemSerializer<*>> = ConcurrentHashMap()

        override fun register(serializer: ItemSerializer<*>) {
            serializerRegistry[serializer::class.java] = serializer
        }

        override fun unregister(type: Class<out ItemSerializer<*>>) {
            serializerRegistry.remove(type)
        }

        override fun unregister(serializer: ItemSerializer<*>) = unregister(serializer::class.java)

        override fun isRegistered(serializer: ItemSerializer<*>): Boolean {
            return serializerRegistry.containsValue(serializer)
        }

        override fun isRegistered(type: Class<out ItemSerializer<*>>): Boolean {
            return serializerRegistry.containsKey(type)
        }

        override fun getSerializers(): Collection<ItemSerializer<*>> {
            return serializerRegistry.values
        }

    }

}