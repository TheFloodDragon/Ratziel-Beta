package cn.fd.ratziel.core.function

import taboolib.library.reflex.ClassField
import taboolib.library.reflex.ClassMethod
import taboolib.library.reflex.ClassStructure
import taboolib.library.reflex.Reflection

/**
 * 获取类中的方法 (不安全)
 * 注意: 请务必确认类中的同类型参数是唯一的,否则会出现其他方法的错误调用!
 * @param name 不确定的方法名,会尝试 (若成功则是安全的,不安全性在于 [findMethod] )
 */
fun ClassStructure.getMethodUnsafe(
    name: String,
    vararg parameter: Class<*>,
    returnType: Class<*>? = null,
) = try {
    getMethodByType(name, *parameter) // 尝试通过方法名和参数类型寻找
} catch (_: NoSuchMethodException) {
    findMethod(null, returnType, true, *parameter) // 尝试根据线索寻找
}

/**
 * 寻找类中的方法
 * @param name 匹配方法名 (若空则不匹配)
 * @param returnType 匹配返回值类型 (若空则不匹配)
 * @param matchParameter 是否匹配参数
 * @param parameter 匹配参数
 */
fun ClassStructure.findMethod(
    name: String? = null,
    returnType: Class<*>? = null,
    matchParameter: Boolean = true,
    vararg parameter: Class<*>,
) = this.findMethod {
    // 匹配方法名
    (name == null || it.name == name) &&
            // 匹配返回值类型
            (returnType == null || it.returnType == returnType) &&
            // 匹配参数类型
            (!matchParameter || Reflection.isAssignableFrom(
                it.parameterTypes, parameter.toList().toTypedArray()
            ))
} ?: throw NoSuchMethodException("${this.name}#$name(${parameter.joinToString(";") { it.name }}):$returnType")

fun ClassStructure.findMethod(predicate: (ClassMethod) -> Boolean) = this.methods.firstOrNull(predicate)

/**
 * 获取类中的字段 (不安全)
 * 注意: 请务必确认类中的同类型字段只有一个,否则会出现其他字段的错误获取!
 * @param name 不确定的字段名,会尝试 (若成功则是安全的,不安全性在于 [findField] )
 */
fun ClassStructure.getFieldUnsafe(
    name: String,
    type: Class<*>? = null,
) = try {
    getField(name) // 尝试通过方法名和参数类型寻找
} catch (_: NoSuchMethodException) {
    findField(null, type) // 尝试根据线索寻找
}

/**
 * 寻找类中的字段
 * @param name 匹配字段名 (若空则不匹配)
 * @param type 匹配字段类型 (若空则不匹配)
 */
fun ClassStructure.findField(
    name: String? = null,
    type: Class<*>? = null,
) = findField {
    (name == null || it.name == name) && (type == null || it.fieldType == type)
} ?: throw NoSuchFieldException("${this.name}#$name:$type")

fun ClassStructure.findField(predicate: (ClassField) -> Boolean) = this.fields.firstOrNull(predicate)

/**
 * 判断 [parent] 是否是当前类的 "父类"
 * 其实就是反转了 [Class.isAssignableFrom]
 */
fun Class<*>.isAssignableTo(parent: Class<*>) = parent.isAssignableFrom(this)

/**
 * 查找类是否存在
 * @return 是否能成功找到该类
 */
fun hasClass(clazz: String) = runCatching { Class.forName(clazz) }.isSuccess

fun hasClass(clazz: String, initialize: Boolean = false, loader: ClassLoader) =
    runCatching { Class.forName(clazz, initialize, loader) }.isSuccess