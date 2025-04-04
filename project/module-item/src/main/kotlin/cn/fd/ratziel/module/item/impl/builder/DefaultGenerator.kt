package cn.fd.ratziel.module.item.impl.builder

import cn.altawk.nbt.tag.NbtCompound
import cn.fd.ratziel.core.element.Element
import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.serialization.ContextualSerializer
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
    fun buildAsync(sourceData: ItemData, context: ArgumentContext) = ItemElement.scope.async {
        // 生成基本物品
        val item = NativeSource.generateItem(origin, sourceData) ?: throw IllegalStateException("Failed to generate item source!")

        // 呼出开始生成的事件
        ItemGenerateEvent.Pre(item.id, this@DefaultGenerator, context, origin.property).call()

        // 原生任务: 组件 -> 数据
        val nativeTasks = ItemRegistry.registry.map {
            async { runTask(it, origin.property, context, item.data) }
        }
        // 源任务: 物品源 -> 解析 -> 数据
        val sourcedTasks = ItemRegistry.sources.map {
            async { it.generateItem(origin, context)?.data }
        }

        // 等待所有任务完成, 然后合并数据
        val firstMat = item.data.material
        for (generated in sourcedTasks.plus(nativeTasks).awaitAll()) {
            if (generated != null) {
                // 重新设置材质
                val nextMat = generated.material
                if (nextMat != firstMat) item.data.material = nextMat
                // 合并标签
                item.data.tag.merge(generated.tag, true)
            }
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
        context: ArgumentContext,
        refer: ItemData
    ): ItemData? {
        // 获取序列化器
        @Suppress("UNCHECKED_CAST")
        val serializer = (integrated as ItemRegistry.Integrated<Any>).serializer
            .let { if (it is ContextualSerializer) it.accept(context) else it }
        // 第一步: 解码成物品组件
        val component = try {
            // 解码
            ItemElement.json.decodeFromJsonElement(serializer, element)
        } catch (ex: Exception) {
            severe("Failed to deserialize element by '$serializer'!")
            ex.printStackTrace()
            return null
        }
        // 第二步: 编码成组件数据
        val tag = try {
            // 编码
            ItemElement.nbt.encodeToNbtTag(serializer, component)
        } catch (ex: Exception) {
            severe("Failed to transform component by '$serializer'! Source component: $component")
            ex.printStackTrace()
            return null
        }
        // 第三步: 处理组件数据
        if (tag is NbtCompound) {
            val processor = integrated.processor
            try {
                return processor.process(SimpleData(refer.material, tag))
            } catch (ex: Exception) {
                severe("Failed to process data by '$processor'!")
                ex.printStackTrace()
            }
        }
        return null
    }

}