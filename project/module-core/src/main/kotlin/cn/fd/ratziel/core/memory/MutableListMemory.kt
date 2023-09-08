package cn.fd.ratziel.core.memory

/**
 * MutableListMemory
 *
 * @author TheFloodDragon
 * @since 2023/9/8 22:24
 */
open class MutableListMemory<T> : Memory<MutableList<T>> {

    /**
     * 容器
     */
    protected val memory: MutableList<T> = mutableListOf()

    /**
     * 加入到容器
     */
    fun addToMemory(element: T) {
        memory.add(element)
    }

    /**
     * 从容器中删除
     */
    fun removeFromMemory(element: T) {
        memory.remove(element)
    }

    /**
     * 清空容器
     */
    fun clearMemory() {
        memory.clear()
    }

}