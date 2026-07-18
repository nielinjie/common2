package xyz.nietongxue.common.pinyin

import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper
import xyz.nietongxue.common.collections.editDistance
import kotlin.math.abs
import kotlin.math.max

class PinyinSimilarity(
    val pinyinSegmenter: PinyinSegmenter
) {
    /**
     * 判断两个词是否为近似音
     */
    fun similarity(word1: String, word2: String): Double {
        val p2 = pinyinSegmenter.segment(word2)
        return similarity(word1, p2)

    }

    fun similarity(word1: String, p2: List<PinyinUnit>): Double {
        val p1 = pinyinSegmenter.segment(word1)

        try {
            if (abs(p1.size - p2.size) > 1) {
                return 0.0 // 长度差异过大
            }
            val distance = calculateDistance(p1, p2)
            val similarity = 1.0 - distance.toDouble() / max(p1.size, p2.size)

            return similarity
        } catch (e: Exception) {
            return 0.0
        }
    }

    private fun calculateDistance(p1: List<PinyinUnit>, p2: List<PinyinUnit>): Int {
        return editDistance(p1, p2, ::isPhoneticallySimilar)
    }

    /**
     * 声韵近似规则（关键）
     * FIXME 这里有明显问题，此处应该计算的是两个音节是否相同
     * 但这里是声母韵母分开计算的。比如，显然不能说韵母相同，音节就相同。
     */


    fun yunmuSimilar(a: PinyinUnit.TowPhase, b: PinyinUnit.TowPhase): Boolean {
        if (a.yunmu == b.yunmu) return true

        // 前后鼻音
        if (a.yunmu.replace("g", "") == b.yunmu.replace("g", "")
        ) {
            return true
        }

        // i / ü
        if ((a.yunmu == "iu" && b.yunmu == "ü")
            || (a.yunmu == "ü" && b.yunmu == "iu")
        ) {
            return true
        }
        return false
    }

    fun shengmuSimilar(a: PinyinUnit.TowPhase, b: PinyinUnit.TowPhase): Boolean {
        if (a.shengmu == b.shengmu) return true
        if (a.shengmu == "n" && b.shengmu == "l") return true
        if (a.shengmu == "l" && b.shengmu == "n") return true

        if (a.shengmu == "zh" && b.shengmu == "z") return true
        if (a.shengmu == "z" && b.shengmu == "zh") return true
        if (a.shengmu == "ch" && b.shengmu == "c") return true
        if (a.shengmu == "c" && b.shengmu == "ch") return true
        if (a.shengmu == "sh" && b.shengmu == "s") return true
        if (a.shengmu == "s" && b.shengmu == "sh") return true

        return false
    }

    fun phaseOneSimilarity(a: PinyinUnit.OnePhase, b: PinyinUnit.OnePhase): Boolean {
        return a.yunmu == b.yunmu


    }


    fun isPhoneticallySimilar(a: PinyinUnit, b: PinyinUnit): Boolean {
        return when (a) {
            is PinyinUnit.TowPhase -> {
                when (b) {
                    is PinyinUnit.TowPhase -> {
                        shengmuSimilar(a, b) && yunmuSimilar(a, b)
                    }

                    is PinyinUnit.OnePhase -> {
                        false
                    }
                }
            }

            is PinyinUnit.OnePhase -> {
                when (b) {
                    is PinyinUnit.TowPhase -> {
                        false
                    }

                    is PinyinUnit.OnePhase -> {
                        phaseOneSimilarity(a, b)
                    }
                }
            }
        }
    }
}

class PinyinSegmenter {
    /**
     * 拆分为：声母 + 韵母（去声调）
     */
    fun segment(hanzi: String): List<PinyinUnit> {
        val pinyinStr: String = PinyinHelper.convertToPinyinString(
            hanzi, " ", PinyinFormat.WITHOUT_TONE
        )
        return segmentForPinyinString(pinyinStr)
    }

    fun segmentForPinyinString(pinyinStr: String): List<PinyinUnit> {
        val result: MutableList<PinyinUnit> = mutableListOf()
        for (py in pinyinStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            result.add(PinyinUnit.fromString(py))
        }
        return result
    }


}

/**
 * 音节
 * 声母+韵母
 * 只有韵母
 * 整体认读
 */

sealed interface PinyinUnit {
    data class TowPhase(val shengmu: String, val yunmu: String) : PinyinUnit
    data class OnePhase(val yunmu: String) : PinyinUnit
    companion object {
        fun fromString(pinyin: String): PinyinUnit {
            val shengmu = extractShengmu(pinyin)
            val yunmu = pinyin.substring(shengmu.length)
            return if (shengmu.isEmpty()) {
                OnePhase(yunmu)
            } else {
                TowPhase(shengmu, yunmu)
            }
        }

        fun extractShengmu(pinyin: String): String {
            val sm = arrayOf<String?>(
                "b", "p", "m", "f", "d", "t", "n", "l",
                "g", "k", "h", "j", "q", "x",
                "zh", "ch", "sh", "r", "z", "c", "s"
            )
            for (s in sm) {
                if (pinyin.startsWith(s!!)) return s
            }
            return ""
        }
    }
}


