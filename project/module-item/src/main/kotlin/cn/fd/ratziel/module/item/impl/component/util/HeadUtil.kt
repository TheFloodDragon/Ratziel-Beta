package cn.fd.ratziel.module.item.impl.component.util

import cn.fd.ratziel.module.item.nbt.NBTCompound
import cn.fd.ratziel.module.item.nms.RefItemMeta
import org.bukkit.inventory.meta.SkullMeta
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.XSkull
import java.util.concurrent.ConcurrentHashMap

/**
 * HeadUtil
 *
 * @author TheFloodDragon
 * @since 2024/6/29 17:05
 */
/**
 * HeadUtil
 *
 * @author TheFloodDragon
 * @since 2024/6/29 16:26
 */
object HeadUtil {

    /**
     * 头颅缓存
     */
    private val CACHE: MutableMap<String, NBTCompound> = ConcurrentHashMap()

    /**
     * 默认头颅物品
     */
    private val DEFAULT_HEAD = XMaterial.PLAYER_HEAD.parseItem()!!

    /**
     * 获取对应 [headMeta] 头颅的 [NBTCompound]
     */
    fun getHeadTag(headMeta: String): NBTCompound {
        return CACHE.computeIfAbsent(headMeta) { generateSkullTag(headMeta)!! }
    }

    /**
     * 通过 [headMeta] 生成 [NBTCompound]
     */
    fun generateSkullTag(headMeta: String): NBTCompound? {
        return generateSkullTag(generateSkullMeta(headMeta) ?: return null)
    }

    /**
     * 通过 [SkullMeta] 生成 [NBTCompound]
     */
    fun generateSkullTag(meta: SkullMeta): NBTCompound {
        return RefItemMeta(meta).applyToTag(NBTCompound())
    }

    /**
     * 通过 [headMeta] 生成 [SkullMeta]
     */
    fun generateSkullMeta(headMeta: String): SkullMeta? {
        return DEFAULT_HEAD.clone().itemMeta?.let { XSkull.applySkin(it, headMeta) }
    }

}