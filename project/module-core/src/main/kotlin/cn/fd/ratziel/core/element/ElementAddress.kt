package cn.fd.ratziel.core.element

import java.io.File

/**
 * ElementAddress
 * 代表一个元素的地址
 * 元素地址目前分两种: 文件地址 和 空地址
 *
 * @author TheFloodDragon
 * @since 2023/8/21 10:49
 */
open class ElementAddress(
    /**
     * 元素标识符
     */
    open val id: String,
    /**
     * 元素类型
     */
    open val type: ElementType,
    /**
     * 元素所在文件路径(若为空则不是文件地址)
     */
    open val file: File?,
) {
    /**
     * 元素地址是否是文件地址
     */
    open fun isFileAddress(): Boolean {
        return !isEmptyAddress()
    }

    open fun isEmptyAddress(): Boolean {
        return file == null
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ElementAddress) {
            this.id == other.id && this.type == other.type && this.file == other.file
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (file?.hashCode() ?: 0)
        return result
    }

}