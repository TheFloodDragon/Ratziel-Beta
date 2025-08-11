package cn.fd.ratziel.common

import cn.fd.ratziel.core.element.ElementType
import java.io.File

/**
 * Workspace
 *
 * @author TheFloodDragon
 * @since 2025/4/12 23:11
 */
class Workspace(
    /**
     * 工作空间路径
     */
    val folder: File,
    /**
     * 元素文件过滤器
     */
    val filter: Regex,
    /**
     * 是否监听此工作空间内的文件变更
     */
    val listen: Boolean,
    /**
     * 是否使用文件名称作为元素名称
     */
    val useFileNameAsElementName: Boolean,
    /**
     * 统一的元素类型, 空代表不统一类型
     */
    val unifiedElementType: ElementType?,
) {

    /**
     * 工作空间文件列表 (未过滤)
     */
    val files: Sequence<File> = folder.walk().filter { it.isFile }

}