package cn.fd.ratziel.item.api

/**
 * ItemDisplay - 物品的显示部分
 *
 * NMS:
 *   1.13+ > Json Format
 *   1.13- > Original Format (§)
 *
 * @author TheFloodDragon
 * @since 2023/10/14 16:08
 */
interface ItemDisplay {

    /**
     * 物品名称
     */
    var name: String?

    /**
     * 物品描述
     */
    var lore: List<String>

}