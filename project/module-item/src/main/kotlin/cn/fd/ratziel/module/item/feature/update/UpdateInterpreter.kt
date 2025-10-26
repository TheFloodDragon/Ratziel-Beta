package cn.fd.ratziel.module.item.feature.update

import cn.altawk.nbt.NbtPath
import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.contextual.ArgumentContext
import cn.fd.ratziel.core.util.getBy
import cn.fd.ratziel.module.item.ItemManager
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemInterpreter
import cn.fd.ratziel.module.item.api.builder.ItemStream
import cn.fd.ratziel.module.item.impl.RatzielItem
import cn.fd.ratziel.module.item.impl.builder.DefaultGenerator
import cn.fd.ratziel.module.item.internal.ItemSheet
import cn.fd.ratziel.module.nbt.delete
import cn.fd.ratziel.module.nbt.read
import cn.fd.ratziel.module.nbt.write
import kotlinx.serialization.json.*
import java.util.concurrent.CompletableFuture

/**
 * UpdateInterpreter
 *
 * @author TheFloodDragon
 * @since 2025/10/19 11:20
 */
class UpdateInterpreter : ItemInterpreter {

    /**
     * 更新是否被启用
     */
    var enabled = false

    /**
     * 更新是否自动
     */
    var automatic = false

    /**
     * 物品当前版本
     */
    lateinit var version: String

    /**
     * 受保护的 [NbtPath] 列表
     */
    val protectedPaths: MutableList<NbtPath> = arrayListOf(
        NbtPath(ItemSheet.ENCHANTMENT_COMPONENT) // 默认保护附魔标签不被覆盖
    )

    override suspend fun preFlow(stream: ItemStream) {
        // 获取物品版本
        version = (stream.item.getValue() as RatzielItem).info.hash

        val section = (stream.fetchProperty() as? JsonObject)?.get("update") ?: return
        if (section is JsonPrimitive) {
            enabled = section.boolean
        } else if (section is JsonObject) {
            enabled = section["enabled"]?.jsonPrimitive?.boolean ?: true

            // 读取是否自动更新
            automatic = section.getBy("auto", "automatic")?.jsonPrimitive?.boolean ?: false

            // 读取受保护的 NBT 路径
            val paths = section.getBy("protected", "protection")?.jsonArray ?: return
            for (content in paths.map { it.jsonPrimitive.content.trim() }) {
                // 以 ! 开头的路径表示移除保护
                if (content.startsWith('!')) {
                    val path = NbtPath(content.removePrefix("!"))
                    protectedPaths.remove(path)
                } else {
                    // 添加保护
                    protectedPaths.add(NbtPath(content))
                }
            }
        }
    }

    companion object {

        /**
         * 更新源物品
         * @param source 源物品
         *
         * @return 更新后的物品异步结果, 若不需要更新则返回 null
         */
        @JvmStatic
        fun updateItem(source: RatzielItem, context: ArgumentContext): CompletableFuture<out NeoItem>? {
            val update = source.service[ItemUpdate::class.java]
            // 服务不存在或者未启用则不更新
            if (update?.enabled != true) return null
            // 检查版本是否相同 (版本相同就更新)
            if (source.info.hash == update.version) return null

            // 提前保存受保护的节点 (克隆)
            val protected = update.protectedPaths.associateWith { source.data.tag.read(it)?.clone() }
            // 更新前的自定义数据 (克隆)
            val customData = (source.data.tag[ItemSheet.CUSTOM_DATA_COMPONENT]!! as NbtCompound).clone()

            // 获取执行器
            val generator = ItemManager.generator(source.identifier) as DefaultGenerator
            // 重新生成物品
            val newItem = generator.buildAsync(context, source).thenApply {
                // 将保护的节点设置回去
                for ((path, value) in protected) {
                    // 原来这里是啥, 更新后一定也是啥
                    if (value != null) {
                        it.data.tag.write(path, value)
                    } else {
                        it.data.tag.delete(path)
                    }
                }

                // 合并自定义数据 (特殊处理)
                val newCustomData = it.data.tag[ItemSheet.CUSTOM_DATA_COMPONENT]!! as NbtCompound
                val merged = customData.merge(newCustomData, false)
                it.data.tag[ItemSheet.CUSTOM_DATA_COMPONENT] = merged

                // 返回最终物品
                return@thenApply it
            }

            return newItem
        }

    }

}