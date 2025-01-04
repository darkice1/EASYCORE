package easy.sql.enums

enum class ConflictAction {
	UPDATE,      // 冲突时执行 DO UPDATE
	DO_NOTHING,  // 冲突时执行 DO NOTHING
}