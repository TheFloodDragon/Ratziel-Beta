package cn.fd.ratziel.module.item.impl.builder

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.SimpleContext
import cn.fd.ratziel.module.item.ItemElement
import cn.fd.ratziel.module.item.ItemRegistry
import cn.fd.ratziel.module.item.api.ItemData
import cn.fd.ratziel.module.item.api.builder.ItemGenerator
import cn.fd.ratziel.module.item.api.event.ItemGenerateEvent
import cn.fd.ratziel.module.item.impl.SimpleData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.serialization.json.JsonElement
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
    val origin: Element,
) : ItemGenerator {

    /**
     * 预解析的 [JsonElement]
     */
    private val preResolved = CompletableFuture.supplyAsync({
        DefaultResolver.resolve(origin.property, SimpleContext())
    }, ItemElement.executor)

    override fun build() = build(SimpleContext())

    /**
     * 构建物品
     */
    override fun build(context: ArgumentContext) = buildAsync(SimpleData(), context)

    /**
     * 异步生成物品
     *
     * 注: 此处使用多线程进行 反序列化和应用数据 的操作
     */
    fun buildAsync(sourceData: ItemData, context: ArgumentContext) = ItemElement.scope.async {
        // 生成基本物品 (本地源物品)
        val item = NativeSource.generateItem(origin, sourceData) ?: throw IllegalStateException("Failed to generate item source!")

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(item.id, this@DefaultGenerator, context, origin.property).call()

        // 解析后的元素内容
        var resolved = preResolved.get()

        // 解释器
        val interceptorTasks = ItemRegistry.interceptors.map {
            async { it.intercept(Element(origin.identifier, resolved), context) }
        }.awaitAll()

        // 再次解析
        resolved = DefaultResolver.resolve(resolved, context)

        // 源任务: 元素 -> 数据
        val sourcedTasks = ItemRegistry.sources.map {
            async { it.generateItem(Element(origin.identifier, resolved), context)?.data }
        }

        // 原生任务: 元素(解析过后的) -> 组件 -> 数据
        val nativeTasks = ItemRegistry.registry.map {
            async { runTask(it, resolved, item.data) }
        }

        val originalMaterial = item.data.material
        val originalAmount = item.data.amount
        // 等待所有任务完成, 然后合并数据
        val allTasks = interceptorTasks
            .plus(sourcedTasks.awaitAll())
            .plus(nativeTasks.awaitAll().map { it.getOrNull() })
        for (generated in allTasks) {
            // 跳过空数据
            if (generated == null) continue

            // 设置材质
            val nowMat = generated.material
            if (nowMat != originalMaterial) item.data.material = nowMat
            // 设置数量
            val nowAmount = generated.amount
            if (nowAmount != originalAmount) item.data.amount = nowAmount
            // 合并标签
            item.data.tag.merge(generated.tag, true)
        }

        // 呼出生成结束的事件
        val event = ItemGenerateEvent.Post(item.id, this@DefaultGenerator, context, item)
        event.call()
        event.item // 返回最终结果
    }.asCompletableFuture()

    /**
     * 执行任务
     */
    fun runTask(
        integrated: ItemRegistry.Integrated<*>,
        element: JsonElement,
        refer: ItemData,
    ): Result<ItemData> {
        // 获取序列化器
        @Suppress("UNCHECKED_CAST")
        val serializer = (integrated as ItemRegistry.Integrated<Any>).serializer
        // 第一步: 解码成物品组件
        val component = try {
            // 解码
            ItemElement.json.decodeFromJsonElement(serializer, element)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by '$serializer'!")
            ex.printStackTrace()
            return Result.failure(ex)
        }
        // 第二步: 编码成组件数据
        val tag = try {
            // 编码
            ItemElement.nbt.encodeToNbtTag(serializer, component)
        } catch (ex: Exception) {
            severe("Failed to transform component by '$serializer'! Source component: $component")
            ex.printStackTrace()
            return Result.failure(ex)
        }
        // 第三步: 处理组件数据
        if (tag is NbtCompound) {
            val processor = integrated.processor
            try {
                val processed = processor.process(SimpleData(refer.material, tag))
                return Result.success(processed)
            } catch (ex: Exception) {
                severe("Failed to process data by '$processor'!")
                ex.printStackTrace()
                return Result.failure(ex)
            }
        }
        return Result.failure(IllegalStateException("Unknown exception during data generation!"))
    }

}