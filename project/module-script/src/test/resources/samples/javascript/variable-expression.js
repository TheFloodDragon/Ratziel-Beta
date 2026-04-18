var total = 0;
for (var i = 0; i < 10000; i++) {
    total += (((base + i) * (multiplier - (i % modulus))) + offset) / divisor + (bias * (i % 3));
}
total;
