package cn.fd.ratziel.module.compat.hook

import cn.fd.ratziel.module.compat.util.ClassProvider

/**
 * ManagedPluginHook
 *
 * @author TheFloodDragon
 * @since 2024/2/17 11:31
 */
interface ManagedPluginHook : PluginHook {

    /**
     * 受托管的类集合
     * 受托管的类将由 CompatibleClassLoader 加载并运行
     */
    val managedClasses: Array<String>

    /**
     * 绑定的 [ClassProvider]
     */
    val bindProvider: ClassProvider?

}