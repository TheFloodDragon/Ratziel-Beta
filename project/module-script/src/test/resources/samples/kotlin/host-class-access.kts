var sum = 0
repeat(10000) {
    val clazz = java.lang.Integer::class.java
    sum += clazz.name.length
}
sum
