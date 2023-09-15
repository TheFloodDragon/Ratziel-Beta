package cn.fd.ratziel.bukkit.module.trait_todo

import cn.fd.ratziel.bukkit.module.trait_todo.api.InteractType
import cn.fd.ratziel.bukkit.module.trait_todo.api.Trait
import cn.fd.ratziel.bukkit.module.trait_todo.api.TraitWS
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * DefaultTrait
 *
 * @author TheFloodDragon
 * @since 2023/8/25 14:10
 */
enum class DefaultTrait : TraitWS {

    /**
     * 交互特征
     */
    INTERACT {
        override val alias: Array<String> = arrayOf("click")
        override val sub: Trait = InteractType.ANY
    };

    /**
     * 默认标识符
     */
    override val id: String = this.name.lowercase()

    /**
     * 默认别名
     */
    override val alias: Array<String> = emptyArray()

    /**
     * 默认没有子特征
     */
    override val sub: Trait? = null

    companion object {
        /**
         * 注册所有默认特征
         */
        @Awake(LifeCycle.ENABLE)
        fun registerAll() {
            entries.forEach { TraitRegistry.registerTrait(it) }
        }
    }

}