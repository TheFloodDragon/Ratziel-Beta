package cn.fd.ratziel.common.event

import taboolib.common.platform.event.ProxyEvent
import java.io.File

/**
 * WorkspaceInitializeEvent
 * 工作空间初始化时触发
 *
 * @author TheFloodDragon
 * @since 2023/10/4 13:45
 */
class WorkspaceInitializeEvent(
    /**
     * 路径
     */
    var path: File,
    /**
     * 是否复制默认文件
     */
    var copyDefaults: Boolean,
) : ProxyEvent()