@file:OptIn(ExperimentalSerializationApi::class)

package cn.fd.ratziel.common.function.block.provided

import cn.fd.ratziel.core.function.ArgumentContext
import cn.fd.ratziel.core.function.block.ExecutableBlock
import cn.fd.ratziel.core.serialization.serializers.TolerantListSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * ConditionBlock
 *
 * @author TheFloodDragon
 * @since 2025/3/23 09:16
 */
@Serializable
data class ConditionBlock(
    @JsonNames("if", "condition", "conditions")
    @Serializable(with = TolerantListSerializer::class)
    val funcIf: List<ExecutableBlock>,
    @JsonNames("then")
    val funcThen: ExecutableBlock?,
    @JsonNames("else")
    val funcElse: ExecutableBlock?
) : ExecutableBlock {

    override fun execute(context: ArgumentContext) {
        if (funcIf.all { it.execute(context) == true })
            funcThen?.execute(context)
        else funcElse?.execute(context)
    }

}