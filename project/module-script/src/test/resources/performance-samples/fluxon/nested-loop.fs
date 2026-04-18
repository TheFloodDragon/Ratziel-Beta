total = 0
for i in 0..<512 {
 for j in 0..<64 {
  total += (&i * &j) % 7
 }
}
&total
