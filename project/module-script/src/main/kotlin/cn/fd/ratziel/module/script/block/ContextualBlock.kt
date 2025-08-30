package cn.fd.ratziel.module.script.block

/**
 * ContextualBlock
 *
 * @author TheFloodDragon
 * @since 2025/8/30 19:54
 */
class ContextualBlock(
    block: ExecutableBlock,
    val context: BlockContext,
) : ExecutableBlock by block
