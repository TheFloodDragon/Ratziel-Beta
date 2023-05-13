rootProject.name = "FDUtilities"

include("project:bukkit")
include("plugin")
//包含所有模块
File("${rootDir}\\module").listFiles()?.filter { it.isDirectory }?.forEach {
    include("module:${it.name}")
}