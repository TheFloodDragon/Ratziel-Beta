var sum = 0
repeat(__ITER__) {
    val clazz = java.lang.Integer::class.java
    sum += clazz.name.length
}
sum
