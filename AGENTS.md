# Repository Guidelines（仓库贡献指南）

## 沟通与代理约定
- 与本仓库相关的“你—我”交互默认全程使用中文（包括 Issue、PR、评审与讨论）。
- 代码注释与文档建议优先使用中文，必要时可附英文对照。

## 项目结构与模块组织
- `src/main/java`：核心库（Java/Kotlin），包名以 `easy.*` 开头。
- `src/main/resources`：运行期资源（按需添加）。
- `src/test/java`：测试与示例（Kotlin/Java），含 `*Test.kt` 与 `Test*.kt` 形式。
- `web/WEB-INF`：示例 Web 配置（`web.xml`、`config.txt`）。
- `build.gradle.kts`：Gradle Kotlin DSL，Java 21 工具链与发布任务。
- `gradle/`、`gradlew*`：Gradle Wrapper，所有命令使用 Wrapper。

## 构建、测试与开发命令
- `./gradlew build`：编译并运行测试，产物位于 `build/`。
- `./gradlew test`：仅执行单元测试。
- `./gradlew publishToMavenLocal`：发布到本地 Maven 仓库。
- `./gradlew publishAndCloseSonatype`：发布到 Sonatype 并关闭 staging。
- `./gradlew release`：发布并关闭+释放 staging（OSSRH）。

## 编码风格与命名规范
- 语言：Java + Kotlin 混合；新增内容优先使用 Kotlin。
- 缩进：4 空格；文件编码 UTF-8。
- 命名：类/对象用 `UpperCamelCase`，方法/字段用 `lowerCamelCase`，资源文件可用 `lower.snake`。
- 包结构：置于 `easy.<module>`（如 `easy.sql`、`easy.util`）。每文件一个公开类型，文件名与类型一致。

## 测试规范
- 框架：Kotlin test（`testImplementation(kotlin("test"))`）。
- 位置：`src/test/java`；命名采用 `*Test.kt` 或 `Test*.kt`。
- 运行：`./gradlew test`。测试应快速、可重复，外部依赖请 Mock。
- 含 `main` 的可执行示例与单元测试区分存放。

## 提交与 Pull Request 规范
- 提交信息尽量遵循 Conventional Commits（如 `feat:`、`fix:`、`refactor(sql):`、`docs:`），用祈使句，范围明确。
- PR 应包含变更目的、关联 Issue、变更前后行为；涉及 Web/UI 的提供截图；标注配置/迁移步骤。
- 合并前确保 `./gradlew build` 全绿，并为新增功能补充相应测试。

## 安全与配置提示
- 禁止提交任何密钥与凭据。通过 `web/WEB-INF/config.txt` 与自定义 `CONFIGLOADCLASS` 管理配置。
- 发布所需 `signingKey`/`signingPassword` 与 OSSRH 账号请放入 `gradle.properties` 或环境变量。
