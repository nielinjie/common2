package xyz.nietongxue.common.collections

import kotlin.math.max
import kotlin.math.min


fun <T> levenshteinDistance(list1: List<T>, list2: List<T>, areSimilar: (T, T) -> Boolean): Int {
    return editDistance(list1, list2, areSimilar)
}

/**
 * 计算两个列表之间的编辑距离（Levenshtein Distance），支持自定义元素相似性判断。
 *
 * 编辑距离定义为：将一个列表转换为另一个列表所需的最少操作次数。
 * 允许的操作包括：插入一个元素、删除一个元素、替换一个元素。
 *
 * 使用动态规划（Dynamic Programming）实现，时间复杂度 O(n×m)，空间复杂度 O(n×m)。
 *
 * @param list1 第一个列表
 * @param list2 第二个列表
 * @param areSimilar 判断两个元素是否"相似"（替换代价为0）的函数。
 *                   当返回 true 时，视为同一元素（替换代价为 0）；
 *                   当返回 false 时，视为不同元素（替换代价为 1）。
 * @return 两个列表之间的最小编辑距离
 */
fun <T> editDistance(list1: List<T>, list2: List<T>, areSimilar: (T, T) -> Boolean): Int {
    val len1 = list1.size
    val len2 = list2.size
    // dp[i][j] 表示将 list1 的前 i 个元素转换为 list2 的前 j 个元素所需的最少操作次数
    val dp = Array<IntArray?>(len1 + 1) { IntArray(len2 + 1) }

    // 初始化：将 list1 的前 i 个元素转换为空列表，需要删除 i 次
    for (i in 0..len1) dp[i]!![0] = i
    // 初始化：将空列表转换为 list2 的前 j 个元素，需要插入 j 次
    for (j in 0..len2) dp[0]!![j] = j

    for (i in 1..len1) {
        for (j in 1..len2) {
            // 判断当前两个元素是否"相似"，相似则替换代价为 0，否则为 1
            val cost = if (areSimilar(list1[i - 1], list2[j - 1])) 0 else 1
            dp[i]!![j] = min(
                min(
                    // 删除 list1[i-1]：从 dp[i-1][j] 转移，代价 +1
                    dp[i - 1]!![j] + 1,
                    // 在 list1 中插入 list2[j-1]：从 dp[i][j-1] 转移，代价 +1
                    dp[i]!![j - 1] + 1
                ),
                // 替换（或不替换）：从 dp[i-1][j-1] 转移，代价为 cost（0 或 1）
                dp[i - 1]!![j - 1] + cost
            )
        }
    }
    return dp[len1]!![len2]
}

/**
 * 计算两个列表的相似度，返回值范围 [0.0, 1.0]。
 *
 * 基于编辑距离计算相似度：
 * - 当两个列表完全相同时，返回 1.0
 * - 当两个列表差异最大时（一个为空，另一个非空），返回 0.0
 *
 * 计算公式：similarity = 1.0 - editDistance / max(list1.size, list2.size)
 *
 * @param list1 第一个列表
 * @param list2 第二个列表
 * @param areSimilar 判断两个元素是否相似（替换代价为 0）的函数
 * @return 两个列表的相似度，范围 [0.0, 1.0]
 */
fun <T> listSimilarity(list1: List<T>, list2: List<T>, areSimilar: (T, T) -> Boolean): Double {
    if (list1.isEmpty() && list2.isEmpty()) return 1.0
    if (list1.isEmpty() || list2.isEmpty()) return 0.0
    val distance = editDistance(list1, list2, areSimilar)
    return 1.0 - distance.toDouble() / max(list1.size, list2.size)
}
