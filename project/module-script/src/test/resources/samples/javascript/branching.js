var score = 0;
for (var i = 0; i < __ITER__; i++) {
    if (i % 2 === 0) {
        score += i;
    } else if (i % 3 === 0) {
        score -= i;
    } else {
        score += 1;
    }
}
score;
