package easy.config

import easy.io.JFile
import easy.util.Format
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
@Suppress("unused")
object Config {
	private var properties: Properties = Properties()

/*	private val CFG: Config = Config()
		val cfg = Config()
		load()
		cfg
	}*/
	init {
		load()
	}

	@JvmStatic
	@Throws(FileNotFoundException::class)
	fun load(filepath: String?) {
//		System.out.println(filepath);
		if (filepath!=null)
		{
			val pps = Properties()
			pps.load(Files.newInputStream(File(filepath).toPath()))
			val name = "CONFIGLOADCLASS"
			val loadclassname = pps.getProperty(name)
			if (loadclassname != null && loadclassname!="") {

				val cl = try{
					Class.forName(loadclassname).getConstructor().newInstance() as ConfigLoad
				}
				catch (e:Exception)
				{
//					e.printStackTrace()
					println("[$filepath][$name->$loadclassname][${e}]")
					null
				}
				properties = cl?.load(pps) ?: pps
			}
			else
			{
				properties = pps
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
						fpath = "$tpath/WEB-INF$cfgname"
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

	@JvmStatic
	fun load() {
		var cpath: String? = null
		var fpath: String? = null
		try {
			val u = javaClass.getResource("/")
			cpath = if(u === null) {
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
			println("Load config from [$fpath]")
			load(fpath)
		} catch (ex: Exception) {
			println("载入配置文件错误[$cpath]->[$fpath]")
			ex.printStackTrace()
		}
	}

	@JvmStatic
	fun getProperty(key: String?): String? {
		return properties.getProperty(key)
	}

	@JvmStatic
	fun getProperty(key: String?, defvalue: String?): String {
		return properties.getProperty(key, defvalue)
	}

	@JvmStatic
	@Suppress("unused")
	fun setProperty(key: String?, newvalue: String?) {
		properties.setProperty(key, newvalue)
	}


	@JvmStatic
	fun getProperties(): Properties {
		return properties
	}
}
