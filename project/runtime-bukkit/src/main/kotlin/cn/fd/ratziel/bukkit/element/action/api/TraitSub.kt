package cn.fd.ratziel.bukkit.element.action.api

/**
 * TraitSub
 *
 * @author TheFloodDragon
 * @since 2023/8/25 14:19
 */
class TraitSub(
    /**
     * 子特征名
     */
    val name: List<String>
) {
    constructor(name: String) : this(listOf(name))
}