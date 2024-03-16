package cn.fd.ratziel.module.item.api

/**
 * Transformer - 转换器
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:44
 */
interface Transformer<T,K> {

    /**
     * 正向转化 - 输出型转换
     */
    fun transform(input: T): K

    /**
     * 反向转化 - 应用型转换
     */
    fun detransform(input: T, from: K)

}