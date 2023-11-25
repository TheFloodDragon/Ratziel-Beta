package cn.fd.ratziel.core.function

/**
 * MirrorClass - 镜像类
 *
 * [T] - 镜前类
 *
 * @author TheFloodDragon
 * @since 2023/11/24 22:01
 */
abstract class MirrorClass<T> {

    /**
     * 镜后 - 表面该类是什么类的镜像类
     */
    abstract val clazz: Class<*>

    /**
     * 镜子 - 将镜后类转化成镜前类
     */
    abstract fun of(obj: Any): T

}