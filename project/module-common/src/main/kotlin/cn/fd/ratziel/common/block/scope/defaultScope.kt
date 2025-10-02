package cn.fd.ratziel.common.block.scope

import cn.fd.ratziel.common.block.BlockScope
import cn.fd.ratziel.common.block.provided.ConditionBlock
import cn.fd.ratziel.common.block.provided.MultiLineBlock
import cn.fd.ratziel.common.block.provided.ValueBlock

val BlockScope.Companion.DefaultScope by lazy {
    BlockScope(
        { ConditionBlock.Parser },
        { MultiLineBlock.Parser },
        { ValueBlock.Parser },
    )
}
