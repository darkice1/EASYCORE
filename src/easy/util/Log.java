package easy.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import easy.config.Config;
import easy.io.JFile;
import easy.mail.SendTextMail;

/**
 * 
 * <p><i>Copyright: 9esoft.com (c) 2005-2005<br>
 *
 * LOG
 *
 * @version 1.0 (<i>2005-11-14 Neo</i>)
 */

public class Log
{
	protected final static SimpleDateFormat normalformat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
	protected final static SendTextMail sendmail = new SendTextMail(Config.getProperty("LOGSMTP"),Config.getProperty("LOGUSERNAME"),Config.getProperty("LOGPASSWORD")) ;
	protected final static String ERROR_REPORT = Config.getProperty("PROJECT")+" ERROR REPORT" ;
	protected final static String TAT = "\tat " ;
	protected final static String SEP = "\r\n" ;
	protected final static String CAUSE = "CAUSE:" ;
	protected final static String LINE = SEP+"------------------------------------------------------------------------------"+SEP;
	protected static long lasttime = 0;

	protected final static String LOG_PATH = Config.getProperty("ROOT_PATH","/") + Config.getProperty("LOG_PATH","log.txt");
	protected final static String SQL_PATH = Config.getProperty("ROOT_PATH","/") + Config.getProperty("SQL_PATH","sql.txt");
	protected final static String ERROR_PATH = Config.getProperty("ROOT_PATH","/") + Config.getProperty("ERROR_PATH","error.txt");

	protected final static JFile LOGFILE;
	protected final static JFile SQLFILE ;
	protected final static JFile ERRORFILE;

	protected final static boolean ISSEND = Config.getProperty("LOGSEND","false").equals("true")?true:false;
	
	protected final static boolean WRITELOG = Config.getProperty("WRITELOG","false").equals("true")?true:false;
	protected final static boolean WRITEERROR = Config.getProperty("WRITEERROR","false").equals("true")?true:false;
	protected final static boolean WRITESQL = Config.getProperty("WRITESQL","false").equals("true")?true:false;

	protected final static boolean OUTLOG = Config.getProperty("OUTLOG","true").equals("true")?true:false;
	protected final static boolean OUTERROR = Config.getProperty("OUTERROR","true").equals("true")?true:false;
	protected final static boolean OUTSQL = Config.getProperty("OUTSQL","false").equals("true")?true:false;
	static 
	{
		if (WRITELOG)
		{
			LOGFILE = new JFile (LOG_PATH,true);
		}
		else
		{
			LOGFILE=null;
		}
		if (WRITESQL)
		{
			SQLFILE = new JFile (SQL_PATH,true);
		}
		else
		{
			SQLFILE=null;
		}
		if (WRITEERROR)
		{
			ERRORFILE = new JFile (ERROR_PATH,true);
		}
		else
		{
			ERRORFILE=null;
		}
		sendmail.setFrom (Config.getProperty("LOGFROMUSER"));
		sendmail.setTo (Config.getProperty("LOGTOUSER"));
	}

	public static String OutException(Exception ex)
	{	
		return OutException(ex,null);
	}


	public static String OutException (Exception ex,String outstr)
	{
		String datestr = normalformat.format (new Date());
		
		StringBuffer buf = new StringBuffer (datestr);
		buf.append(LINE);
		
		StringBuffer strbuf = new StringBuffer();
		if (outstr != null)
		{		
			strbuf.append (outstr);
			strbuf.append(LINE);		
		}
		
		strbuf.append(ex.toString ());
		strbuf.append(SEP);
		strbuf.append(CAUSE);
		strbuf.append(ex.getCause ());
		StackTraceElement[] trace = ex.getStackTrace ();
        for (int i=0,len=trace.length; i < len; i++)
        {
        	strbuf.append(SEP);
        	strbuf.append(TAT);
        	strbuf.append(trace[i]);
        }
        strbuf.append(SEP);
        strbuf.append (LINE);
		
        buf.append(strbuf);
        if (WRITEERROR)
        {
        	ERRORFILE.WriteText(buf.toString());
        }
		
		if (ISSEND)
		{
			sendmail.setSubject (datestr + ERROR_REPORT);
			sendmail.setContent (buf.toString ());
			sendmail.send ();		
		}
		
		if (OUTERROR)
		{
			return OutLog(strbuf.toString());
		}
		else
		{
			return strbuf.toString();
		}
	}
	
	public static String OutLog (String formate,Object... strs)
	{
		String str = String.format(formate,strs);
		return OutLog (str);
	}
	
	public static String OutLog (String str)
	{
		String datestr = normalformat.format (new Date());
		
		String out = new String(String.format("%s%s%s", datestr,str,SEP));
		
		if (WRITELOG)
		{
			LOGFILE.WriteText(out);		
		}
		
		if (OUTLOG)
		{
			System.out.print(out);
		}
		
		return out;
	}
	
	public static String OutSql (String str)
	{
		String datestr = normalformat.format (new Date());
		if (WRITESQL)
		{
			SQLFILE.WriteText(String.format("%s%s%s", datestr,str,SEP));
		}
		
		if (OUTSQL)
		{
			return OutLog(str);
		}
		else
		{
			return "";
		}
	}
}