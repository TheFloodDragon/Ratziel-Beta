package cn.fd.ratziel.module.compat.hook

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
     * 插件是否挂钩
     */
    val isHooked: Boolean

}