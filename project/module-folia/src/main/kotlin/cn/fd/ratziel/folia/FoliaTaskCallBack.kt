package cn.fd.ratziel.folia

import cn.fd.ratziel.folia.lib.ProxyScheduler
import cn.fd.ratziel.folia.lib.ProxyTask

data class FoliaTaskCallBack(
    var task: ProxyTask? = null,
    var scheduler: ProxyScheduler? = null,
) {

    fun cancel() {
        task?.cancel()
    }

}
