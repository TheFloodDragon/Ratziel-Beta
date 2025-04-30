package cn.fd.ratziel.module.item.impl.builder

import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
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
        if (clazz.hasAnnotation(Awake::class.java) && SectionTagResolver::class.java.isAssignableFrom(clazz.toClass())) {
            val resolver = findInstance(clazz) as? SectionTagResolver ?: return
            DefaultSectionResolver.resolvers.add(resolver)
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}
