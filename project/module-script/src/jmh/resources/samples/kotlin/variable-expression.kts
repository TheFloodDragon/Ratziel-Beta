var total = 0L
for (i in 0 until __ITER__) {
    total += (((base + i) * (multiplier - (i % modulus))) + offset) / divisor + (bias * (i % 3))
}
total
