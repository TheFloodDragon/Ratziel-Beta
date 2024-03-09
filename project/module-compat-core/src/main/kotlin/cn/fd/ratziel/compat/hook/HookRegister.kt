package cn.fd.ratziel.compat.hook

import cn.fd.ratziel.core.function.isAssignableTo
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.io.getInstance
import taboolib.common.platform.Awake
import java.util.function.Supplier

/**
 * HookRegister
 *
 * @author TheFloodDragon
 * @since 2024/3/9 14:25
 */
@Awake
class HookRegister : ClassVisitor(10) {

    override fun visitStart(clazz: Class<*>, instance: Supplier<*>?) {
        if (clazz.isAssignableTo(PluginHook::class.java) && clazz.isAnnotationPresent(HookInject::class.java))
            HookManager.register(instance?.get() as? PluginHook ?: clazz.asSubclass(PluginHook::class.java).getInstance(true)!!.get())
    }

    override fun getLifeCycle() = LifeCycle.INIT

}