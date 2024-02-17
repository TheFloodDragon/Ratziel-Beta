package cn.fd.ratziel.compat.hook

/**
 * PluginHook
 *
 * @author TheFloodDragon
 * @since 2024/2/17 11:30
 */
interface PluginHook {

    /**
     * 挂钩的插件名
     */
    val pluginName: String

    /**
     * 判断插件是否已挂钩
     */
    fun isHooked(): Boolean

}