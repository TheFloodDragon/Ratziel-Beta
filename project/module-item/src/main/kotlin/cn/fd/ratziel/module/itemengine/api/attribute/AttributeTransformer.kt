package cn.fd.ratziel.module.itemengine.api.attribute

/**
 * AttributeTransformer - 属性转化器
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:27
 */
interface AttributeTransformer<T, K> {

    /**
     * 正向转化 - 输出型转化
     */
    fun transform(target: T): K

    /**
     * 反向转化 - 应用型转化
     */
    fun detransform(target: T, from: K)

}