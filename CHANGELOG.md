# CHANGELOG

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
