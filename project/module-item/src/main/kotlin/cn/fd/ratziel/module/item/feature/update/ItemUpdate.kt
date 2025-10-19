package cn.fd.ratziel.module.item.feature.update

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.module.item.ItemManager

/**
 * ItemUpdate
 *
 * @author TheFloodDragon
 * @since 2025/10/19 11:16
 */
class ItemUpdate(
    /**
     * 物品标识符
     */
    val identifier: Identifier
) {

    /**
     * [UpdateInterpreter] 存储的物品更新的相关配置
     */
    private val configuration = ItemManager.generatorInterpreter<UpdateInterpreter>(identifier)

    /**
     * 更新时受保护的节点
     */
    val protectedPaths get() = configuration.protectedPaths

}