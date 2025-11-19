package xyz.nietongxue.common.bpmn

import com.alibaba.compileflow.engine.ProcessEngine
import com.alibaba.compileflow.engine.ProcessEngineFactory
import com.alibaba.compileflow.engine.bpmn.definition.BpmnModel
import com.alibaba.compileflow.engine.core.extension.ExtensionInvoker
import com.alibaba.compileflow.engine.core.infrastructure.bean.SpringApplicationContextProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class EngineConfig {


    @Bean
    open fun provider(): SpringApplicationContextProvider? {
        return SpringApplicationContextProvider()
    }


    @Bean
    open fun processEngine(
        provider: SpringApplicationContextProvider,
        invoker: ExtensionInvoker
    ): ProcessEngine<BpmnModel> {
        val re: ProcessEngine<BpmnModel> = ProcessEngineFactory.createBpmn() as ProcessEngine<BpmnModel>
        return re
    }

    @Bean(name = ["MyAddBean"])
    open fun add(): MyAdd {
        return MyAdd()
    }
}