package cn.fd.ratziel.function.argument

/**
 * ArgumentSupplyException
 *
 * @author TheFloodDragon
 * @since 2024/5/2 21:21
 */
class ArgumentSupplyException : Exception {

    constructor(
        supplier: ArgumentSupplier,
        type: Class<*>,
        throwable: Throwable
    ) : super("Supplier $supplier couldn't supply this type of argument: ${type.name}!", throwable)

    constructor(supplier: ArgumentSupplier, type: Class<*>) : super("Supplier $supplier couldn't supply this type of argument: ${type.name}!")

}