var sum = 0L
repeat(100000) {
    sum += java.lang.Integer.MAX_VALUE.toLong()
}
sum
