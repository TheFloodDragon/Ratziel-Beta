package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.functional.ArgumentContext
import cn.fd.ratziel.core.functional.ArgumentContextProvider
import cn.fd.ratziel.module.item.api.NeoItem
import java.util.concurrent.CompletableFuture

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
    val compositor: InterpreterCompositor

    /**
     * 生成器上下文提供者
     */
    val contextProvider: ArgumentContextProvider? get() = null

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