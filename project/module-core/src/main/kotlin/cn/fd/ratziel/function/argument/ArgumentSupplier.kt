package cn.fd.ratziel.function.argument

/**
 * ArgumentSupplier - 参数提供者
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:15
 */
interface ArgumentSupplier {

    /**
     * 获取指定类型的参数
     * @throws ArgumentSupplyException 无法提供时抛出
     */
    operator fun <T> get(type: Class<T>): T

}