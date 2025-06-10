package cn.fd.ratziel.module.item.internal

import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.impl.builder.NativeSource
import taboolib.common.platform.event.SubscribeEvent
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

/**
 * IdentifiedCache
 *
 * @author TheFloodDragon
 * @since 2025/6/7 20:54
 */
class IdentifiedCache<T>(
    val map: MutableMap<Identifier, T> = ConcurrentHashMap(),
    val onUpdate: Consumer<T> = Consumer {},
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
                val identifier = NativeSource.identifier(event.element)
                val value = cache.map[identifier] ?: continue
                // 触发更新回调
                @Suppress("UNCHECKED_CAST")
                (cache as IdentifiedCache<Any?>).onUpdate.accept(value)
                // 删除要被更新的元素
                cache.map.remove(identifier)
            }
        }

        @SubscribeEvent
        private fun onStart(event: ElementEvaluateEvent.Start) {
            if (event.handler !is ItemElement) return
            for (cache in caches) {
                // 清空缓存
                cache.map.clear()
            }
        }

    }

}