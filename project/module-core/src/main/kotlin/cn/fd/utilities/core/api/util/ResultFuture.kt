package cn.fd.utilities.core.api.util

/**
 * 该结果的成功与否在未来
 * 结果只能被改变一次！
 */
class ResultFuture<T>(val value: T) {

    private var result: ResultType = ResultType.FUTURE

    /**
     * 使结果成功
     */
    fun success(): ResultFuture<T> {
        if (result == ResultType.FUTURE)
            result = ResultType.SUCCESS
        return this
    }

    /**
     * 使结果为失败
     */
    fun failure(): ResultFuture<T> {
        if (result == ResultType.FUTURE)
            result = ResultType.FAILURE
        return this
    }

    /**
     * 获取结果
     */
    fun getResult(): ResultType {
        return result
    }

    /**
     * 获取结果 (Boolean)
     * 如果是ResultType.FUTURE则返回false
     */
    fun getResultB(): Boolean {
        return result == ResultType.SUCCESS
    }

    /**
     * 获取值
     */
    fun get(): T {
        return value
    }

    /**
     * 返回值是否为空
     */
    fun isNull(): Boolean {
        return this.value == null
    }

}