package xyz.nietongxue.common.bpmn

import com.alibaba.compileflow.engine.core.extension.ExtensionRealization
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import kotlin.test.Test

class ScanTest {
    @Test
    fun test() {
        val packages = listOf("com.alibaba.compileflow","com.compileflow.extension")
        val provider : ClassPathScanningCandidateComponentProvider = ClassPathScanningCandidateComponentProvider(false)
        provider.addIncludeFilter(AnnotationTypeFilter(ExtensionRealization::class.java))
        for (packageName in packages) {
            val components = provider.findCandidateComponents(packageName)
            for (component in components) {
                println(component.beanClassName)
            }
        }
    }
}