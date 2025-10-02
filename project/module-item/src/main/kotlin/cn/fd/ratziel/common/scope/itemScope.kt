package cn.fd.ratziel.common.scope

import cn.fd.ratziel.common.block.BlockScope
import cn.fd.ratziel.block.scope.ScriptScope

val BlockScope.Companion.ItemScope by lazy {
    BlockScope.ScriptScope
}
