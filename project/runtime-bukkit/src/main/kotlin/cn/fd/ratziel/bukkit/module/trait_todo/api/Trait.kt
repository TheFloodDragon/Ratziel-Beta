package cn.fd.ratziel.bukkit.module.trait_todo.api

/**
 * Trait - 特征
 *
 * @author TheFloodDragon
 * @since 2023/8/25 14:17
 */
interface Trait {

    /**
     * 特征标识符
     */
    val id: String

    /**
     * 特征别名
     */
    val alias: Array<String>

}