package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.util.MirrorClass
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.module.nms.MinecraftVersion

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

    companion object : MirrorClass<NBTCompound>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy {
            refNBTClass("NBTTagCompound")
        }

        @JvmStatic
        override fun of(obj: Any) = NBTCompound(obj)

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

}