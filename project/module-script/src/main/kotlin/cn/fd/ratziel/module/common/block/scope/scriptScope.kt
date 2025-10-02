package cn.fd.ratziel.module.common.block.scope

import cn.fd.ratziel.common.block.BlockScope
import cn.fd.ratziel.common.block.scope.DefaultScope
import cn.fd.ratziel.module.common.block.provided.ScriptBlock

val BlockScope.Companion.ScriptScope by lazy {
    BlockScope.DefaultScope.plus { ScriptBlock.Parser() }
}
