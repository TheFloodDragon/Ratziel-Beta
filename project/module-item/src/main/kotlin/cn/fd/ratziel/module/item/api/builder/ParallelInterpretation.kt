package cn.fd.ratziel.module.item.api.builder

/**
 * ParallelInterpretation - 可并行解释标记
 *
 * 解释器调度过程中, 被标记 [ParallelInterpretation] 的任务会并行执行。
 * 而下一个 未被标记的任务 (串行任务) 会在前面的所有并行任务完成后再执行。
 *
 * @author TheFloodDragon
 * @since 2025/7/8 21:08
 */
annotation class ParallelInterpretation
