package cn.fd.utilities.common.util

/**
 * 从一个含有列表(元素)的列表中合并子列表(元素)
 * 效果:
 *  listA[ listB[ 1, 2, 3], listB[ 4, 5, 6] ]
 *  To: ListB[ 1, 2, 3, 4, 5, 6]
 */
fun <T> List<List<T>>.mergeAll(): List<T> {
    return mutableListOf<T>().also { out ->
        this.forEach { out.addAll(it) }
    }
}