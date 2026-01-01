package cn.fd.ratziel.module.item.api.component.transformer

/**
 * MinecraftComponentTransformer
 * 
 * @author TheFloodDragon
 * @since 2026/1/1 22:26
 */
interface MinecraftComponentTransformer<T> {

    /**
     * 组件 -> Minecraft 对象
     */
    fun transformToMinecraftObj(tar: T): Any

    /**
     * Minecraft 对象 -> 组件
     */
    fun detransformFromMinecraftObj(src: Any): T

}