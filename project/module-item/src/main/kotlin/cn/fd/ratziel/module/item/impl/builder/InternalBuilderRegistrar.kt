package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.builder.ItemInterceptor
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ReflexClass

/**
 * InternalBuilderRegistrar
 *
 * @author TheFloodDragon
 * @since 2024/10/1 15:51
 */
@Awake
class InternalBuilderRegistrar : ClassVisitor(10) {

    override fun visitStart(clazz: ReflexClass) {
        // 需要带有 @AutoRegister 才会自动注册
        if (!clazz.hasAnnotation(AutoRegister::class.java)) return

        // 注册 SectionTagResolver
        if (SectionTagResolver::class.java.isAssignableFrom(clazz.toClass())) {
            val resolver = findInstance(clazz) as? SectionTagResolver ?: return
            for (name in resolver.names) {
                SectionResolver.tagResolvers[name] = resolver
            }
        }

        // 注册 ItemInterceptor
        if (clazz.hasInterface(ItemInterceptor::class.java)) {
            val interceptor = findInstance(clazz) as ItemInterceptor
            ItemRegistry.registerInterceptor(interceptor)
        }

    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}
