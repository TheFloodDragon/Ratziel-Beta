package cn.fd.ratziel.item.nbt.api

/**
 * NotCorrectClass
 *
 * @author TheFloodDragon
 * @since 2023/10/21 22:01
 */
class NotCorrectClass(
    unresolved: Any,
    target: Class<*>,
) : RuntimeException(
    "Unsupported type of class \"${unresolved::class.qualifiedName}\" , it need \"${target.name}\" !"
)