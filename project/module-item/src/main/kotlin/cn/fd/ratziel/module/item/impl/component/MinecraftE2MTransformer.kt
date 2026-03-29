package cn.fd.ratziel.module.item.impl.component

/**
 * MinecraftE2MTransformer
 *
 * 仅支持 1.20.5+
 *
 * @author TheFloodDragon
 * @since 2026/3/21 23:18
 */
interface MinecraftE2MTransformer<T> {

    /**
     * 将 封装的组件数据 转换为 Minecraft 组件对象
     */
    fun toMinecraftObj(encapsulated: T): Any

    /**
     * 将 Minecraft 组件对象 转换为 封装的组件数据
     */
    fun fromMinecraftObj(minecraftObj: Any): T

}