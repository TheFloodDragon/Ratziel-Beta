total = 0
for i in 0..<100000 {
 total += (((&base + &i) * (&multiplier - (&i % &modulus))) + &offset) / &divisor + (&bias * (&i % 3))
}
&total
