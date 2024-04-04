package cn.fd.ratziel.common.event

import taboolib.common.event.InternalEvent

/**
 * WorkspaceLoadEvent
 *
 * @author TheFloodDragon
 * @since 2024/4/4 21:13
 */
open class WorkspaceLoadEvent : InternalEvent() {
    class Start : WorkspaceLoadEvent()
    class End : WorkspaceLoadEvent()
}