package cn.fd.ratziel.module.item.exception

/**
 * ComponentNotFoundException
 *
 * @author TheFloodDragon
 * @since 2025/5/3 18:00
 */
class ComponentNotFoundException(type: Class<*>) :
    RuntimeException("Cannot find the component of type '$type'. It might not be a valid component type!")
