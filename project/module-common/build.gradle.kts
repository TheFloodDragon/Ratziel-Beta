// 资源处理
tasks.processResources {
    filesMatching("**/*.json") {
        expand(
            "serialization" to rootProject.libs.versions.serialization.get(),
            "adventureApi" to rootProject.libs.versions.adventureApi.get(),
            "adventurePlatform" to rootProject.libs.versions.adventurePlatform.get(),
        )
    }
}