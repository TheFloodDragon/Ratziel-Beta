package tb.module.configuration

import cn.fd.utilities.core.util.releaseResourceFile
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common5.FileWatcher
import taboolib.library.reflex.ClassField
import taboolib.module.configuration.ConfigNodeFile
import taboolib.module.configuration.ConfigNodeLoader
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.SecuredFile
import java.util.function.Supplier

@RuntimeDependencies(
    RuntimeDependency(
        "!org.yaml:snakeyaml:2.0",
        test = "!org.yaml.snakeyaml_2_0.Yaml",
        relocate = ["!org.yaml.snakeyaml", "!org.yaml.snakeyaml_2_0"]
    ),
    RuntimeDependency("!com.typesafe:config:1.4.2", test = "!com.typesafe.config.Config"),
    RuntimeDependency("!com.electronwill.night-config:core:3.6.6", test = "!com.electronwill.nightconfig.core.Config"),
    RuntimeDependency(
        "!com.electronwill.night-config:toml:3.6.6",
        test = "!com.electronwill.nightconfig.toml.TomlFormat"
    ),
    RuntimeDependency(
        "!com.electronwill.night-config:json:3.6.6",
        test = "!com.electronwill.nightconfig.json.JsonFormat"
    ),
    RuntimeDependency(
        "!com.electronwill.night-config:hocon:3.6.6",
        test = "!com.electronwill.nightconfig.hocon.HoconFormat"
    )
)
@Awake
class ConfigLoader : ClassVisitor(1) {

    @Suppress("DEPRECATION")
    override fun visit(field: ClassField, clazz: Class<*>, instance: Supplier<*>?) {
        if (field.isAnnotationPresent(Config::class.java)) {
            val configAnno = field.getAnnotation(Config::class.java)
            val name = configAnno.property("value", "config.yml")
            val target = configAnno.property("target", "config.yml") //修改部分——添加
            if (files.containsKey(name)) {
                field.set(instance?.get(), files[name]!!.configuration)
            } else {
                val file = releaseResourceFile(name, if (target == "sameAsValue") name else target) //修改部分
                // 兼容模式加载
                val conf =
                    if (field.fieldType == SecuredFile::class.java) SecuredFile.loadConfiguration(file) else Configuration.loadFromFile(
                        file
                    )
                // 赋值
                field.set(instance?.get(), conf)
                // 自动重载
                if (configAnno.property("autoReload", false) && isFileWatcherHook) {
                    FileWatcher.INSTANCE.addSimpleListener(file) {
                        if (file.exists()) {
                            conf.loadFromFile(file)
                        }
                    }
                }
                val configFile = ConfigNodeFile(conf, file)
                conf.onReload {
                    val loader = PlatformFactory.getAPI<ConfigNodeLoader>()
                    configFile.nodes.forEach { loader.visit(it, clazz, instance) }
                }
                files[name] = configFile
                // 开发模式
                if (TabooLibCommon.isDevelopmentMode()) {
                    TabooLibCommon.print("Loaded config file: ${file.absolutePath}")
                }
            }
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.INIT
    }

    companion object {

        val files = HashMap<String, ConfigNodeFile>()

        val isFileWatcherHook by lazy {
            try {
                FileWatcher.INSTANCE
                true
            } catch (ex: NoClassDefFoundError) {
                false
            }
        }
    }
}