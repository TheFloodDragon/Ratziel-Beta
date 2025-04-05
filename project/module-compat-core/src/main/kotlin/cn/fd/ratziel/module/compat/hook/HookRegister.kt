package cn.fd.ratziel.module.compat.hook

import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ReflexClass

/**
 * HookRegister
 *
 * @author TheFloodDragon
 * @since 2024/3/9 14:25
 */
@Awake
class HookRegister : ClassVisitor(10) {

    override fun visitStart(clazz: ReflexClass) {
        if (PluginHook::class.java.isAssignableFrom(clazz.toClass())) {
            HookManager.register(findInstance(clazz) as PluginHook)
        }
    }

    override fun getLifeCycle() = LifeCycle.INIT

}