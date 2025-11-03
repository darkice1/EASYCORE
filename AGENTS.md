# Repository Guidelines（仓库贡献指南）

## 沟通与代理约定
- 与本仓库相关的“你—我”交互默认全程使用中文（包括 Issue、PR、评审与讨论）。
- 代码注释与文档建议优先使用中文，必要时可附英文对照。
- 禁止任何代理或脚本自动执行提交、推送等 Git 操作，所有提交需经人工确认。

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

## 修改清单维护
- 在仓库根目录维护统一的修改清单（文件名为 `CHANGELOG.md`），用于记录重大代码或文档变更。
- 每次执行代码或文档改动（含本地调试）前后，必须更新 `CHANGELOG.md`，按时间倒序追加当日条目，确保记录完整。
- 条目格式需包含日期（YYYY-MM-DD）、修改人及主要变更摘要，可选补充关联 Issue 或 PR；修改人需使用当前 `git config user.name`。
- 日期/修改人与变更内容请分行展示，保持信息清晰。
- 同一日期同一修改人若有多项更新，需汇总在同一条目中，并将变更摘要分条列出，保持排版整洁。

## 安全与配置提示
- 禁止提交任何密钥与凭据。通过 `web/WEB-INF/config.txt` 与自定义 `CONFIGLOADCLASS` 管理配置。
- 发布所需 `signingKey`/`signingPassword` 与 OSSRH 账号请放入 `gradle.properties` 或环境变量。
