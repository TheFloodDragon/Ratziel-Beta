package cn.fd.ratziel.core.contextual

import java.util.concurrent.CopyOnWriteArrayList

/**
 * SimpleContext
 *
 * @author TheFloodDragon
 * @since 2024/6/28 16:19
 */
class SimpleContext(
    /** [ArrayList] 的迭代速率相对较快 **/
    private val list: MutableList<Pair<Class<*>, Any>> = CopyOnWriteArrayList(),
) : ArgumentContext {

    constructor(vararg values: Any) : this() {
        for (value in values) this.list.add(value::class.java to value)
    }

    constructor(vararg values: Any, action: ArgumentContext.() -> Unit) : this(values) {
        action(this)
    }

    override fun <T : Any> popOrNull(type: Class<T>): T? {
        // 查找类
        for (i in 0..list.lastIndex) {
            val find = this.list[i]
            @Suppress("UNCHECKED_CAST")
            if (type.isAssignableFrom(find.first)) {
                return find.second as T
            }
        }
        return null
    }

    override fun put(element: Any) {
        val type = element::class.java
        // 删除所有此类型的对象或者父类型的对象
        this.list.removeIf { it.first.isAssignableFrom(type) }
        // 插入新元素到开头, 以便新元素能被第一个获取到
        this.list.add(0, type to element)
    }

    override fun remove(type: Class<*>) {
        // 删除所有此类型的对象或者子类型的对象
        this.list.removeIf { type.isAssignableFrom(it.first) }
    }

    override fun copy() = SimpleContext().also { it.list.addAll(this.list) }

    override fun args() = list.map { it.second }

}