package cn.fd.ratziel.compat.item.neige

import cn.fd.ratziel.module.item.api.service.ItemServiceFunction
import cn.fd.ratziel.module.item.impl.service.BaseServiceRegistry
import cn.fd.ratziel.module.item.impl.service.GlobalServiceRegistry
import org.bukkit.Bukkit
import pers.neige.neigeitems.item.ItemGenerator
import pers.neige.neigeitems.item.action.ItemAction
import pers.neige.neigeitems.manager.ActionManager
import pers.neige.neigeitems.manager.ItemManager
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.concurrent.ConcurrentHashMap

/**
 * NeigeItemsService
 *
 * @author TheFloodDragon
 * @since 2024/5/4 11:23
 */
object NeigeItemsService {

    fun registerDefault() {
        Registry.register(ItemAction::class.java, {
            ActionManager.itemActions[it.toString()]
        }, { id, value ->
            ActionManager.itemActions[id.toString()] = value
        })
        Registry.register(ItemGenerator::class.java, {
            ItemManager.items[it.toString()]
        }, { id, value ->
            ItemManager.items[id.toString()] = value
        })
    }

    @Awake(LifeCycle.LOAD)
    fun enable() {
        if (Bukkit.getPluginManager().getPlugin("NeigeItems") != null) {
            registerDefault()
            GlobalServiceRegistry.registries.add(Registry)
        }
    }

    object Registry : BaseServiceRegistry() {
        override val registry: MutableMap<Class<*>, ItemServiceFunction<*>> = ConcurrentHashMap()
    }

}