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
        // 迭代查找类
        val find = this.list.find { type.isAssignableFrom(it.first) }
            ?.second ?: return null
        @Suppress("UNCHECKED_CAST")
        return find as T
    }

    override fun put(element: Any) {
        // 插入新元素到开头, 以便新元素能被第一个获取到
        this.list.add(0, element::class.java to element)
    }

    override fun remove(type: Class<*>) {
        // 删除所有此类型的对象或者子类型的对象
        this.list.removeIf { type.isAssignableFrom(it.first) }
    }

    override fun copy() = SimpleContext(this.args())

    override fun args(): Collection<Any> = list

}