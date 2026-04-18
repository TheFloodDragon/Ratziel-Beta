val map = LinkedHashMap<String, Int>(10000)
for (i in 0 until 10000) {
    map["k$i"] = i
}
map
