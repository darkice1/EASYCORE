import easy.sql.BaseTable
import easy.sql.enums.ConflictAction

object TestBaseTable {
	@JvmStatic
	fun main(args: Array<String>) {
		val kbt = BaseTable("keyword")
		kbt.Add("keyword", "aaa")
		kbt.Add("num", "1")
		kbt.AddPro("ts", "now()")

		val strsql = kbt.getInsertUpdateOnConflict(
			keys = "keyword",
			conflictAction = ConflictAction.UPDATE,
			"ts" to { _ -> "now()" },
			"num" to { fieldName -> "keyword.$fieldName + 1" })
		println(strsql)
	}
}