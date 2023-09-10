package cn.fd.ratziel.core.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

/**
 * ProxyCoroutineScope
 *
 * @author TheFloodDragon
 * @since 2023/9/8 21:40
 */
open class ProxyCoroutineScope(
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    protected val scope by lazy { CoroutineScope(dispatcher) }

    fun disableScope() {
        scope.cancel()
    }

}

/**
 * 创建IO协程
 */
open class ProxyCoroutineScopeIO : ProxyCoroutineScope(Dispatchers.IO)