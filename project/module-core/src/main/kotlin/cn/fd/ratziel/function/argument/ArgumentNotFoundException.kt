package cn.fd.ratziel.function.argument

/**
 * ArgumentNotFoundException
 *
 * @author TheFloodDragon
 * @since 2024/5/1 14:12
 */
class ArgumentNotFoundException : Exception {

    constructor(type: Class<*>, throwable: Throwable) : super("Cannot found the argument: ${type.name} !", throwable)

    constructor(type: Class<*>) : super("Cannot found the argument: ${type.name} !")

    constructor() : super("Cannot found the argument!")

}