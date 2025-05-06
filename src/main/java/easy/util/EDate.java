package easy.util;

import easy.config.Config;
import easy.io.JFile;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹钀嶉敓鏂ゆ嫹閿熺Ц鐧告嫹閿熸枻鎷疯柊閿熷壙锟�i></p>
 *
 * 閿熺殕璁规嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻妭鎺ュ尅鎷�
 *
 * @version 1.0 (<i>2006-3-17 Neo</i>)
 */

public class EDate
{
	private LocalDateTime localDateTime;
	private final static ZoneId TIMEZONE = ZoneId.of(Config.getProperty("DEFTIMEZONE", "GMT+8"));
//	private final static int[] MYWEEK = {7,1,2,3,4,5,6};

	private final static DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final static DateTimeFormatter LOGFORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
	private final static DateTimeFormatter SQLDATEFORMAT =  DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public EDate ()
	{
		localDateTime = LocalDateTime.now(TIMEZONE);
	}

	public EDate (long ts)
	{
		localDateTime = LocalDateTime.ofInstant(new Date(ts).toInstant(), TIMEZONE);
	}

	public EDate (Date pdate)
	{
		localDateTime = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime();
	}


	public EDate (String str)
	{
		 this (str, "yyyy-MM-dd HH:mm:ss");
	}

	public EDate (String datestr,String formatestr)
	{
		DateTimeFormatter df = DateTimeFormatter.ofPattern(formatestr).withZone(TIMEZONE);
		localDateTime = LocalDateTime.parse(datestr, df);
	}
	public int getYear()
	{
		return localDateTime.getYear();
	}

	public void setYear(int year)
	{
		localDateTime = localDateTime.withYear(year);
	}

	public int getMonth()
	{
		return localDateTime.getMonthValue();
	}

	public void setMonth(int month)
	{
		localDateTime = localDateTime.withMonth(month);
	}

	public int getDay()
	{
		return localDateTime.getDayOfMonth();
	}


	public int getWeekOfYear()
	{
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		return localDateTime.get(weekFields.weekOfWeekBasedYear());
	}

	public void setDay(int day)
	{
		localDateTime = localDateTime.withDayOfMonth(day);
	}

	public int getHour()
	{
		return localDateTime.getHour();
	}

	public void setHour(int hour)
	{
		localDateTime = localDateTime.withHour(hour);
	}

	public int getMin()
	{
		return localDateTime.getMinute();
	}

	public void setMin(int min)
	{
		localDateTime = localDateTime.withMinute(min);
	}

	public int getSec()
	{
		return localDateTime.getSecond();
	}

	public void setSec(int sec)
	{
		localDateTime = localDateTime.withSecond(sec);
	}

	public void setDate(Date pdate)
	{
		localDateTime = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime();
	}

	public Date getDate()
	{
		return Date.from(localDateTime.atZone(TIMEZONE).toInstant());
	}

	public static String getString()
	{
		return new EDate().toString();
	}

	public static String toString (Date p_date)
	{
		return new EDate(p_date).toString();
	}

	public static String getSQLDate (Date date)
	{
		return SQLDATEFORMAT.format (date.toInstant().atZone(TIMEZONE).toLocalDateTime());
	}

	public String getSQLDate()
	{
		return SQLDATEFORMAT.format(localDateTime);
	}


	public static String getLogDate(Date date)
	{
		return new EDate(date).getSQLDate();
	}

	public static String getLogDate()
	{
		return LOGFORMAT.format(new EDate().localDateTime);
	}

	@Override
	public String toString ()
	{
		return DATEFORMAT.format(localDateTime.atZone(TIMEZONE));
	}

	public Date nextDay ()
	{
		LocalDateTime nextDay = localDateTime.plusDays(1);
		return Date.from(nextDay.atZone(TIMEZONE).toInstant());
	}
	public Date lastDay ()
	{
		LocalDateTime lastDay = localDateTime.minusDays(1);
		return Date.from(lastDay.atZone(TIMEZONE).toInstant());
	}

	public long getTime()
	{
		return localDateTime.atZone(TIMEZONE).toInstant().toEpochMilli();
	}

	public int getWeek()
	{
//		int[] myWeek = {7, 1, 2, 3, 4, 5, 6};
//		DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
//		return MYWEEK[dayOfWeek.getValue() - 1];
		return localDateTime.getDayOfWeek().getValue();
	}

	public boolean equals (Date pdate)
	{
//		localDateTime = pdate.toInstant().atZone(TIMEZONE).toLocalDateTime();
		return localDateTime.equals (pdate.toInstant().atZone(TIMEZONE).toLocalDateTime());
	}

	public static String toString(final Date date,final String format)
	{
		 SimpleDateFormat myFmt=new SimpleDateFormat(format);
		 return myFmt.format(date);
	}

	public void setTime(long time)
	{
		Instant instant = Instant.ofEpochMilli(time);
		localDateTime = LocalDateTime.ofInstant(instant, TIMEZONE);
	}

	public static boolean isWorkday(Date d)
	{
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
			*//* System.out.println(html); *//*
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}*/

		boolean isok = true;
		String url = String.format("https://holiday-api.leanapp.cn/api/v1/work?date=%s",EDate.getSQLDate(d));
		try
		{
			String html = JFile.loadHttpFile(url);
			JSONObject json = new JSONObject(html);
//			System.out.println(html);
			if ("N".equals(json.getJSONObject("data").getString("shouldWork")))
			{
				isok = false;
			}
			//* System.out.println(html); *//*
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}

		return isok;
	}

	/*public static void main(String[] args)
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
}