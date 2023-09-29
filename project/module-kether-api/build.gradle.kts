dependencies {
    api("io.izzel.taboolib:module-kether:$taboolibVersion"){
        /**
         * 删除关于Bukkit的内容
         */
        exclude("taboolib/module/kether/action/game/**")
    }
    installTaboo("expansion-javascript")
}