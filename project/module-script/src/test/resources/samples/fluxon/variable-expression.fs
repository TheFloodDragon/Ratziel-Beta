total = 0
for i in 0..<10000 {
 total += (((&base + &i) * (&multiplier - (&i % &modulus))) + &offset) / &divisor + (&bias * (&i % 3))
}
&total
