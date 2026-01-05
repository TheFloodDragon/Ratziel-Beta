package cn.fd.ratziel.platform.bukkit.nms.share

import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.io.runningClassMapWithoutLibrary
import taboolib.common.platform.Awake
import taboolib.library.reflex.ReflexClass
import taboolib.module.nms.AsmClassTranslation
import taboolib.module.nms.MinecraftVersion

/**
 * NMSShareVisitor
 * 
 * @author TheFloodDragon
 * @since 2025/12/30 22:37
 */
@Awake
class NMSShareVisitor : ClassVisitor(10) {

    override fun visitStart(clazz: ReflexClass) {
        val share = clazz.getAnnotationIfPresent(NMSShare::class.java) ?: return
        // 版本校验
        val version = share.property("version", 0)
        if (MinecraftVersion.versionId < version) return
        // 生成代理类
        val bindClass = clazz.name!! // 勿用 ReflexClass#toClass, 会导致类被另一个类加载器先加载
        AsmClassTranslation(bindClass).createNewClass()
        // 同时生成所有的内部类
        runningClassMapWithoutLibrary.filter { (name, _) -> name.startsWith("$bindClass$") }.forEach { (source, _) ->
            AsmClassTranslation(source).createNewClass()
        }
    }

    override fun getLifeCycle() = LifeCycle.LOAD

}