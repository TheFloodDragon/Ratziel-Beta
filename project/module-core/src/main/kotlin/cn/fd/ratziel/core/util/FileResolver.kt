@file:Suppress("NOTHING_TO_INLINE")

package cn.fd.ratziel.core.util

import java.io.File

/**
 * FileResolver
 *
 * @author TheFloodDragon
 * @since 2025/10/2 20:50
 */
interface FileResolver {

    /**
     * 解析文件路径
     */
    fun resolve(path: String, baseFile: File?): File?

    companion object {

        /**
         * 解析文件路径
         */
        @JvmStatic
        fun resolveBy(baseFile: File?, path: String, vararg resolvers: FileResolver): File {
            if (path.startsWith("!")) { // 使用绝对路径解析
                return File(path.drop(1))
            } else {
                // 解析器解析
                for (resolver in resolvers) {
                    return resolver.resolve(path, baseFile) ?: continue
                }
                // 相对路径解析 & 无基文件则使用绝对路径解析
                return baseFile?.resolve(path) ?: File(path)
            }
        }

    }

}

/**
 * 解析文件路径
 */
inline fun File?.resolveBy(path: String, vararg resolvers: FileResolver) = FileResolver.resolveBy(this, path, *resolvers)
