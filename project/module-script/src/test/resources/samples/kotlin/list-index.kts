val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)
var sum = 0L
for (i in 0 until 10000) {
    sum += numbers[i % 8]
}
sum
