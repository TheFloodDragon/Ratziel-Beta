package cn.fd.ratziel.module.item.internal.builder

import cn.fd.ratziel.module.item.api.builder.SectionTagResolver
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
        if (clazz.hasInterface(SectionTagResolver::class.java)) {
            DefaultSectionResolver.resolvers.add(findInstance(clazz) as SectionTagResolver)
        }
    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}
