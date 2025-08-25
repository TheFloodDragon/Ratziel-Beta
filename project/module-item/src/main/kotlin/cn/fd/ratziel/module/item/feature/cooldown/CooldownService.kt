@file:OptIn(ExperimentalUuidApi::class)

package cn.fd.ratziel.module.item.feature.cooldown

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.api.service.ItemServiceRegistry
import taboolib.common.platform.Awake
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.ExperimentalUuidApi

/**
 * CooldownService
 *
 * @author TheFloodDragon
 * @since 2025/5/31 13:19
 */
object CooldownService {

    val serviceMap: MutableMap<Identifier, Cooldown> = ConcurrentHashMap()

    @Awake
    private fun registerService() {
        ItemServiceRegistry.register(
            Cooldown::class.java,
            { id ->
                serviceMap.computeIfAbsent(id) { Cooldown(it) }
            }, { id, cooldown ->
                serviceMap[id] = cooldown
            }
        )
    }

}