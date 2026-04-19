var total = 0;
for (var i = 0; i < __ITER__; i++) {
    var numerator = ((base + i) * (multiplier - (i % modulus))) + offset;
    total += ((numerator - (numerator % divisor)) / divisor) + (bias * (i % 3));
}
total;
