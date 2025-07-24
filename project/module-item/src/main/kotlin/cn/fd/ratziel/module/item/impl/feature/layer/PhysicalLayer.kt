package cn.fd.ratziel.module.item.impl.feature.layer

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.put
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.nbt.handle
import cn.fd.ratziel.module.nbt.read

/**
 * PhysicalLayer - 物理图层
 *
 * @author TheFloodDragon
 * @since 2025/7/24 13:48
 */
class PhysicalLayer(
    /** 图层名称 **/
    override val name: String,
    /** 图层标签 **/
    val tag: NbtCompound,
) : ItemLayer {

    /** 图层数据 **/
    override val data: ItemData
        get() = SimpleData(tag = this.tag) // 物理图层不考虑数量和材料

    companion object {

        /** 物品图层节点 **/
        @JvmStatic
        val LAYER_PATH = RatzielItem.RATZIEL_PATH + NbtPath.NameNode("layer")

        /** 内部信息 - 当前图层 **/
        const val CURRENT_LAYER_NAME = "current_layer"

        /** 内部信息 - 原始图层数据 **/
        const val ORIGINAL_LAYER_NAME = "original_layer"

        /** 内部信息 - 存储的图层表 **/
        const val STORAGE_LAYERS_NAME = "storage_layers"

        /**
         * 获取图层
         */
        @JvmStatic
        fun getLayer(itemData: ItemData, name: String): ItemLayer? =
            (((itemData.tag.read(LAYER_PATH) as? NbtCompound)
                ?.get(STORAGE_LAYERS_NAME) as? NbtCompound)
                ?.get(name) as? NbtCompound)
                ?.let { PhysicalLayer(name, it) }

        /**
         * 从物品中读取图层表
         */
        @JvmStatic
        fun readLayers(itemData: ItemData): Map<String, ItemLayer> {
            val storageLayers = (itemData.tag.read(LAYER_PATH) as? NbtCompound)
                ?.get(STORAGE_LAYERS_NAME) as? NbtCompound ?: return emptyMap()

            return storageLayers.mapValues { PhysicalLayer(it.key, it.value as NbtCompound) }
        }

        /**
         * 将图层表写入物品
         */
        @JvmStatic
        fun writeLayers(itemData: ItemData, layers: Map<String, ItemLayer>) {
            itemData.tag.handle(LAYER_PATH) {
                val storageLayers = NbtCompound()
                for ((name, layer) in layers) {
                    storageLayers[name] = layer.data.tag
                }
                put(STORAGE_LAYERS_NAME, storageLayers)
            }
        }

    }

    /**
     * Renderer
     *
     * @author TheFloodDragon
     * @since 2025/7/24 13:52
     */
    object Renderer : ItemLayer.Renderer {

        /**
         * 渲染物品图层
         *
         * @param item 物品实例
         * @param layer 图层实例
         */
        override fun render(item: NeoItem, layer: ItemLayer) {
            val root = item.data.tag // 物品数据根
            root.handle(LAYER_PATH) {

                // 恢复之前存储的原始图层数据
                val originalLayer = get(ORIGINAL_LAYER_NAME) as? NbtCompound
                    ?: NbtCompound().also { put(ORIGINAL_LAYER_NAME, it) }
                for ((key, value) in originalLayer) {
                    if (value == REMOVE_MARK) {
                        root.remove(key)
                    } else {
                        root.put(key, value)
                    }
                }
                originalLayer.clear() // 清空原始图层数据

                if (layer !is ItemLayer.Default) {
                    // 更换图层
                    for ((key, value) in layer.data.tag) {
                        // 将物品数据扔到原始图层
                        originalLayer[key] = root[key] ?: REMOVE_MARK
                        // 写入新图层数据
                        root.put(key, value)
                    }
                }
                // 设置当前图层
                put(CURRENT_LAYER_NAME, layer.name)
            }
        }

        /**
         * 删除标记
         */
        @JvmStatic
        private val REMOVE_MARK = NbtCompound { put("_", true) }

    }

}