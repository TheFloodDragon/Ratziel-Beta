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

    val sharingClasses = mutableListOf<String>()

    override fun visitStart(clazz: ReflexClass) {
        val share = clazz.getAnnotationIfPresent(NMSShare::class.java) ?: return
        // 版本校验
        val version = share.property("version", 0)
        if (MinecraftVersion.versionId < version) return
        // 生成代理类
        val baseClassName = clazz.name!!
        load(baseClassName) // 加载注解修饰的类
        // 同时生成所有的内部类
        val innerClasses = runningClassMapWithoutLibrary.filter { (name, _) -> name.startsWith("$baseClassName$") }
        innerClasses.forEach { (className, clazz) ->
            loadWithParents(clazz, innerClasses)
        }
    }

    fun loadWithParents(clazz: ReflexClass, availableClasses: Map<String, ReflexClass>) {
        if (clazz.name !in availableClasses) return // 防止加载其他的非内部类
        // 优先加载该类的父类和接口 (前提是这些类在innerClasses中)
        clazz.superclass?.let { loadWithParents(it, availableClasses) }
        clazz.interfaces.forEach { loadWithParents(it, availableClasses) }
        load(clazz.name!!) // 加载该类
    }

    /**
     * 将指定类加载到 AsmClassLoader 中
     */
    fun load(className: String) {
        if (className !in sharingClasses) {
            sharingClasses.add(className)
            AsmClassTranslation(className).createNewClass()
        }
    }

    override fun getLifeCycle() = LifeCycle.LOAD

}