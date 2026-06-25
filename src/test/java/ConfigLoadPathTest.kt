import easy.config.Config
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import java.nio.file.Files

class ConfigLoadPathTest {
	@Test
	fun loadUsesJvmPropertyConfigPath() {
		val configFile = Files.createTempFile("easycore-config", ".txt")
		val previous = System.getProperty("easycore.config")

		try {
			configFile.writeText(
				"""
				PROJECT=external-test
				DBUSER=your_db_user
				DBPASSWORD=your_db_password
				""".trimIndent()
			)

			System.setProperty("easycore.config", configFile.toString())
			Config.load()

			assertEquals("external-test", Config.getProperty("PROJECT"))
		} finally {
			if (previous == null) {
				System.clearProperty("easycore.config")
				Config.load(null)
			} else {
				System.setProperty("easycore.config", previous)
				Config.load(previous)
			}
			Files.deleteIfExists(configFile)
		}
	}
}
