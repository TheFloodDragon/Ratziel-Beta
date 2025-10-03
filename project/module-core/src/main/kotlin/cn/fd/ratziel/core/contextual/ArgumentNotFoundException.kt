package cn.fd.ratziel.core.contextual

/**
 * ArgumentNotFoundException
 *
 * @author TheFloodDragon
 * @since 2024/8/16 18:28
 */
class ArgumentNotFoundException(val missingType: Class<*>) : Exception("Cannot found the argument: " + missingType.getName() + " !")
