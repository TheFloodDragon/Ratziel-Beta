package cn.fd.ratziel.module.item.api

/**
 * DataHolder
 *
 * @author TheFloodDragon
 * @since 2025/4/3 22:55
 */
interface DataHolder {

    /**
     * 获取数据
     *
     * @param name 数据名称
     */
    operator fun get(name: String): Any?

    /**
     * 设置数据
     *
     * @param name 数据名称
     * @param data 具体数据
     */
    operator fun set(name: String, data: Any)

    /**
     * 转换成数据集
     */
    fun toDataMap(): Map<String, Any>

}