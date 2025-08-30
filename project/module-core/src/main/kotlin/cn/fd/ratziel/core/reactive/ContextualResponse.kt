package cn.fd.ratziel.core.reactive

import cn.fd.ratziel.core.Identifier
import cn.fd.ratziel.core.contextual.ArgumentContext

/**
 * ContextualResponse
 *
 * @author TheFloodDragon
 * @since 2025/8/6 20:56
 */
class ContextualResponse(val identifier: Identifier, val context: ArgumentContext) : ResponseBody
