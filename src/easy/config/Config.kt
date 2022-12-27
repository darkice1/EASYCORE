package easy.config

import easy.io.JFile
import easy.util.Format
import easy.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.util.*

/**
 *
 *
 * *Copyright: Easy (c) 2005-2005 <br></br>
 * Company: Easy *
 *
 *
 * Config������
 *
 * @version 1.0 ( *2005-7-4 neo *)
 */
class Config private constructor() {
	private var properties: Properties = Properties()

	companion object {
		private var CFG: Config? = null
		private val instance: Config?
			get() {
				if(CFG == null) {
					CFG = Config()
					load()
				}
				return CFG
			}

		@Throws(FileNotFoundException::class)
		fun load(filepath: String?) {
//		System.out.println(filepath);
			if (filepath!=null)
			{
				val pps = Properties()
				pps.load(Files.newInputStream(File(filepath).toPath()))
				val loadclassname = pps.getProperty("CONFIGLOADCLASS")
				if (loadclassname != null && loadclassname!="") {

					val cl = try{
						Class.forName(loadclassname).newInstance() as ConfigLoad
					}
					catch (e:Exception)
					{
						Log.OutException(e)
						null
					}
					if (cl != null) {
						CFG = instance
						CFG!!.properties = cl.load(pps)
					}
				}
				else
				{
					CFG = instance
					CFG!!.properties = pps
				}
			}
			else
			{
				println("Config.load:filepath is null")
			}
		}

		private fun getConfigPath(startpath: String?): String? {
			if (startpath != null)
			{
				val cfgname = "/config.txt"
				var fpath: String? = null
				var cf = File(startpath)
				while (true) {
					//			System.out.println(cf.getPath());
					val tpath = cf.path
					if(JFile.exists(tpath + cfgname)) {
						fpath = tpath + cfgname
						break
					}
					if(JFile.exists("$tpath/WEB-INF$cfgname")) {
						fpath = tpath + cfgname
						break
					}
					cf = if(tpath == "/") {
						break
					} else {
						File(cf.parent)
					}
				}
				return fpath
			}
			else
			{
				println("Config.getConfigPath:path is null")
				return null
			}
		}

		fun load() {
			var cpath: String? = null
			var fpath: String? = null
			try {
				CFG = Config()
				val u = CFG!!.javaClass.getResource("/")
				cpath = if(u == null) {
					System.getProperty("user.dir")
				} else {
					u.path
				}
				if(cpath.indexOf("file:") == 0) {
					cpath = cpath.substring(5)
				}
				cpath = Format.replaceAll(cpath, "%20", " ")

				//		System.out.println(String.format("载入配置文件错误[%s]",cpath));
				fpath = getConfigPath(cpath)
				load(fpath)
			} catch (ex: Exception) {
				System.out.printf("载入配置文件错误[%s]->[%s]%n", cpath, fpath)
				//			ex.printStackTrace();
			}
		}

		@JvmStatic
		fun getProperty(key: String?): String? {
			return instance!!.properties.getProperty(key)
		}

		@JvmStatic
		fun getProperty(key: String?, defvalue: String?): String? {
			return instance!!.properties.getProperty(key, defvalue)

		}

		@Suppress("unused")
		fun setProperty(key: String?, newvalue: String?) {
			instance!!.properties.setProperty(key, newvalue)
		}

		val string: String
			get() {
				if(CFG == null) {
					instance
				}
				return CFG!!.properties.toString()
			}
	}
}