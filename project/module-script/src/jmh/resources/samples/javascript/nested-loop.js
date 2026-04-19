var total = 0;
for (var i = 0; i < __ITER__; i++) {
    for (var j = 0; j < 32; j++) {
        total += (i * j) % 7;
    }
}
total;
