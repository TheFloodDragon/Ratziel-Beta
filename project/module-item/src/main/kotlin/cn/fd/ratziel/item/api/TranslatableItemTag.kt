package cn.fd.ratziel.item.api

import taboolib.module.nms.ItemTagData

/**
 * TranslatableItemTag
 * 用于表示可被转化成ItemTag的类
 *
 * @author TheFloodDragon
 * @since 2023/10/20 18:44
 */
interface TranslatableItemTag {

    /**
     * 转化成ItemTag
     */
    fun toItemTag(): ItemTagData

}