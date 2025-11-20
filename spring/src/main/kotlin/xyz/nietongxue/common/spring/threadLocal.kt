package xyz.nietongxue.common.spring

import org.springframework.beans.factory.config.CustomScopeConfigurer
import org.springframework.beans.factory.config.Scope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.SimpleThreadScope

@Configuration
open class ThreadLocalScopeConfig {

    @Bean
    open fun threadLocalScope(): Scope {
        return SimpleThreadScope()
    }

    @Bean
    open fun customScopeConfigurer(threadLocalScope: Scope): CustomScopeConfigurer {
        return CustomScopeConfigurer().apply {
            addScope("threadLocal", threadLocalScope)
        }
    }
}