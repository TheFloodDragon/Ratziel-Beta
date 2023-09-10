package cn.fd.ratziel.bukkit.module.trait.api

/**
 * InteractType
 *
 * @author TheFloodDragon
 * @since 2023/9/10 11:09
 */
enum class InteractType : Trait {

    /**
     * 包括以下所有
     */
    ANY {
        override val alias: Array<String> = arrayOf("all", "whatever")
    },

    /**
     * 左击
     */
    LEFT_CLICK {
        override val alias: Array<String> = arrayOf("left", "lc")
    },

    LEFT_CLICK_AIR {
        override val alias: Array<String> = arrayOf("left_air", "lca")
    },

    LEFT_CLICK_BLOCK {
        override val alias: Array<String> = arrayOf("left_block", "lcb")
    },

    /**
     * 右击
     */
    RIGHT_CLICK {
        override val alias: Array<String> = arrayOf("right", "rc")
    },

    RIGHT_CLICK_AIR {
        override val alias: Array<String> = arrayOf("right_air", "rca")
    },

    RIGHT_CLICK_BLOCK {
        override val alias: Array<String> = arrayOf("right_block", "rcb")
    },

    /**
     * 踩上方块
     */
    PHYSICAL {
        override val alias: Array<String> = arrayOf("on", "onBlock")
    };

    /**
     * 默认标识符
     */
    override val id: String = this.name.lowercase()

    /**
     * 默认别名
     */
    override val alias: Array<String> = emptyArray()

}