package easy.sql

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseTableBigDecimalTest {
	@Test
	fun `big decimal value uses plain string`() {
		val table = BaseTable("account")
		table.Add("amount", BigDecimal("12345.6700"))

		val insertSql = table.insertString

		assertEquals("INSERT INTO account (amount) VALUES ('12345.6700')", insertSql)
	}

	@Test
	fun `big integer value keeps full precision`() {
		val table = BaseTable("account")
		table.Add("amount", BigInteger("12345678901234567890"))

		val insertSql = table.insertString

		assertEquals("INSERT INTO account (amount) VALUES ('12345678901234567890')", insertSql)
	}
}
