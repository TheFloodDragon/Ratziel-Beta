package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.common.element.registry.AutoRegister
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.TaggedSectionResolver
import cn.fd.ratziel.module.item.impl.feature.dynamic.DynamicTagService
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

        // 注册 ItemTagResolver
        if (clazz.hasInterface(ItemTagResolver::class.java)) {
            val resolver = findInstance(clazz) as? ItemTagResolver ?: return
            TaggedSectionResolver.registerTagResolver(resolver)
            DynamicTagService.registerResolver(resolver) // 默认支持动态解析
        }

    }

    override fun getLifeCycle(): LifeCycle {
        return LifeCycle.LOAD
    }

}