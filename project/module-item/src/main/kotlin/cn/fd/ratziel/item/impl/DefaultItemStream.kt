package cn.fd.ratziel.item.impl

import cn.fd.ratziel.bukkit.nbt.NBTTag
import cn.fd.ratziel.core.util.quickRunFuture
import cn.fd.ratziel.item.api.ItemStream
import cn.fd.ratziel.item.meta.VItemMeta
import cn.fd.ratziel.item.util.nms.ObcItemMeta
import org.bukkit.inventory.meta.ItemMeta
import taboolib.module.nms.NMSItemTag
import taboolib.module.nms.nmsProxy
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * DefaultItemStream
 *
 * @author TheFloodDragon
 * @since 2023/10/27 22:23
 */
class DefaultItemStream(nbtTag: NBTTag = NBTTag()) : ItemStream(nbtTag) {

    fun build(vm: VItemMeta): ItemMeta {
        // 处理显示部分 (包括耐久)
        val displayed = nbtTag.apply {
            val futures: ConcurrentLinkedDeque<CompletableFuture<*>> = ConcurrentLinkedDeque()
            futures += quickRunFuture { vm.display.applyTo(this) }
            futures += quickRunFuture { vm.durability.applyTo(this) }
            futures += quickRunFuture { vm.characteristic.applyTo(this) }
            CompletableFuture.allOf(*futures.toTypedArray())
        }
        // 获取NMSItemTag
        val nmsTag = nmsProxy<NMSItemTag>().itemTagToNMSCopy(displayed)
        println(nmsTag)
        // 创建ItemMeta对象
        var itemMeta = ObcItemMeta.createInstance(nmsTag) as ItemMeta

        // 返回ItemMeta
        return itemMeta
    }

}