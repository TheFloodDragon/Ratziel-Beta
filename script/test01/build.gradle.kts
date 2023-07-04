dependencies {
    rootProject.allprojects.forEach {
        if (it.parent?.name == "project" || it.parent?.name == "plugin") {
            implementation(it)
        }
    }
}