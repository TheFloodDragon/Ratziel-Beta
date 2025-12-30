package cn.fd.ratziel.module.item.internal.nms

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.api.component.ComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.impl.component.CachedComponentHolder
import cn.fd.ratziel.module.item.impl.component.NbtNodeIdentifier
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.write
import taboolib.common.UnsupportedVersionException
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy

/**
 * NMSComponent
 * 
 * @author TheFloodDragon
 * @since 2025/11/29 21:35
 */
abstract class NMSComponent {

    /**
     * 获取组件持有器
     */
    abstract fun createComponentHolder(nmsItem: Any): ComponentHolder

    /**
     * 1.20.5+ only: CustomDataComponentTransformer
     */
    open fun customDataComponentTransformer(): ItemComponentType.Transformer<NbtCompound> = throw UnsupportedVersionException()

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            if (MinecraftVersion.versionId >= 12005)
                nmsProxy<NMSComponent>("{name}Impl2")
            else NMSComponentImpl1()
        }

    }

}

class NMSComponentImpl1 : NMSComponent() {

    private val ItemComponentType<*>.path get() = (this.identifier as NbtNodeIdentifier).path

    override fun createComponentHolder(nmsItem: Any) = object : CachedComponentHolder<NbtTag>() {

        override fun getRaw(type: ItemComponentType<*>): NbtTag? {
            val root = NMSItem.INSTANCE.getTag(nmsItem) ?: return null
            // 直接读取标签数据
            return root.read(type.path)
        }

        override fun setRaw(type: ItemComponentType<*>, raw: NbtTag?) {
            if (raw == null) {
                // 填空跳到删除
                removeRaw(type); return
            }
            val root = NMSItem.INSTANCE.getTag(nmsItem) ?: NbtCompound().also {
                NMSItem.INSTANCE.setTag(nmsItem, it) // 没有根标签则创建并设置
            }
            root.write(type.path, raw, true)
        }

        override fun removeRaw(type: ItemComponentType<*>) {
            NMSItem.INSTANCE.getTag(nmsItem)?.delete(type.path)
        }

        override fun <T : Any> exchangeFromRaw(type: ItemComponentType<T>, raw: NbtTag): T {
            return ItemElement.nbt.decodeFromNbtTag(type.serializer, raw)
        }

        override fun <T : Any> exchangeToRaw(type: ItemComponentType<T>, value: T): NbtTag {
            return ItemElement.nbt.encodeToNbtTag(type.serializer, value)
        }

    }

}
