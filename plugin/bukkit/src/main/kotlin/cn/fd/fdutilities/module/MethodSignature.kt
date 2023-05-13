package cn.fd.fdutilities.module

import java.util.*

class MethodSignature constructor(private val name: String, private val params: Array<Class<*>>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as MethodSignature
        return name == that.name && params.contentEquals(that.params)
    }

    override fun hashCode(): Int {
        var result = Objects.hash(name)
        result = 31 * result + params.contentHashCode()
        return result
    }
}