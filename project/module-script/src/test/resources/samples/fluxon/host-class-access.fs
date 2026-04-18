sum = 0
for i in 0..<10000 {
 sum += static (java.lang.Integer).TYPE.getName().length()
}
&sum
