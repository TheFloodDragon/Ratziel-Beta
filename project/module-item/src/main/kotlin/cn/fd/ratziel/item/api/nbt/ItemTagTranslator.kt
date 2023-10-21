package cn.fd.ratziel.item.api.nbt

import taboolib.module.nms.ItemTagData

/**
 * ItemTagTranslator
 * 用于表示可被转化成ItemTag的类
 *
 * @author TheFloodDragon
 * @since 2023/10/20 18:44
 */
interface ItemTagTranslator {

    /**
     * 转化成ItemTag
     */
    fun toItemTag(): ItemTagData

}