package cn.fd.ratziel.compat.hook

/**
 * LinkedPluginHook
 *
 * @author TheFloodDragon
 * @since 2024/2/17 11:31
 */
interface LinkedPluginHook : PluginHook {

    @Deprecated("SB东西")
    val hookMap: HookMap

    val hookedClasses: Array<Class<*>>

}