package cn.fd.ratziel.module.itemengine.api.attribute

/**
 * AttributeTransformer - 属性转换器
 *
 * @author TheFloodDragon
 * @since 2023/12/8 21:27
 */
@Suppress("SpellCheckingInspection")
interface AttributeTransformer<T> {

    /**
     * 正向转换 - 输出型转换
     */
    fun transform(): T

    /**
     * 反向转换 - 应用型转换
     */
    fun detransform(input: T)

}