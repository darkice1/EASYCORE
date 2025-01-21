@file:Suppress("unused")

package easy.util

import easy.io.JFile
import easy.servlet.PageInfo
import easy.sql.CPSql
import easy.sql.DataSet
import easy.sql.Row
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import net.sf.json.JsonConfig
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.mozilla.universalchardet.UniversalDetector
import java.io.*
import java.lang.reflect.Field
import java.math.BigInteger
import java.net.MalformedURLException
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.sql.SQLException
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.*
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import kotlin.math.max

/**
 *
 *
 *
 * 格式化处理
 *
 * @version 1.0 (*2005-8-17 Neo*)
 */
object Format {
	private var PINYINMAP: MutableMap<String, String>? = null

	private const val LOWSTRING = "abcdefghijklmnopqrstuvwxyz"
	private const val NUMLOWSTRING = "abcdefghijklmnopqrstuvwxyz1234567890"
	private const val ALLSTRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"

	private const val HEXSTRING = "01234567890abcdef"

	private const val HMAC_SHA1 = "HmacSHA1"

	private const val HMAC_SHA256 = "HmacSHA256"

	// private final static Pattern URLPAT
	// =Pattern.compile("(http://|https://)[^\\s]*");

	/**
	 * 转换成script输出使用字符串
	 *
	 * @param src
	 * @return
	 */
	@JvmStatic
	fun toScriptString(src: String?): String? {
		var tmp = src
		tmp = replaceAll(tmp, "\r|\n", "")
		tmp = replaceAll(tmp, "\"", "\\\\\"")
		tmp = replaceAll(tmp, "</script>", "\"+\"<\"+\"/script>\"+\"")
		tmp = replaceAll(tmp, "</SCRIPT>", "\"+\"<\"+\"/SCRIPT>\"+\"")

		return tmp
	}

	/**
	 * 字符串是否为null或者空串
	 * @param str
	 * @return
	 */
	@JvmStatic
	fun isEmpty(str: String?): Boolean {
		return str.isNullOrEmpty()
	}

	@JvmStatic
	fun replaceAll(source: String?, searchString: String, preplaceString: String?): String? {
		var replaceString = preplaceString
		if(source == null) {
			return null
		}

		if(source.isEmpty()) {
			return source
		}

		if(isEmpty(searchString)) {
			return source
		}

		if(replaceString == null) {
			replaceString = ""
		}
		val len = source.length
		val sl = searchString.length
		val rl = replaceString.length
		val length: Int
		if(sl == rl) {
			length = len
		} else {
			var c = 0
			var s = 0
			var e: Int
			while ((source.indexOf(searchString, s).also { e = it }) != -1) {
				c++
				s = e + sl
			}
			if(c == 0) {
				return source
			}
			length = len - (c * (sl - rl))
		}

		var s = 0
		var e = source.indexOf(searchString, s)
		if(e == -1) {
			return source
		}
		val sb = StringBuilder(length)
		while (e != -1) {
			sb.append(source, s, e)
			sb.append(replaceString)
			s = e + sl
			e = source.indexOf(searchString, s)
		}
		e = len
		sb.append(source, s, e)
		return sb.toString()
	}

	/**
	 * 转换成HTML输出使用字符串。取出&,",',<,>。
	 *
	 * @param src
	 * @return
	 */
	@JvmStatic
	fun toHTMLString(src: String?, isnoquotes: Boolean): String? {
		if(src == null) {
			return ""
		}
		var tmp = src
		tmp = replaceAll(tmp, "&", "&amp;")
		if(isnoquotes) {
			tmp = replaceAll(tmp, "\"", "&quot;")
			tmp = replaceAll(tmp, "'", "&#039;")
		}
		tmp = replaceAll(tmp, "<", "&lt;")
		tmp = replaceAll(tmp, ">", "&gt;")

		return tmp
	}

	/**
	 * 转换成HTML输出使用字符串。取出&,",',<,>。
	 *
	 * @param src
	 * @return
	 */
	@JvmStatic
	fun toHTMLString(src: String?): String? {
		return toHTMLString(src, false)
	}

	@JvmStatic
	fun toXMLString(ds: DataSet): String {
		val pi = PageInfo()
		pi.startIndex = 0
		pi.recordCount = ds.count
		pi.pageSize = ds.count

		pi.pageNumber = 1
		pi.totalPage = 1

		return toXMLString(ds, pi, -1)
	}

	@JvmOverloads
	@JvmStatic
	fun listToJsonString(list: List<Row>, addjson: JSONObject? = null): String {
		val json = JSONObject()

		val array = JSONArray()

		for (r in list) {
			val map = HashMap<String, String>()
			for (col in r.colsNameList) {
				map[col] = r.getString(col)
			}
			array.add(map)
		}

		if(addjson != null) { // Iterator<Entry<String, String>> paramsfields =
			// params.entrySet().iterator();

			val iter = addjson.keys()
			while (iter.hasNext()) {
				val key = iter.next() as String
				json[key] = addjson[key]
			}
		}
		json["total"] = list.size
		json["result"] = array

		return json.toString()
	}

	@JvmStatic
	fun toXMLString(ds: DataSet, pi: PageInfo, useTime: Long): String {
		val buf = StringBuilder()
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
		buf.append(
			String.format(
				"<rs count=\"%s\" pageSize=\"%s\" pageCount=\"%s\" pageNum=\"%s\" use_time=\"%s\">",
				pi.recordCount,
				pi.pageSize,
				pi.totalPage,
				pi.pageNumber,
				useTime
			             )
		          )

		var i = pi.startIndex
		var k = 1
		while (i < pi.recordCount && k <= pi.pageSize) {
			val r = ds.getRow(i)
			buf.append("<r")
			for (str in r.colsNameList) {
				buf.append(
					String.format(
						" %s=\"%s\"", toHTMLString(str, true), toHTMLString(r.getString(str), true)
					             )
				          )
			}
			buf.append("/>")
			i++
			k++
		}

		buf.append("</rs>")

		return buf.toString()
	}

	@Suppress("unused")
	@JvmStatic
	fun <E> toListString(array: Array<E>): String {
		return toListString(array, ",")
	}

	@JvmStatic
	fun <E> toListString(array: Array<E>, splitstr: String): String {
		val buf = StringBuilder()
		for (e in array) {
			buf.append(e)
			buf.append(splitstr)
		}
		val len = buf.length
		if(len > 0) {
			buf.setLength(len - splitstr.length)
		}

		return buf.toString()
	}

	@JvmStatic
	fun toListString(strs: Array<String?>): String {
		val list: List<String> = ArrayList(mutableListOf(*strs))

		return toListString(list, ",")
	}

	/**
	 * 返回list输出字符串，使用,分割
	 *
	 * @param list
	 * @return
	 */
	@Suppress("unused")
	fun toListString(list: List<*>): String {
		return toListString(list, ",")
	}

	/**
	 * 返回list输出字符串
	 *
	 * @param list
	 * 对应list
	 * @param splitstr
	 * 分割字符
	 * @return
	 */
	@JvmStatic
	fun toListString(list: List<*>, splitstr: String): String {
		val buf = StringBuilder()
		for (o in list) {
			buf.append(o.toString())
			buf.append(splitstr)
		}
		if(list.isNotEmpty()) {
			buf.setLength(buf.length - splitstr.length)
		}
		return buf.toString()
	}

	@JvmStatic
	fun getContent(str: String, start: String, end: String): String? {
		try {
			val si = str.indexOf(start)
			if(si < 0) {
				return null
			}
			val ssi = si + start.length
			val ei = str.indexOf(end, ssi)
			return str.substring(ssi, ei)
		} catch (e: Exception) {
			return null
		}
	}

	@JvmStatic
	fun replaceContent(
		str: String, start: String, end: String, newstring: String?
	                  ): String? {
		try {
			val si = str.indexOf(start)
			val ssi = si + start.length
			val ei = str.indexOf(end, ssi)

			return String.format(
				"%s%s%s", str.substring(0, ssi), newstring, str.substring(ei)
			                    )
		} catch (e: Exception) {
			return null
		}
	}

	private val pinYinMap: Map<String, String>?
		get() {
			if(PINYINMAP == null) {
				PINYINMAP = HashMap()

				// 1) 通过 getResourceAsStream，读取与 FORMAT 类同包下的 pinyin.txt
				//    如果 pinyin.txt 位于同包下，参数不用加前缀 “/”。
				//    如果位于资源根目录或其他路径，请参见后续说明。
				try {
					javaClass.getResourceAsStream("pinyin.txt").use { `is` ->
						if(`is` == null) {
							// 如果为 null，说明无法找到资源
							throw RuntimeException(
								"Cannot find resource pinyin.txt relative to " + javaClass.name
							                      )
						}
						// 2) 用你的自定义 JFile 类，从 InputStream 中读内容
						val file = JFile(`is`)

						// 3) 逐行读取，将前两个空格分割的字段放入 PINYINMAP
						val lines = file.lineList
						for (line in lines) {
							val parts = line.split(" ".toRegex(), limit = 2).toTypedArray()
							if(parts.size >= 2) {
								PINYINMAP!![parts[0]] = parts[1]
							}
						}
					}
				} catch (e: IOException) {
					// 如果需要处理流异常，可以在这里捕获
					throw RuntimeException("Error reading pinyin.txt resource", e)
				}
			}
			return PINYINMAP
		}

	/**
	 * 汉字转拼音
	 *
	 * @param str
	 * @return
	 */
	@JvmStatic
	fun getPinyin(str: String): String {
		val buf = StringBuilder()
		for (i in str.indices) {
			val k = str.substring(i, i + 1)
			val t = pinYinMap!![k]
			buf.append(Objects.requireNonNullElse(t, k))
		}

		return buf.toString()
	}

	/**
	 * 取得拼音首字母
	 *
	 * @param str
	 * @return
	 */
	@JvmStatic
	fun getFirstPinyin(str: String): String {
		val buf = StringBuilder()
		for (i in str.indices) {
			val k = str.substring(i, i + 1)
			val t = pinYinMap!![k]
			if(t == null) {
				buf.append(k)
			} else {
				buf.append(t, 0, 1)
			}
		}

		return buf.toString()
	}
	@JvmStatic
	fun getAllField(obj: Any, getsuper: Boolean): List<Field> {
		return getAllField(obj.javaClass, getsuper)
	}
	@JvmStatic
	fun getAllField(c: Class<*>, getsuper: Boolean): List<Field> {
		val list: MutableList<Field> = ArrayList()

		//		Class<?> c =  obj.getClass();
		if(getsuper) {
			val supername = c.superclass.name

			// System.out.println("@@@@@@@@@"+c.getName()+" "+supername);
			if("java.lang.Object" != supername) {
				list.addAll(getAllField(c.superclass, true))
			}
		}

		// System.out.println("#########"+c.getName());
		val fs = c.declaredFields
		for (f in fs) {
			// System.out.println(f+" "+f.getGenericType().getTypeName()+"
			// "+f.getName());
			// f.toString();
			if(!f.toString().contains(" transient ") && !f.genericType.typeName.contains("java.lang.Class.")) {
				list.add(f)
			}
		}

		return list
	}

	@Throws(ClassNotFoundException::class)
	@JvmStatic
	fun getAllField(classname: String?, getsuper: Boolean): List<Field> {
		val c = Class.forName(classname)

		return getAllField(c, getsuper)
	}

	/**
	 * 取得对象所有变量打印
	 *
	 * @param o
	 * @return
	 */
	@JvmOverloads
	@JvmStatic
	fun beanToString(o: Any, getsuper: Boolean = false): String {
		val jsonconfig = JsonConfig()
		jsonconfig.isAllowNonStringKeys = true

		val json = JSONObject.fromObject("{}", jsonconfig)
		val fields = getAllField(o, getsuper)

		for (f in fields) {
			// 判断该字段对当前实例是否可访问
			val wasAccessible = f.canAccess(o)
			// 如果不可访问，则尝试设置为可访问
			if (!wasAccessible) {
				// trySetAccessible() 在模块封装或安全限制下可能返回 false
				f.trySetAccessible()
			}

			try {
				// 读取字段值
				val po = f.get(o)

				if (po != null && (po.javaClass.isArray || po is List<*>)) {
					val arr = JSONArray.fromObject("[]", jsonconfig)
					if (po.javaClass.isArray) {
						arr.add(po)
					} else if (po is List<*>) {
						for (ppo in po) {
							arr.add(ppo.toString())
						}
					}
					json[f.name] = arr
				} else {
					json[f.name] = po
				}
			} catch (e: IllegalArgumentException) {
				// 根据需要处理
			} catch (e: IllegalAccessException) {
				e.printStackTrace()
			}

			// 如果一定需要恢复到原有可访问性，需根据自身逻辑再做处理
			// 但 Java 9+ 没有“撤销” trySetAccessible() 的内置办法，一般场景也不再必需恢复
		}

		return json.toString()
	}

	@JvmStatic
	fun getRequestUrl(req: HttpServletRequest): String {
		val buf = StringBuilder(req.serverName)
		buf.append("/")
		buf.append(req.servletPath)

		val q = req.queryString
		if(q != null) {
			buf.append("?").append(q)
		}

		return buf.toString()
	}

	/**
	 * 字符串相似值 Levenshtein Distance
	 *
	 * @param str1
	 * @param str2
	 * @return
	 */
	@JvmStatic
	fun ld(str1: String, str2: String): Int {
		val d: Array<IntArray> // 矩阵
		val n = str1.length
		val m = str2.length
		var j: Int // 遍历str2的
		var ch1: Char // str1的
		var ch2: Char // str2的
		var temp: Int // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if(n == 0) {
			return m
		}
		if(m == 0) {
			return n
		}
		d = Array(n + 1) { IntArray(m + 1) }
		var i = 0 // 遍历str1的
		while (i <= n) {
			// 初始化第一列
			d[i][0] = i
			i++
		}
		j = 0
		while (j <= m) {
			// 初始化第一行
			d[0][j] = j
			j++
		}
		i = 1
		while (i <= n) {
			// 遍历str1
			ch1 = str1[i - 1]
			// 去匹配str2
			j = 1
			while (j <= m) {
				ch2 = str2[j - 1]
				temp = if(ch1 == ch2) {
					0
				} else {
					1
				}
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(
					d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp
				             )
				j++
			}
			i++
		}
		return d[n][m]
	}

	/**
	 * 返回字符串相似百分比
	 *
	 * @param str1
	 * @param str2
	 * @return
	 */
	@JvmStatic
	fun sim(str1: String, str2: String): Double {
		val ld = ld(str1, str2)
		return 1 - ld.toDouble() / max(str1.length.toDouble(), str2.length.toDouble())
	}

	private fun min(one: Int, two: Int, three: Int): Int {
		var min = one
		if(two < min) {
			min = two
		}
		if(three < min) {
			min = three
		}
		return min
	}


	@Suppress("unused")
	@JvmStatic
	fun ip2long(ip: String?): Long {
		var num: Long = 0

		if(ip != null) {
			val ips = ip.split("[.]".toRegex(), limit = 4).toTypedArray()
			try {
				var i = 0
				val len = ips.size
				while (i < len) {
					val s = ips[i].trim { it <= ' ' }
					var l: Long = 0
					try {
						l = s.toLong()
					} catch (e: Exception) {
						// Log.OutException(e);
					}

					num += l shl ((3L - i) * 8).toInt()
					i++
				}

				/*
				 * num = 16777216L * Long.parseLong(ips[0]) + 65536L
				 * Long.parseLong(ips[1]) + 256 * Long.parseLong(ips[2]) +
				 * Long.parseLong(ips[3]);
				 */
			} catch (e: Exception) {
				Log.OutException(e, ip)
			}
		}

		return num
	}

	/**
	 * 整数转成ip地址.
	 *
	 * @param ipLong
	 * @return
	 */
	@Suppress("unused")
	@JvmStatic
	fun long2ip(ipLong: Long): String {
		// long ipLong = 1037591503;
		val mask = longArrayOf(0x000000FF, 0x0000FF00, 0x00FF0000, -0x1000000)
		var num: Long
		val ipInfo = StringBuilder()
		for (i in 0..3) {
			num = (ipLong and mask[i]) shr (i * 8)
			if(i > 0) ipInfo.insert(0, ".")
			ipInfo.insert(0, num.toString(10))
		}
		return ipInfo.toString()
	}

	/**
	 * 返回随机字符串 只有小写字母与数字
	 *
	 * @param num
	 * @return
	 */
	@JvmStatic
	fun getRandStringNum(num: Int): String {
		val buf = StringBuilder()
		val len = NUMLOWSTRING.length
		// LOWSTRING
		for (i in 0..<num) {
			val pos = ThreadLocalRandom.current().nextInt(len)
			buf.append(NUMLOWSTRING, pos, pos + 1)
		}
		return buf.toString()
	}

	@JvmStatic
	fun getRandHex(num: Int): String {
		val buf = StringBuilder()
		val len = HEXSTRING.length
		// LOWSTRING
		for (i in 0..<num) {
			val pos = (Math.random() * len).toInt()
			buf.append(HEXSTRING, pos, pos + 1)
		}
		return buf.toString()
	}

	/**
	 * 返回随机字符串 只有小写字母
	 *
	 * @param num
	 * 生成字母数量
	 * @return
	 */
	@JvmStatic
	fun getRandString(num: Int): String {
		val buf = StringBuilder()
		val len = LOWSTRING.length
		// LOWSTRING
		for (i in 0..<num) {
			val pos = ThreadLocalRandom.current().nextInt(len)
			buf.append(LOWSTRING, pos, pos + 1)
		}
		return buf.toString()
	}

	/**
	 * 返回随机字符串 大小写与数字
	 *
	 * @param num
	 * 生成字母数量
	 * @return
	 */
	@JvmStatic
	fun getRandAllString(num: Int): String {
		val buf = StringBuilder()
		val len = ALLSTRING.length
		// LOWSTRING
		for (i in 0..<num) {
			val pos = ThreadLocalRandom.current().nextInt(len)
			buf.append(ALLSTRING, pos, pos + 1)
		}
		return buf.toString()
	}


	@Suppress("unused")
	@JvmStatic
	fun getDecoderChartset(pstr: String): String {
		// utf8中汉字是 %E4%B8%80 到 %E9%BE%A5 gbk中汉字是 %D2%BB 到 %FD%9B
		var str = pstr
		str = str.uppercase(Locale.getDefault())
		var charset = "UTF-8"

		val pattern = Pattern.compile("%[0-9A-F]{2}")
		val msc = pattern.matcher(str)
		out@ while (msc.find()) {
			val r = msc.group(0)
			val f = r.substring(1, 2)
			if("E" == f) {
				for (i in 0..1) {
					if(!msc.find()) {
						charset = "GBK"
						break@out
					}
				}
			} else {
				if("D" == f || "F" == f) {
					charset = "GBK"
					break
				}
			}
		}
		return charset
	}

	@JvmStatic
	fun sha256(str: String?): String {
//		return MessageDigest("sha-256", str);
		return DigestUtils.sha256Hex(str)
	}

	@JvmStatic
	fun sha1(str: String?): String {
//		return MessageDigest("sha-1", str);
		return DigestUtils.sha1Hex(str)
	}

	@JvmStatic
	fun md2(str: String?): String {
//		return MessageDigest("md2", str);
		return DigestUtils.md2Hex(str)
	}

	@Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
	@JvmStatic
	fun hmacSha1(key: ByteArray, data: ByteArray?): ByteArray {
		val signingKey = SecretKeySpec(key, HMAC_SHA1)
		val mac = Mac.getInstance(HMAC_SHA1)
		mac.init(signingKey)

		return mac.doFinal(data)
	}

	@Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
	@JvmStatic
	fun hmacSha1(key: String, data: String): ByteArray {
		return hmacSha1(key.toByteArray(), data.toByteArray())
	}

	@Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
	@JvmStatic
	fun hmacSha256(key: ByteArray, data: ByteArray?): ByteArray {
		val signingKey = SecretKeySpec(key, HMAC_SHA256)
		val mac = Mac.getInstance(HMAC_SHA256)
		mac.init(signingKey)

		return mac.doFinal(data)
	}

	@Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
	@JvmStatic
	fun hmacSha256(key: String, data: String): ByteArray {
		return hmacSha256(key.toByteArray(), data.toByteArray())
	}

	@Throws(IOException::class)
	@JvmStatic
	fun fileMd5(inputFile: String): String? {
		val file = File(inputFile)
		var value: String? = null
		val filein = FileInputStream(file)
		val byteBuffer = filein.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
		val md5: MessageDigest
		try {
			md5 = MessageDigest.getInstance("MD5")
			md5.update(byteBuffer)

			val bi = BigInteger(1, md5.digest())
			value = bi.toString(16)
		} catch (e: NoSuchAlgorithmException) {
			Log.OutException(e)
		}

		filein.close()

		return value
	}

	@JvmStatic
	fun md5(bytes: ByteArray?): ByteArray {
		return DigestUtils.md5(bytes)
	}

	@JvmStatic
	fun md5Str(bytes: ByteArray?): String {
//		return byte2hex(MessageDigest("md5", bytes));
		return DigestUtils.md5Hex(bytes)
	}

	@JvmStatic
	fun md5(str: String?): String {
//		return MessageDigest("md5", str);
		return DigestUtils.md5Hex(str)
	}

	@JvmStatic
	fun getFileExtName(filename: String): String {
		val idx = filename.lastIndexOf(".") + 1

		return filename.substring(idx)
	}

	@JvmStatic
	fun getNumber(str: String): Long {
		val number = "0123456789"
		val buf = StringBuilder("0")

		for (t in str.toCharArray()) {
			var i = 0
			val len = number.length
			while (i < len) {
				val nt = number[i]
				if(nt == t) {
					buf.append(t)
					break
				}
				i++
			}
		}

		return buf.toString().toLong()
	}

	/*	public static String MessageDigest(String m, String str)
	{
		String mstr;
		mstr = byte2hex(MessageDigest(m, str.getBytes()));

		return mstr;
	}*/
	@JvmStatic
	fun hex2byte(s: String): ByteArray {
		var s2: String
		val b = ByteArray(s.length / 2)
		var i = 0
		while (i < s.length / 2) {
			s2 = s.substring(i * 2, i * 2 + 2)
			b[i] = (s2.toInt(16) and 0xff).toByte()
			i++
		}
		return b
	}

	@JvmStatic
	fun byte2hex(b: ByteArray): String // 二行制转字符串
	{
		val hexString = StringBuilder()
		for (value in b) {
			val hex = Integer.toHexString(0xff and value.toInt())
			if(hex.length == 1) {
				hexString.append('0')
			}
			hexString.append(hex)
		}
		return hexString.toString()
	}

	private val ENBASE64URL: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
	private val DEBASE64URL: Base64.Decoder = Base64.getUrlDecoder()

	private val ENBASE64: Base64.Encoder = Base64.getEncoder()
	private val DEBASE64: Base64.Decoder = Base64.getDecoder()

	@JvmStatic
	fun encodeBase64Url(buf: ByteArray?): String {
//		byte[] encoded = ENBASE64URL.encode(buf);
//
//		// BASE64Encoder en = new sun.misc.BASE64Encoder();
//		return new String(encoded);
		return ENBASE64URL.encodeToString(buf)
	}

	@JvmStatic
	fun decodeBase64Url(str: String?): ByteArray {
		// BASE64Decoder decoder = new BASE64Decoder();
		// return decoder.decodeBuffer(str);
		return DEBASE64URL.decode(str)
	}

	@JvmStatic
	fun encodeBase64(buf: ByteArray?): String {
		val encoded = ENBASE64.encode(buf)

		// BASE64Encoder en = new sun.misc.BASE64Encoder();
		return String(encoded)
	}

	@JvmStatic
	fun decodeBase64(str: String?): ByteArray {
		// BASE64Decoder decoder = new BASE64Decoder();
		// return decoder.decodeBuffer(str);
		return DEBASE64.decode(str)
	}

	@Throws(
		InvalidKeyException::class,
		NoSuchAlgorithmException::class,
		NoSuchPaddingException::class,
		InvalidKeySpecException::class
	       )
	@JvmStatic
	fun encodeDes(mykey: String, encryptedString: String): String? {
		val keyAsBytes = mykey.toByteArray(StandardCharsets.UTF_8)
		val myKeySpec = DESKeySpec(keyAsBytes)
		val mySecretKeyFactory = SecretKeyFactory.getInstance("DES")
		val key = mySecretKeyFactory.generateSecret(myKeySpec)

		val cipher = Cipher.getInstance("DES/ecb/pkcs5padding")

		var encryptedText: String? = null
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key)
			val plainText = cipher.doFinal(encryptedString.toByteArray())

			encryptedText = Base64UrlSafe.encodeBase64(plainText)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return encryptedText
	}

	@Throws(
		InvalidKeyException::class,
		NoSuchAlgorithmException::class,
		NoSuchPaddingException::class,
		InvalidKeySpecException::class
	       )
	@JvmStatic
	fun decodeDes(mykey: String, encryptedString: String): String? {
		val keyAsBytes = mykey.toByteArray(StandardCharsets.UTF_8)
		val myKeySpec: KeySpec = DESKeySpec(keyAsBytes)
		val mySecretKeyFactory = SecretKeyFactory.getInstance("DES")
		val cipher = Cipher.getInstance("DES/ecb/pkcs5padding")
		val key = mySecretKeyFactory.generateSecret(myKeySpec)

		var decryptedText: String? = null
		try {
			cipher.init(Cipher.DECRYPT_MODE, key)
			val encryptedText = Base64UrlSafe.decodeBase64(encryptedString)
			val plainText = cipher.doFinal(encryptedText)
			decryptedText = String(plainText)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return decryptedText
	}

	/**
	 * 返回svm字符串
	 *
	 * @param type
	 * 类别字段
	 * @param fields
	 * 特征字段（,分割）
	 * @return
	 */
	@JvmStatic
	fun sqlToSvm(sqlstr: String?, type: String?, fields: String): String {
		val buf = StringBuilder()

		val fs = fields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		var fi = 0
		val flen = fs.size
		while (fi < flen) {
			fs[fi] = fs[fi].trim { it <= ' ' }
			fi++
		}

		val sql = CPSql()
		try {
			val ds = sql.executeQuery(sqlstr)
			while (ds.next()) {
				// buf.append(String.format("", arg1));
				buf.append(ds.getFloat(type))
				var i = 0
				val len = fs.size
				while (i < len) {
					buf.append(
						String.format(
							" %d:%f", i + 1, ds.getDouble(fs[i])
						             )
					          )
					i++
				}
				buf.append("\n")
			}
		} catch (e: SQLException) {
			Log.OutException(e)
		}
		sql.close()

		return buf.toString()
	}

	@JvmStatic
	fun getUrls(str: String): List<String> {
		// final Pattern URLPAT =
		// Pattern.compile("(http(|s)://[-a-zA-Z0-9@:%_\\+.~,#?&//=]+)");
		val pattern = Pattern.compile(
			"((http(|s):)?//[-a-zA-Z0-9@:%_" + "+.~,#?&/=]+)"
		                             )

		val list: MutableList<String> = LinkedList()
		val matcher = pattern.matcher(str)
		while (matcher.find()) {
			var url = matcher.group()
			if(url.indexOf("//") == 0) {
				url = "https:$url"
			}
			list.add(url)
		}
		return list
	}

	// public static void main(String[] args)
	// {
	// System.out.println(getUrls("<div id=\"4d703977f553755f624215ea80180c89\"
	// style=\"width:300px;height:250px;\"><script type=\"text/javascript\"
	// src=\"http://adx.haoad.org/p/4d703977f553755f624215ea80180c89.js\"></script></div>"));
	// System.out.println(getUrls("<script type=\"text/javascript\"
	// smua=\"d=p&s=b&u=u3501065&w=300&h=250\"
	// src=\"//www.nkscdn.com/smu0/o.js\"></\"></script>"));
	//
	// }
	@JvmStatic
	fun getAts(str: String?): List<String> {
		val list: MutableList<String> = LinkedList()

		if(str != null) {
			val pattern = Pattern.compile(
				String.format(
					"@[[^@\\s%s]0-9]{1,20}",
					"`~!@#\\$%\\^&*()=+\\[\\]{}\\|/\\?<>,\\.:\\u00D7\\u00B7\\u2014-\\u2026\\u3001-\\u3011\\uFE30-\\uFFE5"
				             )
			                             )
			val matcher = pattern.matcher(str)
			while (matcher.find()) {
				list.add(matcher.group())
			}
		}

		return list
	}

	@JvmStatic
	fun getMapString(map: Map<*, *>): String {
		val buf = StringBuilder()

		for ((key1, value) in map) {
			val key = key1.toString()
			val `val` = value.toString()

			buf.append(String.format("[%s]:[%s]\n", key, `val`))
		}

		return buf.toString()
	}

	private val myIpAll: Map<String, String?>
		get() {
			val all: MutableMap<String, String?> = HashMap()

			try {
				val content = JFile.loadHttpFile(
					"http://iframe.ip138.com/ic.asp", null, null, "gbk", "http://ip138.com/"
				                                )
				val ip = getContent(content, "[", "]")
				val area = getContent(content, "来自：", "</center>")
				all["ip"] = ip
				all["area"] = area
			} catch (e: IOException) {
				Log.OutException(e)
			}

			return all
		}

	/**
	 * 通过字符串返回key
	 * @param key
	 * @return
	 */
	@JvmStatic
	fun getKey(key: String): String {
		if(key.length >= 32) {
			return md5(key)
		}
		return key
	}

	val myIp: String?
		get() = myIpAll["ip"]

	@JvmStatic
	fun getDomain(urlstr: String): String {
		try {
			val url = URI(urlstr)
			return url.host
		} catch (ignored: MalformedURLException) {
		}
		return ""
	}

	@JvmStatic
	fun getChartset(bytes: ByteArray): String {
		val code: String

		val detector = UniversalDetector(null)
		detector.handleData(bytes, 0, bytes.size)
		detector.dataEnd()
		code = detector.detectedCharset
		detector.reset()
/*		if(code == null) {
			code = "utf-8"
		}   */
		/*
		 * if (bytes == null || bytes.length < 2) { return code; }
		 *
		 * int p = ((int) bytes[0] & 0x00ff) << 8 | ((int) bytes[1] & 0x00ff);
		 * switch (p) { case 0xefbb: code = "UTF-8"; break; case 0xfffe: code =
		 * "Unicode"; break; case 0xfeff: code = "UTF-16BE"; break; default:
		 * code = "GBK"; }
		 */
		return code
	}

	@JvmStatic
	fun getStringStream(sInputString: String?): InputStream? {
		if(sInputString != null) {
			return ByteArrayInputStream(
				sInputString.toByteArray()
			                           )
		}
		return null
	}

	@JvmStatic
	fun getStringLen(str: String?, len: Int): String? {
		if(str == null || str.length <= len) {
			return str
		}
		return str.substring(0, len)
	}


	@JvmStatic
	fun decompressStr(str: String?): String {
		var dstr = ""

		try {
			if(!str.isNullOrEmpty()) {
				val bytes = decodeBase64Url(str)

				val bis = ByteArrayInputStream(bytes)
				val gis = GZIPInputStream(bis)
				dstr = String(IOUtils.toByteArray(gis), StandardCharsets.UTF_8)
			}
		} catch (e: Exception) {
			Log.OutException(e)
		}


		return dstr
	}

	@JvmStatic
	fun compressStr(str: String?): String {
		var cstr = ""
		try {
			if(!str.isNullOrEmpty()) {
				val out = ByteArrayOutputStream()
				val gzip = GZIPOutputStream(out)
				gzip.write(str.toByteArray())
				gzip.close()
				cstr = encodeBase64Url(out.toByteArray())
			}
		} catch (e: Exception) {
			Log.OutException(e)
		}
		return cstr
	}

	/**
	 * 将毫秒数转换为可读格式
	 * @param durationMillis 持续时间（毫秒）
	 * @return 可读格式的持续时间
	 */
	@JvmStatic
	fun tsToHumanString(durationMillis: Long): String {
		val days = TimeUnit.MILLISECONDS.toDays(durationMillis)
		val hours = TimeUnit.MILLISECONDS.toHours(durationMillis) % 24
		val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
		val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
		val millis = durationMillis % 1000

		return buildString {
			if (days > 0) append("${days}天")
			if (hours > 0) append("${hours}小时")
			if (minutes > 0) append("${minutes}分")
			if (seconds > 0 || (days == 0L && hours == 0L && minutes == 0L)) append("${seconds}秒")
			if (millis > 0) append("${millis}毫秒")
		}
	}
	/*	public static void main(String[] args)
	{
		String test = "aaabbcc";
		System.out.println(Format.Md2(test));
		System.out.println(Format.byte2hex(Format.Md5(test.getBytes())));
		System.out.println(Format.Md5(test));
		System.out.println(Format.Md5Str(Format.Md5(test.getBytes())));
		System.out.println(Format.Sha1(test));
		System.out.println(Format.Sha256(test));

	}*/
}