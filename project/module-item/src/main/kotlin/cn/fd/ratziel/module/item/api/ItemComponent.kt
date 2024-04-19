package cn.fd.ratziel.module.item.api

/**
 * ItemComponent - 物品组件
 *
 * @author TheFloodDragon
 * @since 2024/3/15 18:53
 */
interface ItemComponent<T> {

    /**
     * 获取NBT数据转换器
     */
    fun transformer(): DataTransformer<T>

}