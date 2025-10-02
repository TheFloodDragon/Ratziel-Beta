/**
 * roll 一 roll
 * @param prob {number} roll 中球的概率 (百分制)
 * @returns {boolean} 是否 roll 中球
 */
function roll(prob) {
    return Math.random() <= (prob / 100)
}

/**
 * 带权放回随机抽样
 *
 * @param n 抽样个数
 * @param weights
 * @returns {*[]}
 */
Array.prototype.choice = function (n, weights) {
    let num = n || 1;
    if (weights && this.length !== weights.length) {
        throw new Error("Inconsistent length of the original array and the weight array.");
    }

    let result = [];
    let cumulativeWeights = [];
    let sum = 0;

    if (weights) {
        // 计算权重总和
        for (let i = 0; i < weights.length; i++) {
            sum += weights[i];
        }

        // 计算累计权重
        for (let i = 0; i < weights.length; i++) {
            cumulativeWeights[i] = (cumulativeWeights[i - 1] || 0) + weights[i] / sum;
        }
    }

    for (let i = 0; i < num; i++) {
        let random = Math.random();
        if (weights) {
            for (let j = 0; j < cumulativeWeights.length; j++) {
                if (random < cumulativeWeights[j]) {
                    result.push(this[j]);
                    break;
                }
            }
        } else {
            result.push(this[Math.floor(Math.random() * this.length)]);
        }
    }
    return result;
};

/**
 * 带权不放回随机抽样
 *
 * @param n 抽样数
 * @param weights 权数组
 * @returns {*[]}
 */
Array.prototype.sample = function (n, weights) {
    let num = n || 1;
    if (weights && this.length !== weights.length) {
        throw new Error("Inconsistent length of the original array and the weight array.");
    }

    let result = [];
    let items = this.slice();
    let itemWeights = weights ? weights.slice() : undefined;

    for (let i = 0; i < num; i++) {
        let sum = 0;
        let cumulativeWeights = [];

        if (itemWeights) {
            // 计算当前权重总和
            for (let j = 0; j < itemWeights.length; j++) {
                sum += itemWeights[j];
            }

            // 计算当前累计权重
            for (let j = 0; j < itemWeights.length; j++) {
                cumulativeWeights[j] = (cumulativeWeights[j - 1] || 0) + itemWeights[j] / sum;
            }
        }

        let random = Math.random();
        for (let j = 0; j < items.length; j++) {
            if (itemWeights) {
                if (random < cumulativeWeights[j]) {
                    result.push(items[j]);
                    items.splice(j, 1); // 删除选中的元素
                    itemWeights.splice(j, 1); // 删除相应的权重
                    break;
                }
            } else {
                if (random < (j + 1) / items.length) {
                    result.push(items[j]);
                    items.splice(j, 1); // 删除选中的元素
                    break;
                }
            }
        }
    }
    return result;
};
