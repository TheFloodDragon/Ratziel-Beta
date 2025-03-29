package cn.fd.ratziel.module.item.api

/**
 * ItemNode - 物品节点
 *
 * @author TheFloodDragon
 * @since 2024/3/16 10:43
 */
@Deprecated("This class is deprecated", ReplaceWith("NbtPath.Node"))
interface ItemNode {

    /**
     * 节点名称
     */
    val name: String

    /**
     * 父节点
     */
    val parent: ItemNode

    companion object {

        /**
         * 根节点
         */
        @JvmField
        val ROOT = object : ItemNode {
            override val name = "\$R\$O\$O\$T\$"
            override val parent: ItemNode = this
        }

    }

}