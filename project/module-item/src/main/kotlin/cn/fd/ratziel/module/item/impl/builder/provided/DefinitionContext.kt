package cn.fd.ratziel.module.item.impl.builder.provided

import java.util.concurrent.ConcurrentHashMap

/**
 * DefinitionContext
 *
 * @author TheFloodDragon
 * @since 2025/5/11 10:18
 */
class DefinitionContext(
    private val map: MutableMap<String, Any?> = ConcurrentHashMap(),
) : MutableMap<String, Any?> by map