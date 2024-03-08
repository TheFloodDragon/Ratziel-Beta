@file:Suppress("UNCHECKED_CAST")

package cn.fd.ratziel.module.itemengine.nbt

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

    open var content: List<NBTData>
        get() = if (isTiNBT()) getTiList().map { toNBTData(it) } else getNmsList().mapNotNull { toNBTData(it) }
        set(value) {
            data = if (isTiNBT()) TiNBTList(value.map { it.getAsTiNBT() }) else new(value.map { it.getAsNmsNBT() })
        }

    /**
     * 获取数据
     */
    open operator fun get(index: Int) = toNBTData(if (isTiNBT()) getTiList()[index] else getNmsList()[index])

    /**
     * 在索引处添加数据
     */
    open fun add(index: Int, data: NBTData) =
        if (isTiNBT()) getTiList().add(index, data.getAsTiNBT()) else getNmsList().add(index, data.getAsNmsNBT())

    /**
     * 在末尾添加数据
     */
    open fun add(data: NBTData) =
        if (isTiNBT()) getTiList().add(data.getAsTiNBT()) else getNmsList().add(data.getAsNmsNBT())

    /**
     * 删除数据
     */
    open fun remove(index: Int) = if (isTiNBT()) getTiList().removeAt(index) else getNmsList().removeAt(index)

    /**
     * 设置索引处的数据
     */
    open operator fun set(index: Int, data: NBTData) = set(index, data, true)

    open fun set(index: Int, data: NBTData, create: Boolean) = (if (isTiNBT()) getTiList() else getNmsList()).let {
        // 若索引等于列表长度(允许创建时), 将数据添加在末尾
        if (index == it.size && create) add(data) else setGenerally(index, data)
    }

    open fun setGenerally(index: Int, data: NBTData) =
        if (isTiNBT()) getTiList().set(index, data.getAsTiNBT()) else getNmsList().set(index, data.getAsNmsNBT())

    /**
     * 清空列表
     */
    open fun clear() = if (isTiNBT()) getTiList().clear() else getNmsList().clear()

    /**
     * 获取 NMS 形式的列表
     */
    open fun getNmsList() = listField.get(getAsNmsNBT()) as ArrayList<Any?>

    /**
     * 获取 Ti 形式的数据
     */
    open fun getTiList() = getAsTiNBT() as MutableList<TiNBTData>

    // Kotlin 操作符
    open operator fun plus(data: NBTData) = add(data)
    open operator fun minus(index: Int) = remove(index)

    constructor() : this(TiNBTList())

    companion object {

        @JvmStatic
        val clazz by lazy { nmsClass("NBTTagList") }

        @JvmStatic
        fun of(obj: Any) = NBTList(obj)

        @JvmStatic
        fun new() = clazz.invokeConstructor()

        @JvmStatic
        fun new(collection: Collection<Any?>) = new(ArrayList(collection))

        @JvmStatic
        fun new(list: ArrayList<Any?>) = new().also { listField.set(it, list) }

        // private final List<NBTBase> c
        // private List<NBTBase> list = Lists.newArrayList();
        internal val listField by lazy {
            ReflexClass.of(clazz).structure.getFieldUnsafe(
                name = if (MinecraftVersion.isUniversal) "c" else "list",
                type = List::class.java
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