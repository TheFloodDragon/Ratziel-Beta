var mathClass = Java.type("java.lang.Math");
var sum = 0;
for (var i = 0; i < 10000; i++) {
    sum += mathClass.abs(-123456);
}
sum;
