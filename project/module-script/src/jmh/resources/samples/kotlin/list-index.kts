val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)
var sum = 0L
for (i in 0 until __ITER__) {
    sum += numbers[i % 8]
}
sum
