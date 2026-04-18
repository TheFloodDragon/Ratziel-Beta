var numbers = [1, 2, 3, 4, 5, 6, 7, 8];
var sum = 0;
for (var i = 0; i < __ITER__; i++) {
    sum += numbers[i % 8];
}
sum;
