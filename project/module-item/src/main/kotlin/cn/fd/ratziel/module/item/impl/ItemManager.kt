package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.element.Element
import java.util.concurrent.ConcurrentHashMap

/**
 * ItemManager
 *
 * @author TheFloodDragon
 * @since 2024/2/1 10:41
 */
object ItemManager {

    /**
     * 物品注册表
     */
    val registry: MutableMap<String, Element> = ConcurrentHashMap()

}