# EASYCORE 中文文档
# 项目概览
EASYCORE（`easy` 库）是一套面向 Servlet/JSP 应用的轻量级基础框架，核心目标是以最小代价提供 Action 调度、数据库访问、日志与常用工具封装，方便快速构建早期 Java Web 项目。当前源码兼容 JDK 21 与 Kotlin 1.9+，通过 Gradle 构建并发布。
## 架构亮点
- Commander Servlet + Action 抽象组成的 MVC 控制中枢，约定 `action` 参数动态定位业务类。
- `easy.servlet.Request/Response` 对原生 `HttpServletRequest/Response` 做封装，配合 Filter 统一编码处理。
- `easy.sql` 提供面向表格数据的 `DataSet/Row/Col`、批量操作、SQL 执行缓存，并默认集成 HikariCP 连接池。
- `easy.util`/`easy.io`/`easy.mail` 等包涵盖日志、日期、压缩、网络与邮件发送等辅助能力。
- 配置体系支持外部配置优先加载，并支持自定义 `ConfigLoad` 扩展与多数据源读写分离。
## 目录速览
- `build.gradle.kts`：Gradle Kotlin DSL 构建脚本，定义依赖、Java 工具链及发布任务。
- `src/main/java/easy`：框架核心源码。
- `src/main/resources`：放置框架所需资源文件（当前为空，可按需添加）。
- `web/WEB-INF`：示例 Web 应用配置，包含 `web.xml` 与 `config.example.txt`。
- `gradle*`：Gradle Wrapper 相关文件。
## 核心模块说明
- `easy.servlet`
	- `Commander`：入口 Servlet，按 `action` 参数反射创建 `Action` 子类，负责生命周期回调与异常处理。
	- `Request/Response`：对请求、响应对象封装，提供 Cookie、Session、上传等便捷方法。
	- `FileUpload` 等类提供多部分表单处理工具。
- `easy.action`
	- `Action` 抽象类：定义初始化、`Perform/afterPerform/send` 模板方法，以及重定向/转发、提示页处理。
	- `JsonAction`、`CommitAction`、`ListXmlAction` 等子类提供常见 JSON/XML 响应或数据库提交范式。
- `easy.sql`
	- `CPSql`/`Sql`：基于 HikariCP 的连接池实现，支持读写分离、连接泄漏检测与 `Config` 配置的池参数。
	- `DataSet`/`Row`/`Col`：以内存表模式封装 `ResultSet`，统一类型转换与字符集处理。
	- `BatchInsert`、`SelectDataSet`、`SqlExeCache` 等工具提升批量与高频查询性能。
- `easy.config`
	- `Config`：启动时优先加载外部配置路径，可通过 `CONFIGLOADCLASS` 指定自定义 `ConfigLoad` 实现对属性解密或动态注入。
- `easy.util`
	- `Log`：集中处理日志输出、文件持久化与邮件告警（依赖运行期配置文件中的 SMTP 配置）。
	- `Format`、`EDate`、`Algorithms`、`zip` 子包等提供字符串处理、时间工具及自研压缩实现。
- 其他扩展
	- `easy.mail`：SMTP 发送与 POP3 读取封装。
	- `easy.net`：HTTP 代理配置与验证。
	- `easy.io`：文件缓存、HTTP 客户端工具，为日志与下载等场景提供支持。
## 配置文件
- 运行期配置文件采用键值对格式，覆盖项目名称、编码设置、数据库连接、日志策略等。
- 加载优先级：JVM 参数 `-Deasycore.config=/path/config.txt`，环境变量 `EASYCORE_CONFIG=/path/config.txt`，最后才按旧逻辑向上查找本地 `config.txt` 或 `WEB-INF/config.txt`。
- 仓库提供 `web/WEB-INF/config.example.txt` 模板；运行期配置通过外部路径提供。
- 通过 `CONFIGLOADCLASS` 可以自定义类（例：`TestCnfLoad`）进行二次处理，例如字段加解密或动态注入。
- 数据库账号请使用最小权限专用用户，避免 `root`；生产数据库建议限制来源 IP，并关闭 JDBC `allowMultiQueries`。
- 与数据库连接池相关的键（如 `DBCONNECTMAX`、`DBMAXCONNECTIONLIFTIME`、`DBMINIMUMCONNECTIONCOUNT` 等）会在 `CPSql` 初始化时映射到 HikariCP 属性。
## Web 层配置
- `web/WEB-INF/web.xml` 中注册 `Commander` Servlet、字符编码过滤器 `SetCharacterEncodingFilter`，并配置 404 错误页与 JSP 属性。
- 通过 `ACTION_PACKAGE` 属性（示例：`taoke.actions.`）定义 `Action` 查找前缀，可根据项目自行调整。
## 构建与发布
- 构建工具：Gradle。常用命令：
	- `./gradlew build`：编译并运行测试。
	- `./gradlew publishAndCloseSonatype` / `release`：对接 Sonatype 进行发布。
- 工程已启用 Javadoc 与源码 Jar 打包，可直接发布到 Maven 中央仓库。
- 需要签名发布时，可在 `gradle.properties` 或环境变量中配置 `signingKey/signingPassword`。
## 开发流程示例
1. 新增业务 Action，位于 `ACTION_PACKAGE` 指定包内，实现 `Perform` 并设置 `url`、`message` 等。
2. 在 `Commander` 映射的 URL（如 `/c.do?action=DemoAction`）发起请求，由框架完成实例化与执行。
3. 在 `Perform` 内可通过 `Sql` 子类访问数据库，返回 `DataSet` 或直接写入 `Response`。
4. 当需要 JSON 响应时，可继承 `JsonAction` 并重写 `toJSON()`。
## 调试与日志
- `Log` 默认根据配置打印到控制台或写入 `LOG_PATH/SQL_PATH/ERROR_PATH` 指定文件。
- 可启用 `LOGSEND=true` 让异常日志通过邮件发送给维护人员。
- 建议结合 `ConfigLoad` 扩展处理运行期字段，并避免在日志中打印数据库口令、令牌和完整连接串。
## 测试
- 当前已包含外部配置路径加载测试，可继续结合 Kotlin `test` 框架补充单元测试。
- 针对数据库相关逻辑，建议使用测试容器或内存数据库，避免依赖生产配置。
## 兼容性与迁移建议
- 旧有 API 仍大量保留 Java 5 风格代码，建议逐步将新的模块迁移到 Kotlin 或现代 Java，并引入 Spring Boot 等生态以获得更好的可维护性。
- 若部署在 Servlet 3.0+ 容器，需要根据实际情况调整 `web.xml`，或迁移至注解式配置。
