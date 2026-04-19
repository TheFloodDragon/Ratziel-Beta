score = 0
for i in 0..<__ITER__ {
 if &i % 2 == 0 {
  score += &i
 } else if &i % 3 == 0 {
  score -= &i
 } else {
  score += 1
 }
}
&score
