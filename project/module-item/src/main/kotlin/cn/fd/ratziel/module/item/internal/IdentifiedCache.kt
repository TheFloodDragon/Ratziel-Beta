package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.module.item.ItemElement
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.BiConsumer

/**
 * IdentifiedCache
 *
 * @author TheFloodDragon
 * @since 2025/6/7 20:54
 */
class IdentifiedCache<T>(
    val map: MutableMap<Identifier, T> = ConcurrentHashMap(),
    val onUpdate: BiConsumer<Identifier, T> = BiConsumer { _, _ -> },
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
                val identifier = SimpleIdentifier(event.element.name)
                val value = cache.map[identifier] ?: continue
                // 删除要被更新的元素
                cache.map.remove(identifier)
                // 回调
                @Suppress("UNCHECKED_CAST")
                (cache as IdentifiedCache<Any?>).onUpdate.accept(identifier, value)
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