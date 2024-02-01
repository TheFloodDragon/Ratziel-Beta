package cn.fd.ratziel.module.itemengine.item

import cn.fd.ratziel.module.itemengine.api.builder.ItemGenerator
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemManager
 *
 * @author TheFloodDragon
 * @since 2024/2/1 10:41
 */
object ItemManager {

    /**
     * 物品注册表
     * 物品标识符:物品生成器
     */
    val registry = ConcurrentHashMap<String, ItemGenerator>()

}