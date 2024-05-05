package cn.fd.ratziel.module.item.nbt

import cn.fd.ratziel.core.exception.UnsupportedTypeException
import taboolib.module.nms.nmsClass

/**
 * ProxyNBTCompound - 代理 [NBTCompound]
 *
 * 用于 1.20.5+ 之后的 DataComponentPatch
 *
 * @author TheFloodDragon
 * @since 2024/5/5 12:34
 */
class ProxyNBTCompound(
    /**
     * PatchedDataComponentMap
     */
    private var pdcMap: Any = util.new(),
    /**
     * 原始的 [NBTCompound] 数据
     */
    rawData: Any = new()
) : NBTCompound(rawData) {

    init {
        if (!clazz.isAssignableFrom(pdcMap::class.java)) throw UnsupportedTypeException(data)
    }

    fun putValue(key: String, value: Any) {
        val type = util.search(key)
        if (type != null) {
            util.set(pdcMap, type, value)
        } else {
            super.put(key, NBTAdapter.adapt(value))
        }
    }

    fun getValue(key: String): Any? = util.search(key)?.let { util.get(pdcMap, type) } ?: super.get(key)

    override fun put(key: String, value: NBTData): NBTData = value.also { putValue(key, it.content) }

    override fun get(key: String): NBTData? = getValue(key)?.let { NBTAdapter.adapt(it) }

    override fun clone() = this.apply { pdcMap = util.clone(pdcMap) }

    companion object {

        /**
         * 工具 - [NMSDataComponent]
         */
        private val util get() = NMSDataComponent.INSTANCE

        /**
         * [net.minecraft.core.component.PatchedDataComponentMap]
         */
        val clazz by lazy { nmsClass("PatchedDataComponentMap") }

    }

}