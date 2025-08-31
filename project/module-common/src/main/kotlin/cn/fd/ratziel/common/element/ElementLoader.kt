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
     * 分配工作空间中的文件
     * @return 被分走的文件
     */
    fun allocate(workspace: Workspace, files: Collection<File>): Collection<File>

    /**
     * 加载工作空间中的文件元素
     */
    fun load(workspace: Workspace, file: File): Result<List<Element>>

    companion object {

        /**
         * 加载器列表
         */
        @JvmField
        val loaders: ArrayDeque<ElementLoader> = ArrayDeque(listOf(DefaultElementLoader))

        /**
         * 分配元素文件
         */
        @JvmStatic
        fun allocate(workspace: Workspace): Map<ElementLoader, Set<File>> {
            val files = workspace.filteredFiles.toMutableSet()
            val map = LinkedHashMap<ElementLoader, Set<File>>(loaders.size)
            for (loader in loaders) {
                val allocated = loader.allocate(workspace, files).toSet()
                map[loader] = allocated
                files.removeAll(allocated)
            }
            return map
        }

    }

}