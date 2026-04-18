var score = 0L
for (i in 0L until 10000L) {
    if (i % 2L == 0L) {
        score += i
    } else if (i % 3L == 0L) {
        score -= i
    } else {
        score += 1
    }
}
score
