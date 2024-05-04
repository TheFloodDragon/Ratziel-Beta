package cn.fd.ratziel.module.item.impl.service

import cn.fd.ratziel.module.item.api.service.ItemServiceFunction
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.concurrent.ConcurrentHashMap

/**
 * NativeServiceRegistry
 *
 * @author TheFloodDragon
 * @since 2024/5/4 12:27
 */
object NativeServiceRegistry : BaseServiceRegistry() {

    override val registry = ConcurrentHashMap<Class<*>, ItemServiceFunction<*>>()

    @Awake(LifeCycle.LOAD)
    private fun init() {
        // TODO 操你妈
    }

}