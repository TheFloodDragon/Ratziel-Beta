var sum = 0;
for (var i = 0; i < 100000; i++) {
    var clazz = Java.type("java.lang.Integer");
    sum += clazz.TYPE.getName().length;
}
sum;
