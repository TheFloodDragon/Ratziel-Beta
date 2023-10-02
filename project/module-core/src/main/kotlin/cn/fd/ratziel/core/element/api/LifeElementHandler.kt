package cn.fd.ratziel.core.element.api

import taboolib.common.LifeCycle

/**
 * LifeElementHandler
 *
 * @author TheFloodDragon
 * @since 2023/10/2 11:48
 */
interface LifeElementHandler : ElementHandler {

    /**
     * 标记处理时的插件生命周期
     */
    val lifeCycle: LifeCycle

}