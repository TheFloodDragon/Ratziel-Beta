package cn.fd.ratziel.module.item.builder

import cn.fd.ratziel.core.SimpleIdentifier
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.serialization.getBy
import cn.fd.ratziel.core.util.digest
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.RatzielItem
import cn.fd.ratziel.module.item.SimpleData
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.NeoItem
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.util.MetaMatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import taboolib.common.platform.function.severe
import java.util.concurrent.CompletableFuture

/**
 * DefaultItemGenerator
 *
 * @author TheFloodDragon
 * @since 2024/4/13 17:34
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

        // 呼出开始生成的事件
        val element = origin.property
        ItemGenerateEvent.Pre(identifier, this, element, context).call()

        // 处理最终结果 (异步)
        return ItemElement.scope.async {

            // 确定最基础的
            selectBasic(element, data)

            // 生成任务列表
            val tasks: List<Job> = ItemRegistry.getSortedList().map {
                @Suppress("UNCHECKED_CAST")
                createTask(this@async, it as ItemRegistry.Integrated<Any>, element, context, data)
            }

            // 等待所有任务完成
            tasks.joinAll()

            // 合成最终结果
            val version = origin.property.toString().digest()
            val item = RatzielItem.of(RatzielItem.Info(identifier, version), data)

            // 呼出生成结束的事件
            val event = ItemGenerateEvent.Post(item.id, this@DefaultGenerator, item, context)
            event.call()
            event.item // 返回最终结果
        }.asCompletableFuture()
    }

    /**
     * 创建任务
     * 流程: 反序列化 -> 转化数据 (应用到 [sourceData])
     */
    fun createTask(
        scope: CoroutineScope,
        integrated: ItemRegistry.Integrated<Any>,
        element: JsonElement,
        context: ArgumentContext,
        data: ItemData
    ) = scope.launch {
        // 第一步: 反序列成物品组件
        val serializer = integrated.serializer
        val component = try {
            serializer.deserialize(element, context)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by \"$serializer!\"! Source element: $element")
            ex.printStackTrace()
            return@launch
        }
        // 第二步: 转化数据
        val transformer = integrated.transformer
        try {
            // 应用数据
            transformer.transform(data, component)
        } catch (ex: Exception) {
            severe("Failed to transform component by \"$transformer\"! Source component: $component")
            ex.printStackTrace()
        }
    }

    /**
     * 确定材料类型
     */
    fun selectBasic(element: JsonElement, data: ItemData) {
        // 材料设置
        val name = ((element as? JsonObject)?.getBy(materialNames) as? JsonPrimitive)?.contentOrNull
        if (name != null) data.material = MetaMatcher.matchMaterial(name)
    }

    @JvmField
    val materialNames = listOf("material", "mat", "materials", "mats")

}