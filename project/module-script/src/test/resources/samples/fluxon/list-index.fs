numbers = [1, 2, 3, 4, 5, 6, 7, 8]
sum = 0
for i in 0..<10000 {
 sum += &numbers[&i % 8]
}
&sum
