package cn.fd.ratziel.module.item.impl.action.provided

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake


@Awake(LifeCycle.LOAD)
private fun activate() {
    WorldContactListener
}
