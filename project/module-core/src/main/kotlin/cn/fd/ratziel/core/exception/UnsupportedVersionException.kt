package cn.fd.ratziel.core.exception

/**
 * UnsupportedVersionException
 * 
 * @author TheFloodDragon
 * @since 2026/3/22 00:08
 */
class UnsupportedVersionException(message: String) : Exception(message) {
    constructor() : this("Unsupported Minecraft version!")
}