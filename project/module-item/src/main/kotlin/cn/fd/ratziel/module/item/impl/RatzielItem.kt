package cn.fd.ratziel.module.item.impl

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.IdentifierImpl
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.service.GlobalServiceManager
import cn.fd.ratziel.module.item.nms.ItemSheet
import cn.fd.ratziel.module.item.nms.RefItemStack
import cn.fd.ratziel.module.item.util.handle
import cn.fd.ratziel.module.item.util.read
import cn.fd.ratziel.module.nbt.NBTCompound
import cn.fd.ratziel.module.nbt.NBTString
import cn.fd.ratziel.module.nbt.readString
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.isAir

/**
 * RatzielItem
 *
 * @author TheFloodDragon
 * @since 2024/5/2 22:05
 */
open class RatzielItem private constructor(info: Info, data: ItemData) : NeoItem {

    /**
     * 物品信息
     */
    val info: Info = info

    /**
     * 物品数据
     */
    final override var data: ItemData = data
        protected set

    /**
     * 物品标识符
     */
    val id: Identifier get() = info.type

    /**
     * 物品服务
     */
    override val service get() = GlobalServiceManager[id]

    companion object {

        /**
         * 物品数据节点
         */
        @JvmField
        val RATZIEL_DATA_NODE = SimpleNode("Ratziel", ItemSheet.CUSTOM_DATA)

        /**
         * @return [RatzielItem]
         */
        @JvmStatic
        fun of(info: Info, data: ItemData): RatzielItem {
            Info.write(info, data)
            return RatzielItem(info, data)
        }

        /**
         * 将目标 [ItemStack] 转为 [RatzielItem]
         *
         * @return 若目标不满足 [isRatzielItem], 则返回空
         */
        @JvmStatic
        fun of(itemStack: ItemStack): RatzielItem? {
            if (itemStack.isAir()) return null
            val itemData = RefItemStack.of(itemStack)
            return of(itemData)
        }

        /**
         * 将目标 [ItemData] 转为 [RatzielItem]
         *
         * @return 若目标不满足 [isRatzielItem], 则返回空
         */
        @JvmStatic
        fun of(itemData: ItemData): RatzielItem? {
            val info = Info.read(itemData) ?: return null
            return RatzielItem(info, itemData)
        }

        /**
         * 判断目标 [ItemStack] 是否为 [RatzielItem]
         */
        @JvmStatic
        fun isRatzielItem(itemStack: ItemStack): Boolean {
            return isRatzielItem(RefItemStack.of(itemStack).customTag)
        }

        /**
         * 检查目标自定义标签数据, 判断是否为 [RatzielItem]
         */
        @JvmStatic
        fun isRatzielItem(customTag: NBTCompound?): Boolean {
            val proprietary = customTag?.get(RATZIEL_DATA_NODE.name) as? NBTCompound
            return proprietary?.containsKey(Info.INTERNAL_NODE.name) ?: false
        }

    }

    /**
     * [RatzielItem] 专有信息
     */
    data class Info(
        /**
         * 物品类型
         */
        val type: Identifier,
        /**
         * 物品版本信息 (生成此物品时的元素哈希)
         */
        val hash: String
    ) {

        constructor(type: String, hash: String) : this(IdentifierImpl(type), hash)

        constructor(type: Identifier, hash: Int) : this(type, hash.toString())

        companion object {

            /**
             * 专有信息节点
             */
            @JvmField
            val INTERNAL_NODE = SimpleNode("internal", RATZIEL_DATA_NODE)

            /**
             * 内部信息 - [id]
             */
            @JvmField
            val INFO_TYPE = SimpleNode("type", INTERNAL_NODE)

            /**
             * 内部信息 - [hash]
             */
            @JvmField
            val INFO_HASH = SimpleNode("hash", INTERNAL_NODE)

            /**
             * 从 [ItemData] 中读取信息
             */
            @JvmStatic
            fun read(data: ItemData): Info? {
                // 获取内部信息
                val internal = data.read<NBTCompound>(INTERNAL_NODE) ?: return null
                // 读取类型
                val type = internal.readString(INFO_TYPE.name) ?: return null
                // 读取版本信息
                val hash = internal.readString(INFO_HASH.name) ?: return null
                // 构造信息对象
                return Info(type, hash)
            }

            /**
             * 将信息写入到 [ItemData]
             */
            @JvmStatic
            fun write(info: Info, data: ItemData) = data.handle(INTERNAL_NODE) {
                // 写入类型
                put(INFO_TYPE.name, NBTString(info.type.toString()))
                // 写入版本信息
                put(INFO_HASH.name, NBTString(info.hash))
            }

        }

    }

}