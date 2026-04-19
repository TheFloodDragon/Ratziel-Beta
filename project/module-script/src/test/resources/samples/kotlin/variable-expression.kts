var total = 0
for (i in 0 until __ITER__) {
    val numerator = ((base + i) * (multiplier - (i % modulus))) + offset
    total += ((numerator - (numerator % divisor)) / divisor) + (bias * (i % 3))
}
total
