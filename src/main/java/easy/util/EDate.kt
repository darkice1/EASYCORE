package easy.util

import easy.config.Config.getProperty
import easy.io.JFile
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.WeekFields
import java.util.*

/**
 *
 * *Copyright: 9esoft.com (c) 2005-2006<br></br>
 * Company: 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹钀嶉敓鏂ゆ嫹閿熺Ц鐧告嫹閿熸枻鎷疯柊閿熷壙锟�i>*
 *
 * 閿熺殕璁规嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻妭鎺ュ尅鎷�
 *
 * @version 1.0 (*2006-3-17 Neo*)
 */
@Suppress("unused")
class EDate {
	private var localDateTime: LocalDateTime

	constructor() {
		localDateTime = LocalDateTime.now(TIMEZONE)
	}

	constructor(ts: Long) {
		localDateTime = LocalDateTime.ofInstant(Date(ts).toInstant(), TIMEZONE)
	}

	constructor(pdate: Date) {
		localDateTime = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime()
	}


	@Suppress("unused")
	constructor(str: String) {
		localDateTime = parseDateTime(str)
	}
	@Suppress("unused")
	constructor(datestr: String, formatestr: String) {
		val df = DateTimeFormatter.ofPattern(formatestr).withZone(TIMEZONE)
		localDateTime = parseDateTime(datestr, df)
	}

	var year: Int
		get() = localDateTime.year
		set(year) {
			localDateTime = localDateTime.withYear(year)
		}

	var month: Int
		get() = localDateTime.monthValue
		set(month) {
			localDateTime = localDateTime.withMonth(month)
		}

	var day: Int
		get() = localDateTime.dayOfMonth
		set(day) {
			localDateTime = localDateTime.withDayOfMonth(day)
		}


	val weekOfYear: Int
		get() {
			val weekFields = WeekFields.of(Locale.getDefault())
			return localDateTime.get(weekFields.weekOfWeekBasedYear())
		}


	var hour: Int
		get() = localDateTime.hour
		set(hour) {
			localDateTime = localDateTime.withHour(hour)
		}

	var min: Int
		get() = localDateTime.minute
		set(min) {
			localDateTime = localDateTime.withMinute(min)
		}

	var sec: Int
		get() = localDateTime.second
		set(sec) {
			localDateTime = localDateTime.withSecond(sec)
		}

	var date: Date
		get() = Date.from(localDateTime.atZone(TIMEZONE).toInstant())
		set(pdate) {
			localDateTime = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime()
		}

	val sqlDate: String
		get() = SQLDATEFORMAT.format(localDateTime)


	override fun toString(): String {
		return DATEFORMAT.format(localDateTime.atZone(TIMEZONE))
	}

	fun nextDay(): Date {
		val nextDay = localDateTime.plusDays(1)
		return Date.from(nextDay.atZone(TIMEZONE).toInstant())
	}

	fun lastDay(): Date {
		val lastDay = localDateTime.minusDays(1)
		return Date.from(lastDay.atZone(TIMEZONE).toInstant())
	}

	var time: Long
		get() = localDateTime.atZone(TIMEZONE).toInstant().toEpochMilli()
		set(time) {
			val instant = Instant.ofEpochMilli(time)
			localDateTime = LocalDateTime.ofInstant(instant, TIMEZONE)
		}

	val week: Int
		get() =//		int[] myWeek = {7, 1, 2, 3, 4, 5, 6};
//		DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
//		return MYWEEK[dayOfWeek.getValue() - 1];
			localDateTime.dayOfWeek.value

	fun equals(pdate: Date): Boolean {
//		localDateTime = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime();
		val target = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime()
		return localDateTime.isEqual(target)
	}

	private fun parseDateTime(text: String): LocalDateTime {
		var parsed = tryParseOffset(text, DATEFORMAT)
		if(parsed != null) {
			return parsed
		}

		parsed = tryParseOffset(text, DATEFORMAT_SHORT_OFFSET)
		if(parsed != null) {
			return parsed
		}

		return LocalDateTime.parse(text, DATEFORMAT_NOTZ)
	}

	private fun parseDateTime(text: String, formatter: DateTimeFormatter): LocalDateTime {
		val parsed = tryParseOffset(text, formatter)
		if(parsed != null) {
			return parsed
		}

		return LocalDateTime.parse(text, formatter)
	}

	private fun tryParseOffset(text: String, formatter: DateTimeFormatter): LocalDateTime? {
		try {
			val odt = OffsetDateTime.parse(text, formatter)
			return odt.atZoneSameInstant(TIMEZONE).toLocalDateTime()
		} catch (_: DateTimeParseException) {
			return null
		}
	} /*public static void main(String[] args)
	{
		EDate d = new EDate();
		System.out.println(d);
		System.out.println(d.getWeek());
		EDate n = new EDate(d.nextDay());
		System.out.println(n);
		EDate tn = new EDate(n.getTime());

		System.out.println(n +" "+ tn);
		System.out.println(getLogDate());
		System.out.println(getLogDate());
		System.out.println(isWorkday(new Date()));

		EDate strd = new EDate("2023-05-08 01:12:34");
		System.out.println(strd);
		System.out.println(strd.getWeek());
		System.out.println(new EDate().getTime()+"  "+ System.currentTimeMillis());

		System.out.println(strd.getWeekOfYear());
		System.out.println(strd.getYear());
		System.out.println(strd.getMonth());
		System.out.println(strd.getDay());
		System.out.println(strd.getHour());
		System.out.println(strd.getMin());
		System.out.println(strd.getSec());
	}*/

	companion object {
		private val TIMEZONE: ZoneId = ZoneId.of(getProperty("DEFTIMEZONE", "GMT+8"))

		//	private final static int[] MYWEEK = {7,1,2,3,4,5,6};
		private const val DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss"
		private val DATEFORMAT: DateTimeFormatter =
			DateTimeFormatterBuilder().appendPattern(DEFAULT_PATTERN).appendOffset("+HH:MM", "+00:00").toFormatter()
		private val DATEFORMAT_NOTZ: DateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN)
		private val DATEFORMAT_SHORT_OFFSET: DateTimeFormatter =
			DateTimeFormatterBuilder().appendPattern(DEFAULT_PATTERN).appendOffset("+HH", "+00").toFormatter()
		private val LOGFORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
		private val SQLDATEFORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

		@JvmStatic
		val string: String
			get() = EDate().toString()

		@JvmStatic
		fun toString(pDate: Date): String {
			return EDate(pDate).toString()
		}

		@JvmStatic
		fun getSQLDate(date: Date): String {
			return SQLDATEFORMAT.format(date.toInstant().atZone(TIMEZONE).toLocalDateTime())
		}

		@JvmStatic
		fun getSQLDate(): String {
			return SQLDATEFORMAT.format(EDate().localDateTime)
		}

		@JvmStatic
		fun getLogDate(date: Date): String {
			return EDate(date).sqlDate
		}

		@Suppress("unused")
		val logDate: String
			get() = LOGFORMAT.format(EDate().localDateTime)

		fun toString(date: Date?, format: String): String {
			val myFmt = SimpleDateFormat(format)
			return myFmt.format(date)
		}

		@Suppress("unused")
		@JvmStatic
		fun isWorkday(d: Date): Boolean {
//		https://holiday-api.leanapp.cn/api/v1/work?date=2020-1-1
//		工作日对应结果为 0, 休息日对应结果为 1, 节假日对应的结果为 2；
//		https://tool.bitefu.net/jiari/?d=2020-01-02&apikey=YOUR_API_KEY
//		http://api.goseek.cn/Tools/holiday?date=20200101
			/*		boolean isok = true;
		String dstr = EDate.toString(d,"yyyyMMdd");
		String url = String.format("http://api.goseek.cn/Tools/holiday?date=%s",dstr);
		try
		{
			String html = JFile.loadHttpFile(url);
			JSONObject json = JSONObject.fromObject(html);
			if (json.getInt("data")!=0)
			{
				isok = false;
			}
			*//* System.out.println(html); */ /*
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}*/

			var isok = true
			val url = String.format("https://holiday-api.leanapp.cn/api/v1/work?date=%s", getSQLDate(d))
			try {
				val html = JFile.loadHttpFile(url)
				val json = JSONObject(html)
				//			System.out.println(html);
				if("N" == json.getJSONObject("data").getString("shouldWork")) {
					isok = false
				}
				//* System.out.println(html); *//*
			} catch (e: IOException) {
				Log.OutException(e)
			}

			return isok
		}
	}
}
