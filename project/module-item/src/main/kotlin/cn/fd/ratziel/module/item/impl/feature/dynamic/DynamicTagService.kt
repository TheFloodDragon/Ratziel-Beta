package cn.fd.ratziel.module.item.impl.feature.dynamic

import cn.fd.ratziel.common.event.ElementEvaluateEvent
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.builder.ItemTagResolver
import cn.fd.ratziel.module.item.impl.builder.DefaultResolver
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.warning
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * DynamicTagService
 *
 * @author TheFloodDragon
 * @since 2025/5/31 17:03
 */
object DynamicTagService {

    /**
     * 动态解析任务表
     */
    val resolvations: MutableMap<String, DynamicTagResolvation> = ConcurrentHashMap()

    /**
     * 提交一个动态标签解析任务
     */
    fun submit(resolvation: DynamicTagResolvation) {
        resolvations[resolvation.identifiedContent] = resolvation
    }

    /**
     * 支持动态解析的 [ItemTagResolver] 表
     */
    val supportedResolvers: MutableList<ItemTagResolver> by lazy {
        CopyOnWriteArrayList(DefaultResolver.defaultTagResolvers)
    }

    /**
     * 寻找动态标签解析器
     */
    fun findResolver(name: String): ItemTagResolver? {
        val resolver = supportedResolvers.firstOrNull { it.alias.contains(name) }
        if (resolver == null) warning("ItemTagResolver for DynamicTagService '$name' not found.")
        return resolver
    }

    /**
     * 注册一个动态标签解析器
     */
    fun registerResolver(resolver: ItemTagResolver) {
        supportedResolvers.add(resolver)
    }

    @SubscribeEvent
    private fun onStart(event: ElementEvaluateEvent.Start) {
        if (event.handler !is ItemElement) return
        // 清空动态解析任务表
        resolvations.clear()
    }

}