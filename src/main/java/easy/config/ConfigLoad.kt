package easy.config

import java.util.*

abstract class ConfigLoad {
	abstract fun load(srcpps: Properties): Properties
}