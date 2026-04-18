var score = 0;
for (var i = 0; i < 10000; i++) {
    if (i % 2 === 0) {
        score += i;
    } else if (i % 3 === 0) {
        score -= i;
    } else {
        score += 1;
    }
}
score;
