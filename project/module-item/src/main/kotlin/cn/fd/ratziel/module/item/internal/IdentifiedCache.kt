package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.module.item.ItemElement
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * IdentifiedCache
 *
 * @author TheFloodDragon
 * @since 2025/6/7 20:54
 */
class IdentifiedCache<T>(
    val map: MutableMap<Identifier, T> = ConcurrentHashMap(),
) {

    init {
        caches.add(this)
    }

    companion object {

        private val caches = CopyOnWriteArrayList<IdentifiedCache<*>>()

        @SubscribeEvent
        private fun onProcess(event: ElementEvaluateEvent.Process) {
            if (event.handler !is ItemElement) return
            for (cache in caches) {
                cache.map.remove(SimpleIdentifier(event.element.name))
            }
        }

        @SubscribeEvent
        private fun onStart(event: ElementEvaluateEvent.Start) {
            if (event.handler !is ItemElement) return
            for (cache in caches) {
                cache.map.clear()
            }
        }

    }

}