rootProject.name = "FDUtilities"

include("plugin:platform-bukkit")

include("project:common")
include("project:core")
include("project:taboolib-generate-bukkit")

//包含所有模块
File("${rootDir}\\module").listFiles()?.filter { it.isDirectory }?.forEach {
    include("module:${it.name}")
}