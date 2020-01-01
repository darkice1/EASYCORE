package easy.util;

import easy.config.Config;
import easy.io.JFile;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
	private Date date;
	private Calendar calendar= Calendar.getInstance ();
	private final static TimeZone TIMEZONE = TimeZone.getTimeZone(Config.getProperty("DEFTIMEZONE","GMT+8"));
	private final static int[] MYWEEK = {7,1,2,3,4,5,6};
	
	static
	{
		TimeZone.setDefault(TIMEZONE);
	}
	
	private final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat LOGFORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	private final SimpleDateFormat SQLDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
//	@Override
//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		//rowList.clear();
//		date = null;
//		calendar = null;
//	}


	public EDate ()
	{
		date=new Date();
		calendar.setTime (date);
	}

	public EDate (Date pdate)
	{
		date=pdate;
		calendar.setTime (date);
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻鍑ゆ嫹yyyy-MM-dd HH:mm:ss
	 * @param str
	 */
	public EDate (String str)
	{
		 this (str, "yyyy-MM-dd HH:mm:ss");
	}

	public EDate (String datestr,String formatestr)
	{
		SimpleDateFormat df = new SimpleDateFormat(formatestr);
		try
		{
			date = df.parse(datestr);
			calendar.setTime (date);
		}
		catch (Exception e)
		{
			Log.OutException(e);
		}
	}
	public int getYear()
	{
		return calendar.get (Calendar.YEAR);
	}

	public void setYear(int year)
	{
		calendar.set (Calendar.YEAR,year);
		date = calendar.getTime ();
	}

	public int getMonth()
	{
		return calendar.get (Calendar.MONTH)+1;
	}

	public void setMonth(int month)
	{
		calendar.set (Calendar.MONTH,month-1);
		date = calendar.getTime ();
	}

	public int getDay()
	{
		return calendar.get (Calendar.DATE);
	}
	
	public int getWeekOfYear()
	{
		return calendar.get (Calendar.WEEK_OF_YEAR);
	}

	public void setDay(int day)
	{
		calendar.set (Calendar.DATE,day);
		date = calendar.getTime ();
	}

	public int getHour()
	{
		return calendar.get (Calendar.HOUR_OF_DAY);
	}

	public void setHour(int hour)
	{
		calendar.set (Calendar.HOUR_OF_DAY,hour);
		date = calendar.getTime ();
	}

	public int getMin()
	{
		return calendar.get (Calendar.MINUTE);
	}

	public void setMin(int min)
	{
		calendar.set (Calendar.MINUTE,min);
		date = calendar.getTime ();
	}

	public int getSec()
	{
		return calendar.get (Calendar.SECOND);
	}

	public void setSec(int sec)
	{
		calendar.set (Calendar.SECOND,sec);
		date = calendar.getTime ();
	}

	public void setDate(Date pdate)
	{
		date=pdate;
		calendar.setTime (date);
	}

	public Date getcalendarDate()
	{
		return calendar.getTime ();
	}

	public Date getDate()
	{
		return date;
	}
	
	public static String getString()
	{
		return new EDate().DATEFORMAT.format (new Date());
	}

	public static String toString (Date p_date)
	{
		return new EDate().DATEFORMAT.format (p_date);
	}
	
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹SQL浣块敓鐭潻鎷峰紡yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static String getSQLDate (Date date)
	{
		return new EDate().SQLDATEFORMAT.format (date);
	}
	
	public String getSQLDate ()
	{
		return SQLDATEFORMAT.format (date);
	}
	
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹LOG浣块敓鐭潻鎷峰紡yyyy_MM_dd_HH_mm_ss
	 * @param date
	 * @return
	 */
	public static String getLogDate(Date date)
	{
		return new EDate().LOGFORMAT.format(date);
	}
	
	public static String getLogDate()
	{
		return new EDate().LOGFORMAT.format(new Date());
	}

	@Override
	public String toString ()
	{
		return DATEFORMAT.format (date);
	}

	public Date NextDay ()
	{
		Date pdate=new Date();
		pdate.setTime(date.getTime ()+86400000);
		return pdate;
	}
	public Date LastDay ()
	{
		Date pdate=new Date();
		pdate.setTime(date.getTime ()-86400000);
		return pdate;
	}

	public long getTime()
	{
		return date.getTime();
	}
	
	public int getWeek()
	{
		return MYWEEK[calendar.get(Calendar.DAY_OF_WEEK)-1];
	}
	
	public boolean equals (Date pdate)
	{
		return date.equals (pdate);
	}
	
	public static String toString(final Date date,final String format)
	{
		 SimpleDateFormat myFmt=new SimpleDateFormat(format);
		 return myFmt.format(date);
	}

	public void setTime(long time)
	{
		date.setTime(time);
		calendar.setTime (date);
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
			JSONObject json = JSONObject.fromObject(html);
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

	public static void main(String[] args)
	{
		System.out.println(isWorkday(new EDate("2020-01-01 00:00:00").getDate()));
	}
}