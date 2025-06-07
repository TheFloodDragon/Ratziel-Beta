package cn.fd.ratziel.platform.bukkit.particle.internal

import net.minecraft.core.particles.ParticleParam
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_21_R3.CraftParticle
import org.bukkit.util.Vector
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.nmsProxy

/**
 * NMSParticle
 *
 * @author TheFloodDragon
 * @since 2025/6/7 12:34
 */
abstract class NMSParticle {

    /**
     * 创建粒子数据包
     */
    abstract fun createParticlePacket(
        particle: Particle,
        data: Any?,
        overrideLimiter: Boolean,
        alwaysShow: Boolean,
        x: Double,
        y: Double,
        z: Double,
        xDist: Float,
        yDist: Float,
        zDist: Float,
        maxSpeed: Float,
        count: Int,
    ): Any

    companion object {

        @JvmStatic
        val INSTANCE by lazy {
            nmsProxy<NMSParticle>()
        }

        /**
         * 创建粒子数据包
         */
        @JvmStatic
        fun createParticlePacket(particle: Particle, location: Location, offset: Vector, speed: Float = 0f, count: Int = 1, data: Any? = null): Any {
            return INSTANCE.createParticlePacket(
                particle, data,
                overrideLimiter = true, alwaysShow = true,
                location.x, location.y, location.z,
                offset.x.toFloat(), offset.y.toFloat(), offset.z.toFloat(),
                speed, count,
            )
        }

    }

}

class NMSParticleImpl : NMSParticle() {

    override fun createParticlePacket(
        particle: Particle,
        data: Any?,
        overrideLimiter: Boolean,
        alwaysShow: Boolean,
        x: Double,
        y: Double,
        z: Double,
        xDist: Float,
        yDist: Float,
        zDist: Float,
        maxSpeed: Float,
        count: Int,
    ): Any {
        if (MinecraftVersion.isHigher(MinecraftVersion.V1_12)) {
            // 创建 ParticleParam
            val param = if (MinecraftVersion.versionId >= 12002) {
                try {
                    CraftParticle.createParticleParam(particle, data)
                } catch (_: NoSuchMethodException) {
                    org.bukkit.craftbukkit.v1_16_R1.CraftParticle.toNMS(particle, data)
                }
            } else org.bukkit.craftbukkit.v1_16_R1.CraftParticle.toNMS(particle, data)
            // 生成数据包
            if (MinecraftVersion.versionId >= 12101) {
                return PacketPlayOutWorldParticles(
                    param as ParticleParam,
                    overrideLimiter,
                    alwaysShow,
                    x, y, z,
                    xDist, yDist, zDist,
                    maxSpeed,
                    count
                )
            } else {
                return net.minecraft.server.v1_16_R1.PacketPlayOutWorldParticles(
                    param as net.minecraft.server.v1_16_R1.ParticleParam,
                    alwaysShow,
                    x, y, z,
                    xDist, yDist, zDist,
                    maxSpeed,
                    count
                )
            }
        } else {
            return net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles(
                org.bukkit.craftbukkit.v1_12_R1.CraftParticle.toNMS(particle),
                alwaysShow,
                x.toFloat(), y.toFloat(), z.toFloat(),
                xDist, yDist, zDist,
                maxSpeed,
                count
            )
        }
    }

}