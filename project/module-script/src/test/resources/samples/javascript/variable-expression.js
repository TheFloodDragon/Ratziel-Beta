var total = 0;
for (var i = 0; i < __ITER__; i++) {
    total += (((base + i) * (multiplier - (i % modulus))) + offset) / divisor + (bias * (i % 3));
}
total;
