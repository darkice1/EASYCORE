package easy.sql

import easy.sql.enums.ConflictAction
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 *
 *
 * *Copyright: Easy (c) 2005-2005<br></br>
 * Company: Easy*
 *
 *
 * 表基础类
 *
 * @version 1.0 (*2005-7-5 neo*)
 */
@Suppress("unused")
class BaseTable {
	var tablename: String? = null

	private var viewname: String? = null

	var order: String? = "id DESC"

	private var fieldlist: String = "*"

	var where: String? = null

	@JvmField
	var params: MutableMap<String, String> = LinkedHashMap()

	@JvmField
	var proparams: MutableMap<String, String> = LinkedHashMap()

	constructor()

	@Suppress("unused")
	constructor(tableName: String?) {
		this.tablename = tableName
	}

	/**
	 * 初始化数据
	 *
	 * @param ds
	 * @return
	 */
	protected fun initdate(ds: DataSet): DataSet {
		return ds
	}

	/**
	 * 添加一般项
	 *
	 * @param field
	 * @param value
	 */
	fun Add(field: String, value: String) {
		params[field] = value
	}

	fun Add(field: String, value: Int) {
		setNumber(field, value)
	}

	fun Add(field: String, value: Long) {
		setNumber(field, value)
	}

	fun Add(field: String, value: Double) {
		setNumber(field, value)
	}

	fun Add(field: String, value: Float) {
		setNumber(field, value)
	}

	fun Add(field: String, value: BigInteger) {
		setNumber(field, value)
	}

	fun Add(field: String, value: BigDecimal) {
		setNumber(field, value)
	}

	/**
	 * 添加存储过程或运算
	 *
	 * @param field
	 * @param value
	 */
	fun AddPro(field: String, value: String) {
		proparams[field] = value
	}

	fun AddRow(r: Row) {
		for (k in r.colsNameList) {
			params[k] = r.getString(k)
		}
	}

	fun clear() {
		params.clear()
		proparams.clear()
	}

	@Throws(SQLException::class)
	fun InsertDelayed(): Int {
		val re = CPSql().use { sql->
			sql.executeUpdateEx(getInsertString(true, false))
		}

		return re
	}

	/**
	 * 插入数据
	 *
	 * @return -1
	 */
	@JvmOverloads
	@Throws(SQLException::class)
	fun Insert(isreturn: Boolean = false): Int {
		val sql = CPSql()
		sql.executeUpdateEx(insertString)
		if(isreturn) {
			val ds = sql.executeQuery(LASTSQL)
			var id = -1
			if(ds.next()) {
				id = ds.getInt("id")
			}
			sql.close()
			return id
		} else {
			sql.close()
			return -1
		}
	}

	val replaceString: String
		get() {
			val paramsfields: Iterator<Map.Entry<String, String>> = params.entries.iterator()
			val profields: Iterator<Map.Entry<String, String>> = proparams.entries.iterator()

			val sqlbuf = StringBuffer("REPLACE INTO ")

			sqlbuf.append(tablename)

			val fieldbuf = StringBuilder("(")
			val valuebuf = StringBuilder("(")

			// 一般属性
			while (paramsfields.hasNext()) {
				val entry = paramsfields.next()

				val pfield = entry.key

				fieldbuf.append(pfield)
				fieldbuf.append(',')

				valuebuf.append(doValue(params[pfield]))
				valuebuf.append(',')
			}
			// 存储过程等
			while (profields.hasNext()) {
				val entry = profields.next()
				val pfield = entry.key

				fieldbuf.append(pfield)
				fieldbuf.append(',')

				valuebuf.append(proparams[pfield])
				valuebuf.append(',')
			}
			fieldbuf.setCharAt(fieldbuf.length - 1, ')')
			valuebuf.setCharAt(valuebuf.length - 1, ')')

			sqlbuf.append(fieldbuf)
			sqlbuf.append(" VALUES ")
			sqlbuf.append(valuebuf)


			return sqlbuf.toString()
		}

	/**
	 *
	 * @param isdelayed 是否延迟
	 * @param isignore 是否忽略错误
	 * @return
	 */
	fun getInsertString(isdelayed: Boolean, isignore: Boolean): String {
		val paramsfields: Iterator<Map.Entry<String, String>> = params.entries.iterator()
		val profields: Iterator<Map.Entry<String, String>> = proparams.entries.iterator()

		var delayed = ""
		var ignore = ""
		if(isdelayed) {
			delayed = "DELAYED "
		}

		if(isignore) {
			ignore = "IGNORE "
		}

		val sqlbuf = StringBuffer(String.format("INSERT %s%sINTO ", delayed, ignore))

		sqlbuf.append(tablename)

		val fieldbuf = StringBuilder("(")
		val valuebuf = StringBuilder("(")

		// 一般属性
		while (paramsfields.hasNext()) {
			val entry = paramsfields.next()

			val field = entry.key

			fieldbuf.append(field)
			fieldbuf.append(',')

			valuebuf.append(doValue(params[field]))
			valuebuf.append(',')
		}
		// 存储过程等
		while (profields.hasNext()) {
			val entry = profields.next()
			val field = entry.key

			fieldbuf.append(field)
			fieldbuf.append(',')

			valuebuf.append(proparams[field])
			valuebuf.append(',')
		}
		fieldbuf.setCharAt(fieldbuf.length - 1, ')')
		valuebuf.setCharAt(valuebuf.length - 1, ')')

		sqlbuf.append(fieldbuf)
		sqlbuf.append(" VALUES ")
		sqlbuf.append(valuebuf)

		return sqlbuf.toString()
	}

	val insertString: String
		get() = getInsertString(false, false)

	/**
	 * 删除数据
	 *
	 * @param where
	 * 删除条件
	 * @throws SQLException
	 */
	@Throws(SQLException::class)
	fun delete(where: String) {
		val sql = CPSql()
		sql.executeUpdateEx(getdeleteString(where))
		sql.close()
	}

	fun getdeleteString(where: String): String {
		//String sql = sqlbuf.toString();
		//sqlbuf = null;

		return "delete from $tablename where $where"
	}

	/**
	 * 更新数据
	 *
	 * @param where
	 * @return
	 * @throws SQLException
	 */
	@Throws(SQLException::class)
	fun update(where: String?): Int {
		val sql = CPSql()
		val re = sql.executeUpdateEx(getUpdateString(where))
		sql.close()

		return re
	}

	@Throws(SQLException::class)
	fun update(format: String?, vararg strs: Any?): Int {
		return update(String.format(format!!, *strs))
	}

	fun getUpdateString(format: String?, vararg strs: Any?): String {
		return getUpdateString(String.format(format!!, *strs))
	}

	fun getUpdateString(where: String?): String {
		val paramsfields: Iterator<Map.Entry<String, String>> = params.entries.iterator()
		val profields: Iterator<Map.Entry<String, String>> = proparams.entries.iterator()

		val sqlbuf = StringBuilder("UPDATE ")
		sqlbuf.append(tablename)
		sqlbuf.append(" SET ")

		// 一般属性
		while (paramsfields.hasNext()) {
			val entry = paramsfields.next()
			val field = entry.key

			sqlbuf.append(field)
			sqlbuf.append('=')
			sqlbuf.append(doValue(params[field]))
			sqlbuf.append(',')
		}
		// 存储过程等
		while (profields.hasNext()) {
			val entry = profields.next()
			val field = entry.key

			sqlbuf.append(field)
			sqlbuf.append('=')
			sqlbuf.append(proparams[field])
			sqlbuf.append(',')
		}

		sqlbuf.setCharAt(sqlbuf.length - 1, ' ')

		sqlbuf.append("WHERE ")
		sqlbuf.append(where)


		//String sql = sqlbuf.toString();
		//sqlbuf = null;
		return sqlbuf.toString()
	}

	/**
	 * 更新字段按逗号分割
	 * @param fields
	 * @return
	 */
	fun getInsertUpdateOnDuplFields(fields: String): String {
		val fs = fields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		return getInsertUpdateOnDuplFields(fs)
	}

	/**
	 * 需要更新字段
	 * @param fields
	 * @return
	 */
	fun getInsertUpdateOnDuplFields(fields: Array<String>): String {
		val sqlbuf = StringBuilder()
		for (f in fields) {
			var v = params[f]
			if(v != null) {
				sqlbuf.append(f)
				sqlbuf.append('=')
				sqlbuf.append(doValue(params[f]))
				sqlbuf.append(',')
			} else {
				v = proparams[f]
				if(v != null) {
					sqlbuf.append(f)
					sqlbuf.append('=')
					sqlbuf.append(proparams[f])
					sqlbuf.append(',')
				}
			}
		}

		sqlbuf.setCharAt(sqlbuf.length - 1, ' ')

		return String.format("%s ON DUPLICATE KEY UPDATE %s", insertString, sqlbuf.toString())
	}

	val insertUpdateOnDuplAll: String
		/**
		 * 插入/更新所有字段
		 * @return
		 */
		get() = getInsertUpdateOnDuplAll(false)

	fun getInsertUpdateOnConflict(
		keys: String?,
		conflictAction: ConflictAction = ConflictAction.UPDATE,
		// 仍然是 (String) -> String, 可以在表达式中显式引用旧值 tableName.fieldName 或新值 EXCLUDED.fieldName
		vararg customUpdateHandlers: Pair<String, (String) -> String>
	                             ): String {
		// 将 vararg 转换为 Map
		val customHandlerMap = mapOf(*customUpdateHandlers)

		// 收集要 INSERT 的字段与值
		val columns = StringJoiner(", ")
		val values = StringJoiner(", ")

		// 生成 SET 子句的字符串
		val updateBuf = StringBuilder()

		// (1) 遍历 params (普通字段, 需要引号)
		for ((fieldName, fieldValue) in params) {
			columns.add(fieldName)
			// 对普通字段进行转义并加引号
			values.add(doValue(fieldValue))

			if (conflictAction == ConflictAction.UPDATE) {
				val handler = customHandlerMap[fieldName]
				if (handler != null) {
					// handler(fieldName) 只生成“右侧表达式”，例如 "keyword.num + 1" 或 "EXCLUDED.num + 1"
					// 左侧必须是裸列名 -> "$fieldName = ..."
					updateBuf.append("$fieldName = ${handler(fieldName)},")
				} else {
					// 默认用 EXCLUDED 覆盖 -> "fieldName = EXCLUDED.fieldName"
					updateBuf.append("$fieldName = EXCLUDED.$fieldName,")
				}
			}
		}

		// (2) 遍历 proparams (数据库函数或表达式, 不需要引号)
		for ((fieldName, fieldValue) in proparams) {
			columns.add(fieldName)
			// 直接拼接, 不加引号
			values.add(fieldValue)

			if (conflictAction == ConflictAction.UPDATE) {
				val handler = customHandlerMap[fieldName]
				if (handler != null) {
					updateBuf.append("$fieldName = ${handler(fieldName)},")
				} else {
					updateBuf.append("$fieldName = EXCLUDED.$fieldName,")
				}
			}
		}

		// 如果是 UPDATE，需要去掉最后一个逗号
		if (conflictAction == ConflictAction.UPDATE && updateBuf.isNotEmpty()) {
			updateBuf.setLength(updateBuf.length - 1) // 截掉最后一个逗号
		}

		// 构造冲突子句
		val conflictClause = when (conflictAction) {
			// 当需要引用旧值时，需要 FROM xxx WHERE xxx.key = EXCLUDED.key
			// 如果你确定逻辑一定用到旧值，就可以一直附加
			ConflictAction.UPDATE -> "DO UPDATE SET $updateBuf"
			ConflictAction.DO_NOTHING -> "DO NOTHING"
		}

		// 最终拼接SQL
		return "INSERT INTO $tablename (${columns}) VALUES (${values}) ON CONFLICT ($keys) $conflictClause"
	}


	fun getInsertUpdateOnDuplAll(isdelayed: Boolean): String {
		val paramsfields: Iterator<Map.Entry<String, String>> = params.entries.iterator()
		val profields: Iterator<Map.Entry<String, String>> = proparams.entries.iterator()

		val sqlbuf = StringBuilder()

		// 一般属性
		while (paramsfields.hasNext()) {
			val entry = paramsfields.next()
			val field = entry.key

			sqlbuf.append(field)
			sqlbuf.append('=')
			sqlbuf.append(doValue(params[field]))
			sqlbuf.append(',')
		}
		// 存储过程等
		while (profields.hasNext()) {
			val entry = profields.next()
			val field = entry.key

			sqlbuf.append(field)
			sqlbuf.append('=')
			sqlbuf.append(proparams[field])
			sqlbuf.append(',')
		}

		sqlbuf.setCharAt(sqlbuf.length - 1, ' ')

		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(isdelayed, false), sqlbuf.toString())
	}

	/**
	 *
	 * @param fields 后面需要操作字段
	 * @param ops 如果少写以最后一个为准
	 * @return
	 */
	fun getInsertUpdateOnDuplPro(fields: String, ops: String): String {
		return getInsertUpdateOnDuplPro(fields, ops, false)
	}

	fun getInsertUpdateOnDuplPro(fs: Array<String>, os: Array<String?>, isdelayed: Boolean): String {
		val buf = StringBuilder()

		val olen = os.size - 1
		var i = 0
		val len = fs.size
		while (i < len) {
			val f = fs[i]
			var fv = params[f]

			if(fv == null) {
				fv = proparams[f]
			}

			if(fv != null) {
				val o = if(i <= olen) {
					os[i]
				} else {
					os[olen]
				}
				buf.append(f)
				buf.append("=")
				buf.append(f)
				buf.append(o)
				buf.append(fv)
				buf.append(",")
			}
			i++
		}

		buf.setLength(buf.length - 1)
		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(isdelayed, false), buf.toString())
	}

	fun getInsertUpdateOnDuplPro(fields: String, ops: String, isdelayed: Boolean): String {
		val fs = fields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		val os: Array<String?> = ops.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

		return getInsertUpdateOnDuplPro(fs, os, isdelayed)
	}

	fun getInsertUpdateOnDupl(updateString: String?): String {
		//sql.executeUpdate(String.format("%s ON DUPLICATE KEY UPDATE %s", r.toString(),updateString));	
		return String.format("%s ON DUPLICATE KEY UPDATE %s", insertString, updateString)
	}

	fun getInsertUpdateOnDupl(formate: String?, vararg strs: Any?): String {
		val sql = String.format(formate!!, *strs)
		return getInsertUpdateOnDupl(sql)
	}

	@Throws(SQLException::class)
	fun InsertUpdateOnDupl(formate: String?, vararg strs: Any?) {
		val sql = String.format(formate!!, *strs)
		InsertUpdateOnDupl(sql)
	}

	@Throws(SQLException::class)
	fun InsertUpdateOnDupl(updateString: String?) {
		val sql = CPSql()
		sql.executeUpdateEx(getInsertUpdateOnDupl(updateString))
		sql.close()
	}

	@Throws(SQLException::class)
	fun select(where: String?, startidx: Int, count: Int): DataSet {
		val sqlbuf = StringBuilder("SELECT ")
		val cbuf = StringBuilder("SELECT COUNT(*) C FROM ")

		sqlbuf.append(fieldlist)
		sqlbuf.append(" FROM ")
		if(viewname == null) {
			sqlbuf.append(tablename)
			cbuf.append(tablename)
		} else {
			if(!viewname!!.contains(" ")) {
				sqlbuf.append(String.format("%s", viewname))
				cbuf.append(String.format("%s", viewname))
			} else {
				sqlbuf.append(String.format("(%s) t", viewname))
				cbuf.append(String.format("(%s) t", viewname))
			}
		}

		if(where != null && where != "") {
			sqlbuf.append(" WHERE ")
			sqlbuf.append(where)

			cbuf.append(" WHERE ")
			cbuf.append(where)
		}

		if(order != null && order != "") {
			sqlbuf.append(" ORDER BY ")
			sqlbuf.append(order)
		}

		if(startidx >= 0) {
			sqlbuf.append(" LIMIT ")
			sqlbuf.append(startidx)

			if(count >= 0) {
				sqlbuf.append(",")
				sqlbuf.append(count)
			}
		}
		// System.out.println(sqlbuf);
		/*
		 * ORACLE SELECT * FROM ( SELECT A.*, rownum r FROM ( SELECT * FROM
		 * Articlesn ORDER BY PubTime DESC ) A WHERE rownum <= PageUpperBound )
		 * B WHERE r > PageLowerBound;
		 */
		val sql = CPSql()

		val cds = sql.executeQuery(cbuf.toString())
		val ds = sql.executeQuery(sqlbuf.toString())

		if(cds.next()) {
			ds.count = cds.getInt("C")
		}
		sql.close()

		initdate(ds)
		return ds
	}

	/**
	 * 是否有信息
	 */
	fun hasinfo(): Boolean {
		return params.isNotEmpty() || proparams.isNotEmpty()
	}

	private fun setNumber(field: String, number: Number) {
		params[field] = number.toSqlLiteral()
	}

	private fun Number.toSqlLiteral(): String {
		return when (this) {
			is BigDecimal -> this.toPlainString()
			else -> this.toString()
		}
	}

	companion object {
		private const val LASTSQL: String = "SELECT LAST_INSERT_ID() id"

		private fun quoteSql(value: String?): String {
			if(value.isNullOrEmpty()) {
				return "''"
			}
			return "'" + value.replace("'", "''") + "'"
		}

		@JvmStatic
		fun doValue(pvalue: String?): String {
			return quoteSql(pvalue)
		}

		@JvmStatic
		fun getPgsqlCreateTableSQL(connection: Connection, schemaName: String, tableName: String): String {
			val createTableSQL = StringBuilder()

			// 获取表定义
			val tableDefSQL = """
            SELECT 'CREATE TABLE ' || quote_ident(n.nspname) || '.' || quote_ident(c.relname) || E'\n(\n' ||
                   string_agg(
                       '    ' || format(
                           '%I %s%s%s%s%s%s',
                           a.attname,
                           pg_catalog.format_type(a.atttypid, a.atttypmod),

                           /* ── Identity columns ───────────────────────────── */
                           CASE WHEN a.attidentity <> '' THEN
                               ' GENERATED ' || CASE a.attidentity WHEN 'd' THEN 'BY DEFAULT' ELSE 'ALWAYS' END || ' AS IDENTITY'
                           ELSE '' END,

                           /* ── Generated / virtual columns ───────────────── */
                           CASE WHEN a.attgenerated <> '' THEN
                               ' GENERATED ALWAYS AS (' || pg_get_expr(ad.adbin, ad.adrelid) || ') STORED'
                           ELSE '' END,

                           /* ── Ordinary DEFAULT expressions ───────────────── */
                           CASE WHEN ad.adbin IS NOT NULL AND a.attgenerated = '' THEN
                               ' DEFAULT ' || pg_get_expr(ad.adbin, ad.adrelid)
                           ELSE '' END,

                           /* ── NOT NULL constraint (IDENTITY already NOT NULL) */
                           CASE WHEN a.attnotnull AND a.attidentity = '' THEN
                               ' NOT NULL'
                           ELSE '' END,

                           /* ── Explicit COLLATE if differs from type collation */
                           CASE WHEN a.attcollation <> t.typcollation THEN
                               ' COLLATE ' || quote_ident(coll.collname)
                           ELSE '' END
                       ),
                       E',\n' ORDER BY a.attnum
                   ) || E'\n);\n'
            FROM pg_catalog.pg_class      c
            JOIN pg_catalog.pg_namespace  n   ON n.oid  = c.relnamespace
            JOIN pg_catalog.pg_attribute  a   ON a.attrelid = c.oid
            JOIN pg_catalog.pg_type       t   ON t.oid  = a.atttypid
            LEFT JOIN pg_catalog.pg_collation coll ON coll.oid = a.attcollation
            LEFT JOIN pg_catalog.pg_attrdef ad ON ad.adrelid = c.oid
                                               AND ad.adnum  = a.attnum
            WHERE a.attnum > 0
              AND NOT a.attisdropped
              AND c.relkind = 'r'
              AND n.nspname = '$schemaName'
              AND c.relname = '$tableName'
            GROUP BY n.nspname, c.relname;
        """.trimIndent()

			connection.createStatement().use { statement ->
				val resultSet = statement.executeQuery(tableDefSQL)
				if (resultSet.next()) {
					createTableSQL.append(resultSet.getString(1)).append("\n")
				}
			}

			// 获取主键定义
			val pkDefSQL = """
            SELECT 'ALTER TABLE ' || quote_ident(n.nspname) || '.' || quote_ident(t.relname) || ' ADD CONSTRAINT ' || quote_ident(c.conname) || ' PRIMARY KEY (' ||
            array_to_string(array_agg(quote_ident(a.attname)), ', ') || ');'
            FROM pg_constraint c
            JOIN pg_class t ON c.conrelid = t.oid
            JOIN pg_attribute a ON a.attnum = ANY (c.conkey) AND a.attrelid = t.oid
            JOIN pg_namespace n ON n.oid = t.relnamespace
            WHERE c.contype = 'p'
            AND n.nspname = '$schemaName'
            AND t.relname = '$tableName'
            GROUP BY n.nspname, t.relname, c.conname;
        """.trimIndent()

			connection.createStatement().use { statement ->
				val resultSet = statement.executeQuery(pkDefSQL)
				if (resultSet.next()) {
					createTableSQL.append(resultSet.getString(1)).append("\n")
				}
			}

			// 获取索引定义，排除主键索引
			val indexDefSQL = """
            SELECT indexname, indexdef
            FROM pg_indexes
            WHERE schemaname = '$schemaName' AND tablename = '$tableName'
            AND indexname NOT IN (
                SELECT conname
                FROM pg_constraint
                WHERE conrelid = (SELECT c.oid FROM pg_class c JOIN pg_namespace n ON c.relnamespace = n.oid WHERE c.relname = '$tableName' AND n.nspname = '$schemaName')
                AND contype = 'p'
            );
        """.trimIndent()

			connection.createStatement().use { statement ->
				val resultSet = statement.executeQuery(indexDefSQL)
				while (resultSet.next()) {
					createTableSQL.append(resultSet.getString("indexdef")).append(";\n")
				}
			}

			// 获取外键定义
			val fkDefSQL = """
            SELECT conname, pg_catalog.pg_get_constraintdef(c.oid, true) || ';'
            FROM pg_constraint c
            JOIN pg_class t ON c.conrelid = t.oid
            JOIN pg_namespace n ON n.oid = t.relnamespace
            WHERE n.nspname = '$schemaName' AND t.relname = '$tableName'
            AND c.contype = 'f';
        """.trimIndent()

			connection.createStatement().use { statement ->
				val resultSet = statement.executeQuery(fkDefSQL)
				while (resultSet.next()) {
					createTableSQL.append("ALTER TABLE $schemaName.$tableName ADD CONSTRAINT ${resultSet.getString(1)} ${resultSet.getString(2)}\n")
				}
			}

			// 获取表注释
			val tableCommentSQL = """
            SELECT obj_description(c.oid, 'pg_class')
            FROM pg_class c
            JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = '$schemaName' AND c.relname = '$tableName';
        """.trimIndent()

			connection.createStatement().use { statement ->
				val resultSet = statement.executeQuery(tableCommentSQL)
				if (resultSet.next()) {
					val comment = resultSet.getString(1)
					if (comment != null) {
						createTableSQL.append("COMMENT ON TABLE $schemaName.$tableName IS '$comment';\n")
					}
				}
			}

			// 获取列注释
			val columnCommentSQL = """
            SELECT a.attname, pg_catalog.col_description(a.attrelid, a.attnum)
            FROM pg_catalog.pg_attribute a
            JOIN pg_catalog.pg_class c ON a.attrelid = c.oid
            JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
            WHERE n.nspname = '$schemaName' AND c.relname = '$tableName'
            AND a.attnum > 0 AND NOT a.attisdropped;
        """.trimIndent()

			connection.createStatement().use { statement ->
				val resultSet = statement.executeQuery(columnCommentSQL)
				while (resultSet.next()) {
					val columnName = resultSet.getString(1)
					val comment = resultSet.getString(2)
					if (comment != null) {
						createTableSQL.append("COMMENT ON COLUMN $schemaName.$tableName.$columnName IS '$comment';\n")
					}
				}
			}

			return createTableSQL.toString()
		}

		@JvmStatic
		fun getPgsqlBackupDatabaseSQL(connection: Connection):String{
			val metaData = connection.metaData
			val tables: ResultSet = metaData.getTables(null, null, "%", arrayOf("TABLE"))

			val sb = StringBuilder()
			while (tables.next()) {
				val schemaName = tables.getString("TABLE_SCHEM")
				val tableName = tables.getString("TABLE_NAME")
				val createTableSQL = getPgsqlCreateTableSQL(connection, schemaName, tableName)
				sb.append(createTableSQL)
			}
			return sb.toString()

		}

		@JvmStatic
		fun getPgsqlBackupDatabaseSQLToFile(connection: Connection, outputFile: String) {
			val schema = getPgsqlBackupDatabaseSQL(connection)
			File(outputFile).writeText(schema)
		}

		@JvmStatic
		fun generateCreateDatabaseSQL(database: String): String {
			return "CREATE DATABASE IF NOT EXISTS `$database`;"
		}

		@JvmStatic
		fun getAllTables(connection: Connection, database: String): List<String> {
			val tables = mutableListOf<String>()
			val statement = connection.createStatement()
			statement.execute("USE $database")
			val resultSet = statement.executeQuery("SHOW TABLES")
			while (resultSet.next()) {
				tables.add(resultSet.getString(1))
			}
			resultSet.close()
			statement.close()
			return tables
		}

		@JvmStatic
		fun getMysqlCreateTableSQL(connection: Connection, database: String, table: String): String {
			val statement = connection.createStatement()
			statement.execute("USE $database")
			val resultSet = statement.executeQuery("SHOW CREATE TABLE `$table`")
			resultSet.next()
			// 使用正则表达式移除 AUTO_INCREMENT 部分
			val createTableSQL = resultSet.getString(2).replace(Regex("AUTO_INCREMENT=\\d+"), "")
			resultSet.close()
			statement.close()

			return "$createTableSQL;\n\n"
		}

		@JvmStatic
		fun getMysqlBackupDatabaseSQL(connection: Connection, database: String = connection.catalog!!): String {
			val stringBuilder = StringBuilder()
			stringBuilder.append(generateCreateDatabaseSQL(database)).append("\n")
			val tables = getAllTables(connection, database)

			for (table in tables) {
				val createTableSQL = getMysqlCreateTableSQL(connection, database, table)
				stringBuilder.append(createTableSQL)
			}
			return stringBuilder.toString()
		}

		@JvmStatic
		fun getMysqlBackupDatabaseSQLToFile(connection: Connection, outputFile: String, database: String = connection.catalog!!) {
			val schema = getMysqlBackupDatabaseSQL(connection, database)
			File(outputFile).writeText(schema)
		}
	}

}
