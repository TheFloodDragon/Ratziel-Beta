numbers = [1, 2, 3, 4, 5, 6, 7, 8]
sum = 0
for i in 0..<__ITER__ {
 sum += &numbers[&i % 8]
}
&sum
