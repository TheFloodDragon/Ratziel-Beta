package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * ItemGenerator - 物品生成器
 *
 * @author TheFloodDragon
 * @since 2023/10/28 12:20
 */
interface ItemGenerator {

    /**
     * 解释器编排器
     */
    val compositor: ItemCompositor

    /**
     * 生成器上下文提供者
     */
    val contextProvider: Supplier<out ArgumentContext>? get() = null

    /**
     * 构建物品
     * @return [CompletableFuture] - [NeoItem]
     */
    fun build(): CompletableFuture<out NeoItem>

    /**
     * 构建物品 (带参数)
     * @return [CompletableFuture] - [NeoItem]
     */
    fun build(context: ArgumentContext): CompletableFuture<out NeoItem>

}