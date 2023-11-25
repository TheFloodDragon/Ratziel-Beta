@file:Suppress("IMPLICIT_CAST_TO_ANY")

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass
import cn.fd.ratziel.core.util.getMethodUnsafe
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass

/**
 * NBT别名
 */
typealias NBTTag = NBTCompound

/**
 * NBTCompound - 复合NBT类型
 *
 * @author TheFloodDragon
 * @since 2023/11/24 21:53
 */
open class NBTCompound(rawData: Any) : NBTData(rawData, NBTDataType.COMPOUND) {

    constructor() : this(TiNBTTag())

    /**
     * 获取数据
     * @param node 节点
     */
    operator fun get(node: String) =
        (if (isTiNBT()) (data as TiNBTTag)[node]
        else NMSMethods.getMethod.invoke(data, node))?.let { toNBTData(it) }

    /**
     * 深度获取
     * @param node 节点
     */
    fun getDeep(node: String) =
        (if (isTiNBT()) (data as TiNBTTag).getDeep(node)
        else getDeepWith(node, false) { it[node.substringAfterLast('.')] })?.let { toNBTData(it) }

    /**
     * 写入数据
     * @param node 节点
     * @param value NBT数据
     */
    fun put(node: String, value: NBTData) {
        if (isTiNBT()) (data as TiNBTTag)[node] = value.getAsTiNBT()
        else NMSMethods.putMethod.invoke(data, node, value.getAsNmsNBT())
    }

    fun put(node: String, value: Any) = put(node, toNBTData(value))

    operator fun set(node: String, value: NBTData) = put(node, value)

    operator fun set(node: String, value: Any) = put(node, value)

    /**
     * 深度写入
     */
    fun putDeep(node: String, value: NBTData) {
        if (isTiNBT()) (data as TiNBTTag).putDeep(node, value.getAsTiNBT())
        else getDeepWith(node, true) { it.put(node.substringAfterLast('.'), value) }
    }

    fun putDeep(node: String, value: Any) = putDeep(node, toNBTData(value))

    /**
     * 针对"深度方法"的重复代码做出的优化
     */
    fun getDeepWith(key: String, create: Boolean, action: (NBTCompound) -> Any?): NBTData? {
        val keys = key.split(DEEP_SEPARATION).dropLast(1)
        if (keys.isEmpty()) {
            return null
        }
        var find: NBTCompound = this
        for (element in keys) {
            var next = find[element]
            if (next == null) {
                if (create) {
                    next = NBTCompound()
                    find[element] = next
                } else {
                    return null
                }
            }
            if (next.type == NBTDataType.COMPOUND) {
                find = next as NBTCompound
            } else {
                return null
            }
        }
        return action(find) as? NBTData
    }

    companion object : MirrorClass<NBTCompound>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagCompound") }

        @JvmStatic
        override fun of(obj: Any) = NBTCompound(obj)

        /**
         * 深度操作分层符
         */
        const val DEEP_SEPARATION = "."

        /**
         * NBTTagCompound#constructor()
         */
        @JvmStatic
        fun new() = clazz.invokeConstructor()

        /**
         * NBTTagCompound#constructor(Map<String,NBTBase>)
         */
        @JvmStatic
        fun new(map: HashMap<String, Any>) = clazz.invokeConstructor(map)

        /**
         * 合并两个 NmsNBT 并返回值
         * @param replace 是否替换原有的标签
         * @return 合并后的 NmsNBT
         */
        @JvmStatic
        @Deprecated("正在考虑别的方法")
        fun merge(source: Any, target: Any, replace: Boolean = true): Any {
            val fieldName = if (MinecraftVersion.isUniversal) "x" else "map"
            return new(HashMap(source.getProperty<Map<String, Any>>(fieldName)!!).also { sourceMap ->
                val targetMap = target.getProperty<Map<String, Any>>(fieldName)
                targetMap?.forEach { (key, value) ->
                    val origin = sourceMap[key]
                    if (value::class.java.isAssignableFrom(clazz)) {
                        sourceMap[key] = merge(origin ?: new(), value, replace)
                    } else {
                        if (origin == null || replace) sourceMap[key] = value
                    }
                }
            })
        }

    }

    object NMSMethods {

        private val classStructure by lazy { ReflexClass.of(clazz).structure }

        internal val getMethod by lazy {
            classStructure.getMethodUnsafe(
                name = if (MinecraftVersion.isUniversal) "c" else "get",
                String::class.java
            )
        }

        internal val putMethod by lazy {
            classStructure.getMethodUnsafe(
                name = if (MinecraftVersion.isUniversal) "a" else "set",
                String::class.java, classNBTBase
            )
        }

    }

}