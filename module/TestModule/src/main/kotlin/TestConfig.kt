import cn.fd.fdutilities.module.ModuleConfig
import cn.fd.fdutilities.module.ModuleManager
import cn.fd.fdutilities.util.Loader
import java.io.File

object TestConfig : ModuleConfig("ServerTeleport.yml") {

    override fun reload() {
        super.reload()
        Loader.listen<ModuleConfig>(this.file) {
            this.reload()
        }
    }

    override fun File.releaseFile(resourcePath: String) {
        if (!this.exists()) {
            releaseResourceFile(
                resourcePath,
                TestModule::class.java.javaClass.classLoader,
                File(ModuleManager.folder, resourcePath)
            )
        }
    }

}