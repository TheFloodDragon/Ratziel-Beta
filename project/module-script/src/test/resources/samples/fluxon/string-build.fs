r = 0..<10000
letters = &r :: map(|| "a")
&letters :: join("")
