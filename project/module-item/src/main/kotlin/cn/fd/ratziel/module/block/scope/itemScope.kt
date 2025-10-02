package cn.fd.ratziel.module.block.scope

import cn.fd.ratziel.common.block.BlockScope
import cn.fd.ratziel.module.common.block.scope.ScriptScope

val BlockScope.Companion.ItemScope by lazy {
    BlockScope.ScriptScope
}
