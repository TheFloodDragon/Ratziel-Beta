var sum = 0L
repeat(10000) {
    sum += java.lang.Integer.MAX_VALUE.toLong()
}
sum
