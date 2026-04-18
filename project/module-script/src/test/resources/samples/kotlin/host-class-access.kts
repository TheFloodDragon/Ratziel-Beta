var sum = 0
repeat(100000) {
    val clazz = java.lang.Integer::class.java
    sum += clazz.name.length
}
sum
