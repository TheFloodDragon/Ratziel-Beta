var total = 0L
for (i in 0 until __ITER__) {
    for (j in 0 until 32) {
        total += (i * j) % 7
    }
}
total
