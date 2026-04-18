var total = 0;
for (var i = 0; i < 512; i++) {
    for (var j = 0; j < 64; j++) {
        total += (i * j) % 7;
    }
}
total;
