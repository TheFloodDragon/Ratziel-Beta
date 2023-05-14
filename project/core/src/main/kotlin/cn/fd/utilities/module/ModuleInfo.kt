package cn.fd.utilities.module

import java.io.File

class ModuleInfo(
    //模块标识符
    val identifier: String,
    //是否启用
    val isEnabled: Boolean = true,
    //模块所在的文件(.jar或.class)
    val filePath: File? = null
)