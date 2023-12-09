package cn.fd.ratziel.core.function

import java.lang.reflect.Type

/**
 * UnsupportedTypeException - 不受支持的类型异常
 *
 * @author TheFloodDragon
 * @since 2023/12/9 16:41
 */
class UnsupportedTypeException(type: Type) : IllegalStateException(
    "Unsupported Type $type !"
) {

    constructor(clazz: Class<*>) : this(clazz as Type)

    constructor(obj: Any) : this(obj::class.java)

}