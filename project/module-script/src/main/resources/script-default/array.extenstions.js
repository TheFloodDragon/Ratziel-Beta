/**
 * 创建一个在范围内的 int 数组
 *
 * @param start {int} 起始值 (开)
 * @param end {int} 终止值 (闭)
 * @param step {int} 步长
 * @returns {int[]}
 */
function range(start, end, step) {
    return Array.from(
        {length: (end - start) / step + 1},
        (value, index) => start + index * step);
}

/**
 * 取列表首个元素
 */
Array.prototype.first = function () {
    return this[0]
}

/**
 * 取列表最后一个元素
 */
Array.prototype.last = function () {
    return this[this.length - 1]
}
