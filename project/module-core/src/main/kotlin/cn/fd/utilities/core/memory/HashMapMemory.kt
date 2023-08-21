package cn.fd.utilities.core.memory

/**
 * HashMapMemory
 *
 * @author: TheFloodDragon
 * @since 2023/8/21 10:11
 */
open class HashMapMemory<K, V> : Memory {

    /**
     * 容器
     */
    protected val memory: HashMap<K, V> = hashMapOf()

    /**
     * 加入到容器
     */
    protected fun addToMemory(key: K, value: V) {
        memory[key] = value
    }

    /**
     * 从容器中删除
     */
    protected fun removeFromMemory(key: K) {
        memory.remove(key)
    }

    /**
     * 清空容器
     */
    protected fun clearMemory() {
        memory.clear()
    }

}