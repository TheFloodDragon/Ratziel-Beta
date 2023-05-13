package cn.fd.fdutilities

import cn.fd.fdutilities.channel.BungeeChannel
import cn.fd.fdutilities.config.SettingsYaml
import cn.fd.fdutilities.module.outdated.Extension
import cn.fd.fdutilities.util.Adventure
import cn.fd.fdutilities.util.FileListener
import cn.fd.fdutilities.util.Loader
import org.bukkit.Bukkit
import taboolib.common.env.RuntimeEnv
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.pluginVersion
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang
import taboolib.platform.BukkitPlugin

@PlatformSide([Platform.BUKKIT])
object FDUtilities : Plugin() {

    val plugin by lazy { BukkitPlugin.getInstance() }

    /**
     * 加载依赖项
     */
    @Awake
    fun loadDependency() {
        RuntimeEnv.ENV.loadDependency(BukkitEnv::class.java, true)
    }

    override fun onLoad() {
        BungeeChannel.init()
        //插件语言设置
        Language.default = SettingsYaml.PLUGIN_LANGUAGE
        //加载模块配置文件
        Loader.reloadAll()
        console().sendLang("Plugin-Loading", Bukkit.getBukkitVersion())
    }

    override fun onEnable() {
        //开启并创建 BukkitAudiences
        Adventure.enable()

        Extension.printInfo()
        console().sendLang("Plugin-Enabled", pluginVersion)
    }

    override fun onDisable() {
        //关闭 BukkitAudiences
        Adventure.disable()
        //关闭配置文件监听器
        FileListener.uninstall()
    }

}