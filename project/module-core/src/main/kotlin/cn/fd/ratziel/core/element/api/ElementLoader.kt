package cn.fd.ratziel.core.element.api

import cn.fd.ratziel.core.element.Element
import java.io.File

/**
 * ElementLoader - 元素加载器
 *
 * @author TheFloodDragon
 * @since 2024/4/21 9:39
 */
interface ElementLoader {

    /**
     * 从文件中加载元素
     */
    fun load(file: File): List<Element>

}