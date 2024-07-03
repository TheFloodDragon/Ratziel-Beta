package cn.fd.ratziel.module.item.api.builder

import cn.fd.ratziel.function.argument.ArgumentContext
import cn.fd.ratziel.function.argument.DefaultArgumentContext
import cn.fd.ratziel.module.item.api.NeoItem
import java.util.concurrent.CompletableFuture

/**
 * ItemGenerator - 物品构生成器
 *
 * @author TheFloodDragon
 * @since 2023/10/28 12:20
 */
interface ItemGenerator {

    /**
     * 构建物品
     * @return [CompletableFuture] - [NeoItem]
     */
    fun build(): CompletableFuture<out NeoItem> = build(DefaultArgumentContext())

    /**
     * 构建物品 (带参数)
     * @return [CompletableFuture] - [NeoItem]
     */
    fun build(context: ArgumentContext): CompletableFuture<out NeoItem>

}