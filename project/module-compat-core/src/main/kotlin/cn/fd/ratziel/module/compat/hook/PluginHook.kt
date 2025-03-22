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
     * 判断插件是否可挂钩
     */
    fun isHookable(): Boolean

}