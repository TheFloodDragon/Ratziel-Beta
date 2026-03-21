package cn.fd.ratziel.module.item.api.component.transformer

/**
 * MinecraftTransformer (only for 1.20.5+)
 * 
 * @author TheFloodDragon
 * @since 2026/3/21 22:11
 */
interface MinecraftTransformer<T> {

    /**
     * 读取组件数据
     * @return 组件数据不存在时返回 null
     */
    fun read(nmsItem: Any): T?

    /**
     * 写入组件数据
     */
    fun write(nmsItem: Any, component: T)

    /**
     * 删除组件数据
     */
    fun remove(nmsItem: Any)

}