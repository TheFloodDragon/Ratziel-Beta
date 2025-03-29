package cn.fd.ratziel.module.item

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtString
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.module.item.RatzielItem.Companion.isRatzielItem
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.impl.ItemSheet
import cn.fd.ratziel.module.item.impl.service.GlobalServiceManager
import cn.fd.ratziel.module.item.internal.nms.RefItemStack
import cn.fd.ratziel.module.nbt.handle
import cn.fd.ratziel.module.nbt.read
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
        val RATZIEL_DATA_PATH = NbtPath(
            NbtPath.NameNode(ItemSheet.CUSTOM_DATA_COMPONENT),
            NbtPath.NameNode("Ratziel")
        )

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
            return isRatzielItem(RefItemStack.of(itemStack).tag)
        }

        /**
         * 检查目标标签, 判断是否为 [RatzielItem]
         */
        @JvmStatic
        fun isRatzielItem(tag: NbtCompound): Boolean {
            return tag.read(RATZIEL_DATA_PATH, false) is NbtCompound
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

        constructor(type: String, hash: String) : this(SimpleIdentifier(type), hash)

        constructor(type: Identifier, hash: Int) : this(type, hash.toString())

        companion object {

            /**
             * 专有信息节点
             */
            @JvmStatic
            private val INTERNAL_PATH = RATZIEL_DATA_PATH.plus(NbtPath.NameNode("internal"))

            /**
             * 内部信息 - [id]
             */
            private const val INFO_TYPE = "type"

            /**
             * 内部信息 - [hash]
             */
            private const val INFO_HASH = "hash"

            /**
             * 从 [ItemData] 中读取信息
             */
            @JvmStatic
            fun read(data: ItemData): Info? {
                // 获取内部信息
                val internal = data.tag.read(INTERNAL_PATH) as? NbtCompound ?: return null
                // 读取类型
                val type = internal[INFO_TYPE] as? NbtString ?: return null
                // 读取版本信息
                val hash = internal[INFO_HASH] as? NbtString ?: return null
                // 构造信息对象
                return Info(type.content, hash.content)
            }

            /**
             * 将信息写入到 [ItemData]
             */
            @JvmStatic
            fun write(info: Info, data: ItemData) = data.tag.handle(INTERNAL_PATH) {
                // 写入类型
                put(INFO_TYPE, NbtString(info.type.content))
                // 写入版本信息
                put(INFO_HASH, NbtString(info.hash))
            }

        }

    }

}