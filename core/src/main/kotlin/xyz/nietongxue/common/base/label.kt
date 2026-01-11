package xyz.nietongxue.common.base

import org.springframework.util.LinkedMultiValueMap

/*

在软件系统设计中，tag、label、meta、annotation这几个概念有何异同？


总结
元数据是“父概念”，是所有描述信息的统称。
标签和标记是“子概念”，是元数据中用于分类、标识的具体实现。它们极其相似，标签更偏用户视角和扁平化，标记可能更偏系统视角和状态性，但混用很常见。 tag vs label
注解是“特殊的子概念”，特指在编程中附加到代码上的、由工具处理的元数据，用于实现声明式逻辑。
理解这些概念的异同，有助于你在设计系统、编写代码或进行技术交流时，更精确地使用术语，从而提升沟通效率和设计质量。
 */


typealias Labels = LinkedMultiValueMap<String, Any>

interface HasLabels {
    val labels: Labels
}

fun emptyLabels() = LinkedMultiValueMap<String, Any>()