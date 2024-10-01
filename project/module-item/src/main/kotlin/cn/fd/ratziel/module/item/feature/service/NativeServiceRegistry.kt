package cn.fd.ratziel.module.item.feature.service

import cn.fd.ratziel.module.item.api.service.ItemServiceFunction
import java.util.concurrent.ConcurrentHashMap

/**
 * NativeServiceRegistry
 *
 * @author TheFloodDragon
 * @since 2024/5/4 12:27
 */
object NativeServiceRegistry : BaseServiceRegistry() {

    override val registry = ConcurrentHashMap<Class<*>, ItemServiceFunction<*>>()

}