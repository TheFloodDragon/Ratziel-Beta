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
     * 判断文件是否应该交给此 [ElementLoader] 加载
     */
    fun accepts(workspace: Workspace, file: File): Boolean

    /**
     * 加载工作空间中的文件元素
     */
    fun load(workspace: Workspace, file: File): Result<List<Element>>

    companion object {

        /**
         * 加载器列表
         */
        @JvmField
        val loaders: MutableList<ElementLoader> = mutableListOf(DefaultElementLoader)

        /**
         * 选择加载器
         */
        @JvmStatic
        fun select(workspace: Workspace, file: File): ElementLoader? {
            for (loader in loaders) {
                if (loader.accepts(workspace, file)) {
                    return loader
                }
            }
            return null
        }

    }

}