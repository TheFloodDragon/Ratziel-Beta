package cn.fd.utilities.core.element

import java.io.File

/**
 * ElementAddress
 * 代表一个元素的地址
 * 元素地址目前分两种: 文件地址 和 空地址
 *
 * @author TheFloodDragon
 * @since 2023/8/21 10:49
 */
class ElementAddress(
    /**
     * 元素标识符
     */
    val id: String,
    /**
     * 元素类型
     */
    val type: ElementType,
    /**
     * 元素所在文件路径(若为空则不是文件地址)
     */
    val file: File?
) {
    /**
     * 元素地址是否是文件地址
     */
    fun isFileAddress(): Boolean {
        return !isEmptyAddress()
    }

    fun isEmptyAddress(): Boolean {
        return file == null
    }

}