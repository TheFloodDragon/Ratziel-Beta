package cn.fd.ratziel.module.itemengine.nbt

import cn.fd.ratziel.core.function.MirrorClass
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.setProperty
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
        get() = if (isTiNBT()) (data as TiNBTList).map { toNBTData(it) } else getField(data)!!.map { toNBTData(it) }
        set(value) {
            data = if (isTiNBT()) TiNBTList(value.map { it.getAsTiNBT() }) else new(value.map { it.getAsNmsNBT() })
        }

    /**
     * 编辑列表
     * @param action 具体编辑
     * ps: 妈的NMS的NBTTagList写的是真的依托答辩
     * 各版本都不一样,麻烦的要死,最终只能这样了
     * 虽然说牺牲了很大的性能
     */
    fun edit(action: MutableList<NBTData>.() -> Unit) = this.apply {
        val list = content.toMutableList()
        action(list)
        content = list
    }

    constructor() : this(TiNBTList())

    companion object : MirrorClass<NBTList>() {

        @JvmStatic
        override val clazz: Class<out Any> by lazy { nmsClass("NBTTagList") }

        @JvmStatic
        override fun of(obj: Any) = NBTList(obj)

        /**
         * 索引标识符
         */
        const val INDEX_SIGN_START = "["
        const val INDEX_SIGN_END = "]"

        /**
         * NBTTagList#constructor()
         */
        @JvmStatic
        fun new() = clazz.invokeConstructor()

        @JvmStatic
        fun new(collection: Collection<Any?>) = clazz.invokeConstructor().also { setField(it, ArrayList(collection)) }

        internal val listFieldName = if (MinecraftVersion.isUniversal) "c" else "list"

        internal fun getField(nmsData: Any) = nmsData.getProperty<List<Any>>(listFieldName)

        internal fun setField(nmsData: Any, list: ArrayList<Any?>) = nmsData.setProperty(listFieldName, list)

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

        internal fun checkIndexed(string: String): Pair<String, Int>? =
            string.takeIf { it.endsWith(INDEX_SIGN_END) }?.run {
                val nodeName = substringBeforeLast(INDEX_SIGN_START)
                val index = substring(
                    lastIndexOf(INDEX_SIGN_START) + INDEX_SIGN_START.length,
                    lastIndexOf(INDEX_SIGN_END)
                ).toInt()
                nodeName to index
            }

    }

}