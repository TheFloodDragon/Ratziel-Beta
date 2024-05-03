package cn.fd.ratziel.function.argument

import cn.fd.ratziel.function.argument.exception.ArgumentSupplyException

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
    @Throws(ArgumentSupplyException::class)
    operator fun <T : Any> get(type: Class<T>): Argument<T>

}