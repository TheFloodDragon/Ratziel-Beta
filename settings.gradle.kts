rootProject.name = "FDUtilities"

include("plugin:bukkit")
//包含所有模块
File("${rootDir}\\module").listFiles()?.filter { it.isDirectory }?.forEach {
    include("module:${it.name}")
}