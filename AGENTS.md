# AGENTS.md

本文件为代码库中的智能代理（agentic coding agents）提供指导，确保所有自动化任务遵循一致的规范和最佳实践。

## 构建/测试命令

### 基本构建命令
- `mvn clean compile` - 编译整个项目
- `mvn clean test` - 运行所有测试
- `mvn clean install` - 编译并安装到本地仓库

### 单元测试运行
- `mvn test` - 运行所有测试
- `mvn test -Dtest=ClassName#methodName` - 运行指定测试类中的特定方法
- `mvn surefire:test -Dtest=ClassName` - 运行指定类的所有测试

### 模块级别操作
- `mvn clean compile -pl module-name` - 编译特定模块
- `mvn test -pl module-name` - 测试特定模块

## 代码风格指南

### 语言和框架
- 主要使用 Kotlin 语言编写
- 使用 Maven 作为构建工具
- 使用 JUnit 5 作为测试框架
- 使用 Jackson 库进行 JSON 处理

### 导入和组织
- 所有导入应按字母顺序排列
- 避免使用通配符导入（如 `import xyz.nietongxue.common.*`）
- 标准 Java/Kotlin 导入在前，第三方库导入在后

### 命名约定
- 类名使用 PascalCase（例如：`FilterTest`）
- 方法名使用 camelCase（例如：`toJsonStringForAny`）
- 常量使用 UPPER_SNAKE_CASE（例如：`MAX_RETRY_COUNT`）
- 包名使用全小写（例如：`xyz.nietongxue.common.json`）

### 类型注解和可空性
- 明确声明类型，避免使用 `any`
- 合理使用 Kotlin 的可空性和非空类型系统
- 对于函数参数和返回值，在必要时明确标注可空性

### 注释和文档
- 使用 Kotlin 的多行注释 `/* */` 或单行注释 `//`
- 在公共API上使用 KDoc 风格的文档注释
- 代码中应包含必要的解释性注释，特别是复杂逻辑部分

### 错误处理
- 使用 Kotlin 的异常处理机制
- 尽可能使用 `try/catch` 块处理潜在错误
- 在适当的地方使用 `Result` 类型或 `Either` 类型来处理错误

### 文件结构
- 源代码位于 `src/main/kotlin` 目录下
- 测试代码位于 `src/test/kotlin` 目录下
- 每个模块都有独立的 `pom.xml` 配置文件

### 特殊标记
- 使用 `@JsonWithType` 注解表示需要类型信息的接口
- 使用 `@JvmStatic` 等注解实现与 Java 互操作性

## 代码审查和质量保证
- 所有代码提交必须通过 CI 流水线
- 代码需通过所有单元测试
- 代码覆盖率需达到最低要求
- 代码风格需符合项目规范（使用 ktlint 或类似工具）