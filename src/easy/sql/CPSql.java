package easy.sql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import easy.config.Config;
import easy.util.Format;
import easy.util.Log;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * 连接池操作��
 *
 * @version 1.0 (<i>2005-7-5 neo</i>)
 */

public class CPSql extends Sql
{
	/**
	 * 别名
	 */
	protected String alias;
	protected final String POOLCLASS = "org.logicalcobwebs.proxool.ProxoolDriver";
	
	protected String poolurl;
	protected String dbclass;
	
	protected boolean usepool = true;

	/**
	 * @see easy.sql.Sql#init()
	 */
	protected void init()
	{
		user = Config.getProperty("DBUSER");
		password = Config.getProperty("DBPASSWORD");
		jdbcurl = Config.getProperty("DBURL");
		dbclass = Config.getProperty("DBCLASS");
		
		usepool = "true".equals(Config.getProperty("USEDBPPOOL","true"));
	}
	
//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		alias = null;
//		poolurl = null;
//		dbclass = null;
//	}

	/**
	 * @see easy.sql.Sql#initdb()
	 */
	protected void initdb()
	{
		alias = Config.getProperty("PROJECT")+Format.Md5(String.format("%s-%s-%s-%s", user,password,dbclass,jdbcurl));
		poolurl = "proxool." +alias+":";

		info = new Properties();
		info.setProperty("proxool.maximum-connection-count", Config.getProperty("DBCONNECTMAX","20"));
		//info.setProperty("proxool.house-keeping-test-sql", "select current_date from dual");
		info.setProperty("proxool.house-keeping-test-sql", "select 1");
		info.setProperty("proxool.maximum-active-time",  Config.getProperty("DBMAXACTIVETIME","60000"));
		
		info.setProperty("proxool.maximum-connection-lifetime",  Config.getProperty("DBMAXCONNECTIONLIFTIME","60000"));
		info.setProperty("proxool.house-keeping-sleep-time",  Config.getProperty("DBMAXKEEPINGSLEEPTIME","30000"));
		info.setProperty("proxool.minimum-connection-count",  Config.getProperty("DBMINIMUMCONNECTIONCOUNT","1"));
		info.setProperty("proxool.simultaneous-build-throttle",  Config.getProperty("SIMULTANEOUSBUILDTHROTTLE","10"));
		info.setProperty("proxool.test-before-use",  "true");
		//info.setProperty("proxool.statistics-log-level", "ERROR");
		//info.setProperty("house-keeping-sleep-time", "30000");
		info.setProperty("user",user);
		info.setProperty("password",password);
		
		try
        {
			if (usepool)
			{
	        	Class.forName(POOLCLASS);
				conn = DriverManager.getConnection(poolurl+dbclass+":"+jdbcurl,info);
			}
			else
			{
	        	Class.forName(dbclass);
				conn = DriverManager.getConnection(jdbcurl,user,password);
			}
			stmt = conn.createStatement(resultSetType,resultSetConncurrency);
        }
        catch (SQLException ex)
        {
        	Log.OutException (ex,jdbcurl);
        }
        catch (Exception ex)
        {
        	Log.OutException (ex,jdbcurl);
        }
	}
	
	public CPSql()
	{
	}

	public CPSql (int resultSetType,int resultSetConncurrency)
	{
		super(resultSetType,resultSetConncurrency);
	}
}