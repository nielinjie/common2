package xyz.nietongxue.common.pinyin

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PinyinSimilarityTest {
    val pinyinSimilarity = PinyinSimilarity(PinyinSegmenter())

    @ParameterizedTest
    @CsvSource(
        "你好, 事情, 0.0",
        "你好吗, 您好, 0.3"
    )
    fun test(word1: String, word2: String, expected: Double) {
        val similarity = pinyinSimilarity.similarity(word1, word2)
        val offset = Offset.offset(0.05)
        assertThat(similarity).isCloseTo(expected, offset)
    }
}