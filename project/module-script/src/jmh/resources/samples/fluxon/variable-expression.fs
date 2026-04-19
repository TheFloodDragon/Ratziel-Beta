total = 0
for i in 0..<__ITER__ {
 total += (((&base + &i) * (&multiplier - (&i % &modulus))) + &offset) / &divisor + (&bias * (&i % 3))
}
&total
