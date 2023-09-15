package cn.fd.ratziel.bukkit.module.trait

import cn.fd.ratziel.bukkit.module.trait.api.InteractType
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * TraitListener
 *
 * @author TheFloodDragon
 * @since 2023/9/10 10:56
 */
object TraitListener {

    @SubscribeEvent
    fun onInteract(e: PlayerInteractEvent) {
        pressController.apply {
            getTasks().forEach { task ->
                task.id.let {
                    println(it)
                    if (it.startsWith("interact_${InteractType.infer(e.action).id}", true))
                        complete(it, true)
                }
            }
        }
    }

}