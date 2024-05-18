package cn.fd.ratziel.module.item

import cn.fd.ratziel.common.element.registry.NewElement
import cn.fd.ratziel.common.event.WorkspaceLoadEvent
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.element.api.ElementHandler
import cn.fd.ratziel.core.serialization.baseJson
import cn.fd.ratziel.module.item.impl.ItemManager
import cn.fd.ratziel.module.item.impl.builder.DefaultItemGenerator
import cn.fd.ratziel.module.item.reflex.RefItemStack
import kotlinx.serialization.json.Json
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent

/**
 * ItemElement
 *
 * @author TheFloodDragon
 * @since 2023/10/14 18:55
 */
@NewElement(
    name = "meta",
    space = "test"
)
object ItemElement : ElementHandler {

    val json by lazy {
        Json(baseJson) {
        }
    }

    override fun handle(element: Element) {

        val generator = DefaultItemGenerator(element)

        val item = generator.build().get()

        println(item.data)

        val test = RefItemStack(ItemStack(Material.BOW).apply { addUnsafeEnchantment(Enchantment.EFFICIENCY, 1) })
        println(test)
        println(test.getData())
        test.setData(item.data.tag)
        println(test)
        println(test.getData())

        // 注册
        ItemManager.registry[element.name] = generator
    }

    @SubscribeEvent
    fun onLoadStart(event: WorkspaceLoadEvent.Start) {
        // 清除注册的物品
        ItemManager.registry.clear()
    }

}