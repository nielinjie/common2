package xyz.nietongxue.common.spring.scan

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.TypeFilter
import kotlin.reflect.KClass

fun scanPackage(packages: List<String>,typeFilter: TypeFilter):List<KClass<*>> {
    val scanner = ClassPathScanningCandidateComponentProvider(false)
    scanner.addIncludeFilter(typeFilter)
    return packages.flatMap {
        scanner.findCandidateComponents(it).map {
            Class.forName(it.beanClassName).kotlin
        }
    }
}