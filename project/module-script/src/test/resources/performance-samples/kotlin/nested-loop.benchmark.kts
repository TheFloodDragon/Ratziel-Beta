var total = 0L
for (i in 0 until 512) {
    for (j in 0 until 64) {
        total += (i * j) % 7
    }
}
total
