package cn.fd.ratziel.common

import cn.fd.ratziel.common.element.ElementMatcher
import cn.fd.ratziel.core.element.ElementType
import taboolib.common5.cbool
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
     * 工作空间选项表
     */
    val options: Map<String, Any?>,
) {

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
    val filter = options["filter"]?.toString()?.toRegex() ?: Regex("^[^#!].*")

    /**
     * 是否监听此工作空间内的文件变更
     */
    val listen = options["listen"]?.cbool ?: true

    /**
     * 是否使用文件名称作为元素名称
     */
    val useFileNameAsElementName = options["use-filename"].cbool

    /**
     * 统一的元素类型, 空代表不统一类型
     */
    val unifiedElementType: ElementType? = options["unified-type"]?.toString()
        ?.takeUnless { it.equals("None", true) }
        ?.let { ElementMatcher.matchTypeOrNull(it) }

}