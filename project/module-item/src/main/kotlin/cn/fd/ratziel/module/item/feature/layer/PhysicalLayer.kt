package cn.fd.ratziel.module.item.feature.layer

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.put
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.nbt.handle
import cn.fd.ratziel.module.nbt.read
import kotlin.collections.iterator

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
        const val CURRENT_LAYER_NAME = "current"

        /** 内部信息 - 存储的图层表 **/
        const val STORAGE_LAYERS_NAME = "layers"

        /** 默认图层名称 **/
        const val DEFAULT_LAYER_NAME = "0"

        /**
         * 获取图层
         */
        @JvmStatic
        fun getLayer(itemData: ItemData, name: String): ItemLayer? {
            val layerInfo = itemData.tag.read(LAYER_PATH, true) as NbtCompound
            return if (name == DEFAULT_LAYER_NAME) {
                findLayer(layerInfo, name)
            } else {
                ((layerInfo[STORAGE_LAYERS_NAME] as? NbtCompound)
                    ?.get(name) as? NbtCompound)
                    ?.let { PhysicalLayer(name, it) }
            }
        }

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

        /**
         * 寻找 [ItemLayer], 找不到就一路创建
         */
        @JvmStatic
        internal fun findLayer(tag: NbtCompound, name: String): PhysicalLayer {
            val storage = tag[STORAGE_LAYERS_NAME] as? NbtCompound
                ?: NbtCompound().also { tag[STORAGE_LAYERS_NAME] = it }
            val layerData = storage[name] as? NbtCompound
                ?: NbtCompound().also { storage[name] = it }
            return PhysicalLayer(name, layerData)
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
                // 获取当前图层
                val currentLayerName = get(CURRENT_LAYER_NAME)?.content as? String ?: DEFAULT_LAYER_NAME
                val currentLayer = findLayer(this@handle, currentLayerName)

                // 将当前图层的数据存到 存储的图层表 中
                val currentLayerData = currentLayer.data.tag
                if (currentLayerData.isEmpty() && currentLayerName == DEFAULT_LAYER_NAME) {
                    // 默认图层在 存储的图层表 中一开始没有, 故需要通过 要设置的图层 来初始化
                    for ((key, _) in layer.data.tag) {
                        currentLayerData[key] = root[key] ?: REMOVE_MARK
                    }
                } else {
                    for ((key, _) in currentLayerData) {
                        currentLayerData[key] = root[key] ?: REMOVE_MARK
                    }
                }

                // 更换图层
                for ((key, value) in layer.data.tag) {
                    // 写入新图层数据
                    if (value == REMOVE_MARK) root.remove(key) else root.put(key, value)
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