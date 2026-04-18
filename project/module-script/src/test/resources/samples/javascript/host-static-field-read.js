var integerClass = Java.type("java.lang.Integer");
var sum = 0;
for (var i = 0; i < 100000; i++) {
    sum += integerClass.MAX_VALUE;
}
sum;
