package cn.fd.ratziel.module.item.impl.component

import taboolib.common.io.runningClassMapWithoutLibrary
import taboolib.module.nms.AsmClassTranslation

/**
 * MinecraftE2MTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/3/21 23:18
 */
interface MinecraftE2MTransformer<T> {

    /**
     * 将 封装的组件数据 转换为 Minecraft 组件对象
     */
    fun toMinecraftObj(encapsulated: T): Any

    /**
     * 将 Minecraft 组件对象 转换为 封装的组件数据
     */
    fun fromMinecraftObj(minecraftObj: Any): T

    companion object {

        private val proxyInstanceMap = mutableMapOf<String, MinecraftE2MTransformer<*>>()

        /**
         * 通过全限定类名反射构造 [MinecraftE2MTransformer]
         *
         * @param fullName 目标类的全限定类名
         */
        fun <T> of(fullName: String): MinecraftE2MTransformer<T> {
            // 从缓存中获取
            if (proxyInstanceMap.containsKey(fullName)) {
                @Suppress("UNCHECKED_CAST")
                return proxyInstanceMap[fullName] as MinecraftE2MTransformer<T>
            }
            // 获取合适的构造函数并创建实例
            fun <T> createInstance(clazz: Class<T>): T {
                // 获取空构造函数
                val constructor = clazz.declaredConstructors.find {
                    it.parameterTypes.size == 0
                }
                if (constructor != null) {
                    constructor.isAccessible = true
                    // 创建实例
                    @Suppress("UNCHECKED_CAST")
                    return constructor.newInstance() as T
                }
                throw NoSuchMethodException("没有找到空构造函数: ${clazz.name}")
            }

            // 生成代理类
            val proxyClass = AsmClassTranslation(fullName).createNewClass()
            // 同时生成所有的内部类
            runningClassMapWithoutLibrary.filter { (name, _) -> name.startsWith("$fullName$") }.forEach { (name, _) ->
                AsmClassTranslation(name).createNewClass()
            }
            // 创建实例
            @Suppress("UNCHECKED_CAST")
            val newInstance = createInstance(proxyClass) as MinecraftE2MTransformer<T>
            // 缓存实例
            proxyInstanceMap[fullName] = newInstance

            return newInstance
        }

    }

}