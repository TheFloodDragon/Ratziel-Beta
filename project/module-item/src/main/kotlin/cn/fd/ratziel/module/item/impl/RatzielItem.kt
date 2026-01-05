package cn.fd.ratziel.module.item.impl

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtString
import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.NamedIdentifier
import cn.fd.ratziel.module.item.api.DataHolder
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.service.ItemServiceManager
import cn.fd.ratziel.module.item.impl.component.ItemSheet
import cn.fd.ratziel.module.item.util.asItemData
import cn.fd.ratziel.module.item.util.extractData
import cn.fd.ratziel.module.item.util.writeTo
import cn.fd.ratziel.module.nbt.NbtAdapter
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
open class RatzielItem private constructor(
    /**
     * 物品信息
     */
    open val info: Info,
    /**
     * 物品数据
     */
    override val data: ItemData,
) : AbstractNeoItem(), DataHolder by Holder(data) {

    /**
     * 物品标识符
     */
    override val identifier: Identifier get() = info.identity

    /**
     * 物品服务
     */
    override val service get() = ItemServiceManager[identifier]

    /**
     * 克隆 [RatzielItem]
     */
    override fun clone(): RatzielItem {
        return RatzielItem(this.info, this.data.clone())
    }

    override fun toString(): String {
        return "RatzielItem(info=$info, data=$data)"
    }

    companion object {

        /**
         * 物品节点
         */
        @JvmStatic
        val RATZIEL_PATH = NbtPath(
            NbtPath.NameNode(ItemSheet.CUSTOM_DATA_COMPONENT),
            NbtPath.NameNode("Ratziel")
        )

        /**
         * 物品数据节点
         */
        @JvmStatic
        val RATZIEL_DATA_PATH = RATZIEL_PATH + NbtPath.NameNode("data")

        /**
         * 由 [ItemData] 创建 [RatzielItem]
         * 同时会向 [data] 中写入 [info]
         *
         * @return [RatzielItem]
         */
        @JvmStatic
        fun of(info: Info, data: ItemData): RatzielItem {
            Info.write(info, data)
            return RatzielItem(info, data)
        }

        /**
         * 由 [ItemData] 创建 [RatzielItem]
         *
         * @return 若目标不是 [RatzielItem], 返回空
         */
        @JvmStatic
        fun of(data: ItemData): RatzielItem? {
            val info = Info.read(data) ?: return null
            return RatzielItem(info, data)
        }

        /**
         * 将目标 [ItemStack] 转为 [RatzielItem]
         *
         * @return 若目标不是 [RatzielItem], 返回空
         */
        @JvmStatic
        fun of(itemStack: ItemStack): RatzielItem? {
            if (itemStack.isAir()) return null
            val itemData = itemStack.extractData()
            return of(itemData)
        }

        /**
         * 将目标 [ItemStack] 转为 [RatzielItem.Sourced]
         *
         * @return 若目标不是 [RatzielItem], 返回空
         */
        @JvmStatic
        fun sourced(source: ItemStack?, overwrite: Boolean = true): Sourced? {
            if (source == null) return null
            val neoItem = of(source) ?: return null
            return Sourced(neoItem, source, overwrite)
        }

        /**
         * 检查 [ItemData], 判断是否具有 专有信息 [Info]
         *
         * @return 目标是否为 [RatzielItem]
         */
        @JvmStatic
        fun isRatzielItem(data: ItemData): Boolean {
            return Info.read(data) != null
        }

        /**
         * 检查 [ItemStack], 判断是否具有 专有信息 [Info]
         *
         * @return 目标是否为 [RatzielItem]
         */
        @JvmStatic
        fun isRatzielItem(itemStack: ItemStack): Boolean {
            return isRatzielItem(itemStack.asItemData())
        }

    }

    class Sourced(
        /**
         * [RatzielItem]
         */
        @JvmField
        var neoItem: RatzielItem,
        /**
         * 源物品 [ItemStack]
         */
        @JvmField
        val source: ItemStack,
        /**
         * 是否覆写源物品
         */
        @JvmField
        var overwrite: Boolean = true,
    ) : RatzielItem(neoItem.info, neoItem.data) {

        override val info get() = neoItem.info
        override val data get() = neoItem.data

        /**
         * 尝试重写 源物品 [source]
         */
        @JvmOverloads
        fun overwrite(force: Boolean = false) {
            if (this.overwrite || force) this.neoItem.writeTo(this.source)
        }

    }

    /**
     * [RatzielItem] 自定义数据 [DataHolder]
     */
    class Holder(val data: ItemData) : DataHolder {

        /**
         * 是否含有数据
         */
        override fun has(name: String): Boolean {
            return (this.data.tag.read(RATZIEL_DATA_PATH) as? NbtCompound)
                ?.contains(name) == true
        }

        /**
         * 读取指定数据
         */
        override fun get(name: String): Any? {
            return (this.data.tag.read(RATZIEL_DATA_PATH) as? NbtCompound)
                ?.get(name)?.let { NbtAdapter.unbox(it) }
        }

        /**
         * 写入指定数据
         */
        override fun set(name: String, data: Any) {
            this.data.tag.handle(RATZIEL_DATA_PATH) { put(name, NbtAdapter.box(data)) }
        }

        /**
         * 拆箱所有数据并转换成数据集
         */
        override fun toDataMap(): Map<String, Any> {
            val originMap = this.data.tag.read(RATZIEL_DATA_PATH) as? NbtCompound ?: return emptyMap()
            return originMap.mapValues { NbtAdapter.unbox(it.value) } // 拆箱
        }

    }

    /**
     * [RatzielItem] 专有信息
     */
    data class Info(
        /**
         * 物品身份标识
         */
        val identity: Identifier,
        /**
         * 物品版本信息 (生成此物品时的元素哈希)
         */
        val hash: String,
    ) {

        constructor(identifier: String, hash: String) : this(NamedIdentifier(identifier), hash)

        companion object {

            /**
             * 专有信息节点
             */
            @JvmStatic
            private val INTERNAL_PATH = RATZIEL_PATH + NbtPath.NameNode("internal")

            /**
             * 内部信息 - [identifier]
             */
            private const val INFO_ID = "identity"

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
                val type = internal[INFO_ID] as? NbtString ?: return null
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
                put(INFO_ID, NbtString(info.identity.content))
                // 写入版本信息
                put(INFO_HASH, NbtString(info.hash))
            }

        }

    }

}