package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.function.argument.ArgumentFactory
import cn.fd.ratziel.function.argument.DefaultArgumentFactory
import cn.fd.ratziel.module.item.api.NeoItem
import java.util.concurrent.CompletableFuture

/**
 * ItemGenerator
 *
 * @author TheFloodDragon
 * @since 2023/10/28 12:20
 */
interface ItemGenerator {

    /**
     * 原始物品配置 (元素)
     */
    val origin: Element

    /**
     * 构建物品
     * @return [CompletableFuture] - [NeoItem]
     */
    fun build(): CompletableFuture<out NeoItem> = build(DefaultArgumentFactory())

    /**
     * 构建物品 (带参数)
     * @return [CompletableFuture] - [NeoItem]
     */
    fun build(arguments: ArgumentFactory): CompletableFuture<out NeoItem>

}