@file:Suppress("UNCHECKED_CAST")

package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
import cn.fd.ratziel.core.function.getFieldUnsafe
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsClass

/**
 * NBTList - NBT列表
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:18
 */
open class NBTList(rawData: Any) : NBTData(
    when (rawData) {
        is List<*> -> adaptData(rawData)
        is Array<*> -> adaptData(rawData.asIterable())
        else -> rawData
    },
    NBTDataType.LIST
) {

    var content: List<NBTData>
        get() = if (isTiNBT()) getTiList().map { toNBTData(it) } else getNmsList().mapNotNull { toNBTData(it) }
        set(value) {
            data = if (isTiNBT()) TiNBTList(value.map { it.getAsTiNBT() }) else new(value.map { it.getAsNmsNBT() })
        }

    operator fun get(index: Int) = toNBTData(if (isTiNBT()) getTiList()[index] else getNmsList()[index])

    fun add(index: Int, data: NBTData) =
        if (isTiNBT()) getTiList().add(index, data.getAsTiNBT()) else getNmsList().add(index, data)

    fun add(data: NBTData) = if (isTiNBT()) getTiList().add(data.getAsTiNBT()) else getNmsList().add(data)

    fun remove(index: Int) = if (isTiNBT()) getTiList().removeAt(index) else getNmsList().removeAt(index)

    operator fun set(index: Int, data: NBTData) =
        if (isTiNBT()) getTiList().set(index, data.getAsTiNBT()) else getNmsList().set(index, data)

    fun clear() = if (isTiNBT()) getTiList().clear() else getNmsList().clear()

    fun getNmsList() = listField.get(getAsNmsNBT()) as ArrayList<Any?>

    fun getTiList() = getAsTiNBT() as MutableList<TiNBTData>

    // Kotlin 操作符
    operator fun plus(data: NBTData) = add(data)
    operator fun minus(index: Int) = remove(index)

    constructor() : this(TiNBTList())

    companion object : MirrorClass<NBTList>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagList") }

        @JvmStatic
        override fun of(obj: Any) = NBTList(obj)

        /**
         * NBTTagList#constructor()
         */
        @JvmStatic
        fun new() = clazz.invokeConstructor()

        @JvmStatic
        fun new(collection: Collection<Any?>) = new(ArrayList(collection))

        @JvmStatic
        fun new(list: ArrayList<Any?>) = new().also { listField.set(it, list) }

        internal val listField by lazy {
            ReflexClass.of(clazz).structure.getFieldUnsafe(
                name = if (MinecraftVersion.isUniversal) "c" else "list",
                List::class.java
            )
        }

        /**
         * 对数据进行适配以符合标准
         */
        internal fun adaptData(rawData: Iterable<*>): Any =
            // 先转换成 NBTData 的列表
            rawData.mapNotNull { toNBTData(it) }.run {
                // 判断第一个 NBTData
                if (firstOrNull()?.isNmsNBT() == true) new(this.map { it.getAsNmsNBT() })
                else TiNBTList().also { list -> this.forEach { list += it.getAsTiNBT() } }
            }

    }

}