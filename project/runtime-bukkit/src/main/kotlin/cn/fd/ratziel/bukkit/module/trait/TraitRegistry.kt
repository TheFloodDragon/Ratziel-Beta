package cn.fd.ratziel.bukkit.module.trait

import cn.fd.ratziel.bukkit.module.trait.api.TraitWS
import cn.fd.ratziel.core.memory.HashMapMemory

/**
 * TraitRegistry
 *
 * @author TheFloodDragon
 * @since 2023/9/10 9:53
 */
object TraitRegistry : HashMapMemory<String, TraitWS>() {

    /**
     * 特征注册表
     *   特征标识符-特征
     */
    fun getTraitRegistry() = memory

    /**
     * 注册特征
     */
    fun registerTrait(trait: TraitWS) {
        addToMemory(trait.id, trait)
    }

    /**
     * 取消注册特征
     */
    fun unregisterTrait(name: String) {
        removeFromMemory(name)
    }

    /**
     * 获取特征
     */
    fun getTrait(name: String) = memory[name]

}