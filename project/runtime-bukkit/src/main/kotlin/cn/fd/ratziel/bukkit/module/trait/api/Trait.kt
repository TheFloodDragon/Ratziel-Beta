package cn.fd.ratziel.bukkit.module.trait.api

import cn.fd.ratziel.bukkit.module.trait.TraitSub

/**
 * Trait - 特征
 *
 * @author TheFloodDragon
 * @since 2023/8/25 14:17
 */
class Trait(
    /**
     * 特征名
     */
    val name: List<String>,
    /**
     * 子特征
     */
    val sub: List<TraitSub> = emptyList()
) {
    constructor(name: String, sub: List<TraitSub> = emptyList()) : this(listOf(name), sub)
}