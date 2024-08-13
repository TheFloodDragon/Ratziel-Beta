package cn.fd.ratziel.module.item.impl.builder

import cn.fd.ratziel.core.serialization.MutableJsonObject
import cn.fd.ratziel.function.ArgumentContext
import cn.fd.ratziel.module.item.api.builder.ItemResolver
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import java.util.concurrent.ConcurrentHashMap

/**
 * CommonItemResolver
 *
 * @author TheFloodDragon
 * @since 2024/8/13 10:54
 */
class CommonItemResolver(
    val element: JsonElement
) : ItemResolver {

    /**
     * 访问的节点, 通过 [CommonItemSerializer] 获取
     */
    val visibleNode: Array<String> get() = CommonItemSerializer.usedNodes

    /**
     * 构建器 (支持多线程和异步)
     */
    val builder: MutableJsonObject = newMap()

    /**
     * 解析元素
     *
     * @return 解析完后, 返回值只包含 [visibleNode] 内的节点, 因此 [CommonItemSerializer] 需要作为最后一个解析
     */
    override fun resolve(element: JsonElement, context: ArgumentContext): JsonElement = runBlocking {

        TODO("Not yet implemented")
    }

    /**
     * 创建一个可变的 [MutableJsonObject]
     * 同时保证线程安全, 以支持 [resolve] 的操作
     */
    private fun newMap() = MutableJsonObject(ConcurrentHashMap())

}