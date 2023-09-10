package cn.fd.ratziel.bukkit.module.trait.api

/**
 * TraitWS
 * 带有子特征
 *
 * @author TheFloodDragon
 * @since 2023/9/10 11:04
 */
interface TraitWS : Trait {
    /**
     * 子特征
     */
    val sub: Trait?
}