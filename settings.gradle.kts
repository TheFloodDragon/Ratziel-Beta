rootProject.name = "FDUtilities"

applyAll("project")
applyAll("script")

//Bukkit实现
include("plugin:platform-bukkit")
//全部聚合版
include("plugin:all")

//所有字项目的加载(懒得自己一个一个打)
fun applyAll(name: String) {
    File("${rootDir}\\$name").listFiles()?.filter { it.isDirectory }?.forEach {
        include("$name:${it.name}")
    }
}