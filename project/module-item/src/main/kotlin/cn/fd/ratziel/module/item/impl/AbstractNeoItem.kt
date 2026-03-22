package cn.fd.ratziel.module.item.impl

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.module.item.api.IdentifiedItem
import cn.fd.ratziel.module.item.api.component.ItemComponentHolder
import cn.fd.ratziel.module.item.api.component.ItemComponentType
import cn.fd.ratziel.module.item.api.component.transformer.SerialNbtTransformer
import cn.fd.ratziel.module.item.internal.RefItemStack
import cn.fd.ratziel.module.nbt.delete
import taboolib.module.nms.MinecraftVersion

/**
 * AbstractNeoItem
 *
 * TODO 修改结构以优化性能
 *
 * @author TheFloodDragon
 * @since 2025/5/3 17:57
 */
abstract class AbstractNeoItem : IdentifiedItem, ItemComponentHolder {

    override fun <T : Any> get(type: ItemComponentType<T>): T? {
        return if (MinecraftVersion.versionId >= 12005) {
            val nmsItem = RefItemStack.of(this.data).nmsStack ?: return null
            type.transforming.minecraftTransformer.read(nmsItem)
        } else {
            type.transforming.nbtTransformer.fromNbtTag(this.data.tag)
        }
    }

    override fun <T : Any> set(type: ItemComponentType<T>, value: T) {
        if (MinecraftVersion.versionId >= 12005) {
            editNmsItem { type.transforming.minecraftTransformer.write(it, value) }
            return
        }
        val tag = type.transforming.nbtTransformer.toNbtTag(value, this.data.tag)
        require(tag is NbtCompound) {
            "Legacy NbtTransformer for component '${type.id}' must produce NbtCompound."
        }
        this.data.tag.merge(tag, true)
    }

    override fun remove(type: ItemComponentType<*>) {
        if (MinecraftVersion.versionId >= 12005) {
            editNmsItem { type.transforming.minecraftTransformer.remove(it) }
            return
        }
        val transformer = type.transforming.nbtTransformer
        require(transformer is SerialNbtTransformer.EntryTransformer<*>) {
            "Legacy NbtTransformer for component '${type.id}' must support path deletion."
        }
        this.data.tag.delete(transformer.path)
    }

    private fun editNmsItem(action: (Any) -> Unit) {
        val ref = RefItemStack.of(this.data)
        val nmsItem = ref.nmsStack ?: return
        action(nmsItem)
        val updated = ref.extractData()
        this.data.material = updated.material
        this.data.tag = updated.tag
        this.data.amount = updated.amount
    }

    /**
     * 消耗一定数量的物品
     *
     * @return 是否成功消耗
     */
    fun take(amount: Int): Boolean {
        val current = this.data.amount
        val took = current - amount
        if (took < 0) {
            return false
        } else {
            this.data.amount = took
            return true
        }
    }

}
