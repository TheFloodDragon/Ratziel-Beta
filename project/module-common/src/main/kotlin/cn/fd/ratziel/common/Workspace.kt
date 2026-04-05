package cn.fd.ratziel.common

import cn.fd.ratziel.core.element.*
import java.io.File

/**
 * Workspace
 *
 * @author TheFloodDragon
 * @since 2025/4/12 23:11
 */
data class Workspace(
    /**
     * 工作空间路径
     */
    val folder: File,
    /**
     * 工作空间默认元素配置
     */
    val configuration: ElementConfiguration,
) {

    init {
        val elementName = configuration[ElementConfiguration.elementName]
        require(elementName == null || elementName == FILE_NAME_ELEMENT_NAME) {
            "Workspace elementName must be '$FILE_NAME_ELEMENT_NAME' or omitted, but was '$elementName'."
        }
    }

    /**
     * 工作空间文件列表 (未过滤)
     */
    val allFiles: Sequence<File> = folder.walk().filter { it.isFile }

    /**
     * 工作空间文件列表 (过滤后的)
     */
    val filteredFiles: Sequence<File> = allFiles.filter { filter.matches(it.name) }

    /**
     * 元素文件过滤器
     */
    val filter: Regex = configuration[ElementConfiguration.filter]?.toRegex() ?: Regex("^[^#!].*")

    /**
     * 是否监听此工作空间内的文件变更
     */
    val listen: Boolean = configuration[ElementConfiguration.listen] ?: true

}
