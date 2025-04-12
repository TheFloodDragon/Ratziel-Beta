package cn.fd.ratziel.common

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
    val path: File,
    /**
     * 工作空间文件列表
     */
    val files: Sequence<File>,
    /**
     * 是否监听此工作空间内的文件变更
     */
    val listen: Boolean,
) {

    override fun toString(): String {
        return "Workspace(path=${path.absolutePath})"
    }

}