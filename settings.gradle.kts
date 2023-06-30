rootProject.name = "FDUtilities"

//所有字项目的加载(懒得自己一个一个打)
File("${rootDir}\\project").listFiles()?.filter { it.isDirectory }?.forEach {
    include("project:${it.name}")
}

//Bukkit实现
include("plugin:platform-bukkit")
//全部聚合版
include("plugin:all")


//包含所有模块 TODO 应该重命名为脚本系统(FScript-抽象)
//File("${rootDir}\\module").listFiles()?.filter { it.isDirectory }?.forEach {
//    include("module:${it.name}")
//}