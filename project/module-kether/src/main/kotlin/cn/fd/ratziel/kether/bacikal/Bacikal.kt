package cn.fd.ratziel.kether.bacikal

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * Bacikal
 *
 * @author Lanscarlos
 * @since 2023-08-20 21:29
 */
object Bacikal {

    lateinit var service: BacikalService

    @Awake(LifeCycle.LOAD)
    fun init() {
        service = DefaultBacikalService
    }

}