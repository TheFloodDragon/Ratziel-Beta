package cn.fd.ratziel.module.item.impl.builder

import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.function.debug
import taboolib.library.reflex.ReflexClass

/**
 * SectionTagRegister
 *
 * @author TheFloodDragon
 * @since 2024/10/1 15:51
 */
@Awake
class SectionTagRegister : ClassVisitor(10) {

    override fun visitStart(clazz: ReflexClass) {
        if (clazz.hasInterface(SectionTagResolver::class.java)) {
            val resolver = findInstance(clazz) as SectionTagResolver
            DefaultSectionResolver.resolvers.add(resolver)
            debug("注册表 DefaultSectionResolver#resolvers 注册了新的解析器: $resolver")
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}
