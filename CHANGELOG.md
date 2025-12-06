# CHANGELOG

- 2025-12-06
  修改人: Neo
  变更摘要:
  - 新增 PushDeerClient Kotlin 工具类，使用官方 POST 表单接口，支持 text/desp/type 等参数与多 key 推送，不内置密钥。
  - 补充 PushDeerClient 测试，覆盖参数编码、重试成功与耗尽场景。
  - 默认重试间隔调整为 100ms，保证短时间快速重试体验。
  - pushMessage/pushText 改为返回布尔值，按 PushDeer code 与 result.success 判定成功，同时新增非零 code 失败重试的测试。
  - PushDeer 请求构建阶段同步裁剪 pushkey 空白并校验非空，避免带空格的 key 生成异常体。
  - BaseTable 的 INSERT/REPLACE 语句在表名后补充空格，输出 SQL 更贴合常规格式。

- 2025-11-21
  修改人: Neo
  变更摘要:
  - DataSet 时间字符串输出增加时区偏移，默认格式包含时区信息。
  - EDate 解析支持含时区日期字符串，兼容旧格式。
  - 修复 DataSet 处理 TIMESTAMP 时调用不存在的 setTime，改用属性赋值避免编译错误。
  - EDate 日期比较改用 isEqual，避免对 LocalDateTime 的身份敏感操作警告。

- 2025-11-03
  修改人: Neo
  变更摘要:
  - 更新 BaseTable 数值处理以支持 BigDecimal/BigInteger 并复用统一 SQL 转义逻辑。
  - 新增 `BaseTableBigDecimalTest` 验证 BigDecimal/BigInteger 插入语句。
  - 补充 `AGENTS.md` 修改清单维护规范并初始化 `CHANGELOG.md`。
