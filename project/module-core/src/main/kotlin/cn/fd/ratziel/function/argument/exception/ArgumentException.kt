package cn.fd.ratziel.function.argument.exception

/**
 * ArgumentException - 元素相关的异常
 *
 * @author TheFloodDragon
 * @since 2024/5/3 15:07
 */
abstract class ArgumentException : Exception {

    constructor() : super()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

    protected constructor(message: String, cause: Throwable, enableSuppression: Boolean, writableStackTrace: Boolean)
            : super(message, cause, enableSuppression, writableStackTrace)

}