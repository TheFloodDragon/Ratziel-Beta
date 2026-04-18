r = 0..<4096
letters = &r :: map(|| "a")
&letters :: join("")
