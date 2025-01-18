import easy.config.Config
import easy.config.ConfigLoad
import easy.util.Format
import java.util.*

class TestCnfLoad:ConfigLoad() {
	override fun load(srcpps: Properties): Properties {
		val pps = Properties()
		pps.setProperty("PROJECT", "test")

		return pps
	}

	companion object{
		@JvmStatic
		fun main(args: Array<String>) {
			val set = mutableSetOf<String>()
			set.add("CONFIGLOADCLASS")
			set.add("PROJECT")

			set.forEach { key ->
				println("$key: ${Config.getProperty(key)}")
			}

			println(Format.getPinyin("你好"))
		}
	}

}