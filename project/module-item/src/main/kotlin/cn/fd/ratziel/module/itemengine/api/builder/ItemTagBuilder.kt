package cn.fd.ratziel.module.itemengine.api.builder

import cn.fd.ratziel.module.itemengine.nbt.NBTTag

/**
 * ItemTagBuilder - 物品标签构建器
 *
 * @author TheFloodDragon
 * @since 2023/11/11 15:44
 */
interface ItemTagBuilder {

    /**
     * 构建物品标签
     */
    fun build(tag: NBTTag)

    /**
     * 应用物品标签
     */
    fun apply(tag: NBTTag) {} // TODO

}