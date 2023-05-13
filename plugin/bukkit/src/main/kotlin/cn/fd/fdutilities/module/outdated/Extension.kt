package cn.fd.fdutilities.module.outdated

import org.bukkit.Bukkit
import taboolib.common.platform.function.console
import taboolib.module.lang.sendLang

@Deprecated("过时")
abstract class Extension(private val pluginName: String) : Module() {

    val pluginEnabled by lazy { Bukkit.getPluginManager().getPlugin(pluginName) != null }

    override fun reload() {
        registry.takeUnless { it.contains(this) }?.add(this)
        super.reload()
    }

    companion object {

        //注册库: 保存着所有扩展模块
        private var registry: ArrayList<Extension> = ArrayList()

        /**
         * 输出挂钩信息
         */
        fun printInfo() {
            registry.filter { it.isEnabled }.forEach {
                console().sendLang("Plugin-Dependency-Hooked", it.pluginName)
            }
        }

    }

}