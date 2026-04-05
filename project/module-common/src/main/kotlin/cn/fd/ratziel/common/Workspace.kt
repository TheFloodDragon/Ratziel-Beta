package cn.fd.ratziel.common

import cn.fd.ratziel.core.element.ElementConfiguration
import cn.fd.ratziel.core.element.ElementConfigurationKeys
import cn.fd.ratziel.core.element.FILE_NAME_ELEMENT_NAME
import cn.fd.ratziel.core.element.elementName
import cn.fd.ratziel.core.element.filter
import cn.fd.ratziel.core.element.listen
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
        val configuredElementName = with(ElementConfigurationKeys) {
            configuration.getNoDefault(elementName)
        }
        require(configuredElementName == null || configuredElementName == FILE_NAME_ELEMENT_NAME) {
            "Workspace elementName must be '$FILE_NAME_ELEMENT_NAME' or omitted, but was '$configuredElementName'."
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
    val filter: Regex = with(ElementConfigurationKeys) {
        configuration.getNoDefault(filter)
    }?.toRegex() ?: Regex("^[^#!].*")

    /**
     * 是否监听此工作空间内的文件变更
     */
    val listen: Boolean = with(ElementConfigurationKeys) {
        configuration.getNoDefault(listen)
    } ?: true
}
