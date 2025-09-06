package cn.fd.ratziel.common.element

import cn.fd.ratziel.common.Workspace
import cn.fd.ratziel.core.element.Element
import java.io.File

/**
 * ElementLoader
 *
 * @author TheFloodDragon
 * @since 2025/8/11 14:31
 */
interface ElementLoader {

    /**
     * 判断文件 [file] 是否应该由此 [ElementLoader] 加载
     */
    fun accepts(file: File, workspace: Workspace): Boolean

    /**
     * 加载工作空间中的文件元素
     */
    fun load(file: File, workspace: Workspace): Result<List<Element>>

    companion object {

        /**
         * 加载器列表
         */
        @JvmField
        val loaders: ArrayDeque<ElementLoader> = ArrayDeque(listOf(DefaultElementLoader))

        /**
         * 分配加载器
         */
        @JvmStatic
        fun allocate(file: File, workspace: Workspace): ElementLoader? {
            for (loader in loaders) {
                if (loader.accepts(file, workspace)) {
                    return loader
                }
            }
            return null
        }

    }

}