var total = 0L
for (i in 0L until 10000L) {
    total += (((base + i) * (multiplier - (i % modulus))) + offset) / divisor + (bias * (i % 3))
}
total
