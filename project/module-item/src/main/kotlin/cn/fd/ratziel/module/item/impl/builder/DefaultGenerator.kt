package cn.fd.ratziel.module.item.impl.builder

import cn.altawk.nbt.tag.NbtCompound
import cn.altawk.nbt.tag.NbtTag
import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.ContextualSerializer
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.core.util.digest
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.RatzielItem
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.SimpleData
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture

/**
 * DefaultGenerator
 *
 * @author TheFloodDragon
 * @since 2025/3/22 15:32
 */
class DefaultGenerator(
    /**
     * 原始物品配置 (元素)
     */
    val origin: Element
) : ItemGenerator {

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(SimpleData(), context)

    /**
     * 异步生成物品
     *
     * 注: 此处使用多线程进行 反序列化和应用数据 的操作
     */
    fun buildAsync(data: ItemData, context: ArgumentContext): CompletableFuture<NeoItem> {
        // 生成物品唯一标识符
        val identifier = SimpleIdentifier(origin.name)
        val element = origin.property
        // 处理最终结果 (异步)
        return ItemElement.scope.async {
            // 呼出开始生成的事件
            ItemGenerateEvent.Pre(identifier, this@DefaultGenerator, context, element, data).call()

            // 确定最基础的
            selectBasic(element, data)

            // 生成任务列表
            val tasks = ItemRegistry.registry.map {
                async {
                    val tag = runTask(it, element, context)
                    val event = ItemGenerateEvent.DataGenerate(identifier, this@DefaultGenerator, context, it.type, tag)
                    event.call()
                    event.generatedTag
                }
            }

            // 等待所有任务完成, 然后合并数据
            for (tag in tasks.awaitAll()) {
                if (tag != null && tag is NbtCompound) {
                    data.tag.merge(tag, false)
                }
            }

            // 合成最终结果
            val version = origin.property.toString().digest()
            val result = RatzielItem.of(RatzielItem.Info(identifier, version), data)

            // 呼出生成结束的事件
            val event = ItemGenerateEvent.Post(result.id, this@DefaultGenerator, context, result)
            event.call()
            event.item // 返回最终结果
        }.asCompletableFuture()
    }

    /**
     * 执行任务
     */
    fun runTask(
        integrated: ItemRegistry.Integrated<*>,
        element: JsonElement,
        context: ArgumentContext
    ): NbtTag? {
        // 获取序列化器
        @Suppress("UNCHECKED_CAST")
        val originSerializer = (integrated as ItemRegistry.Integrated<Any>).serializer
        val serializer = if (originSerializer is ContextualSerializer) originSerializer.accept(context) else originSerializer
        // 第一步: 解码成物品组件
        val component = try {
            // 解码
            ItemElement.json.decodeFromJsonElement(serializer, element)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by '$serializer!'! Source element: $element")
            ex.printStackTrace()
            return null
        }
        // 第二步: 编码成物品数据
        try {
            // 编码
            return ItemElement.nbt.encodeToNbtTag(serializer, component)
        } catch (ex: Exception) {
            severe("Failed to transform component by '$serializer'! Source component: $component")
            ex.printStackTrace()
            return null
        }
    }

    companion object {

        @JvmStatic
        private val materialNames = listOf("material", "mat", "materials", "mats")

        /**
         * 确定材料类型
         */
        private fun selectBasic(element: JsonElement, data: ItemData) {
            // 材料设置
            val name = ((element as? JsonObject)?.getBy(materialNames) as? JsonPrimitive)?.contentOrNull
            if (name != null) data.material = MetaMatcher.matchMaterial(name)
        }

    }

}