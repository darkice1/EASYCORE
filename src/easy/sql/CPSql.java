package easy.sql;

import easy.config.Config;
import easy.util.Format;
import easy.util.Log;

import java.sql.DriverManager;
import java.util.Properties;

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
	//	protected String alias;
	protected final String POOLCLASS = "org.logicalcobwebs.proxool.ProxoolDriver";

	//	protected String poolurl;
	protected String dbclass;
	protected String dbclasswrite;

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


		userwrite = Config.getProperty("DBUSERWRITE");
		passwordwrite = Config.getProperty("DBPASSWORDWRITE");
		jdbcurlwrite = Config.getProperty("DBURLWRITE");
		dbclasswrite = Config.getProperty("DBCLASSWRITE");
	}


	private Properties getProperties()
	{
		Properties info = new Properties();
		info.setProperty("proxool.maximum-connection-count", Config.getProperty("DBCONNECTMAX","20"));
		//info.setProperty("proxool.house-keeping-test-sql", "select current_date from dual");
		info.setProperty("proxool.house-keeping-test-sql", "select 1");
		info.setProperty("proxool.maximum-active-time",  Config.getProperty("DBMAXACTIVETIME","70000"));

		info.setProperty("proxool.maximum-connection-lifetime",  Config.getProperty("DBMAXCONNECTIONLIFTIME","120000"));
		info.setProperty("proxool.house-keeping-sleep-time",  Config.getProperty("DBMAXKEEPINGSLEEPTIME","30000"));
		info.setProperty("proxool.minimum-connection-count",  Config.getProperty("DBMINIMUMCONNECTIONCOUNT","1"));
		info.setProperty("proxool.simultaneous-build-throttle",  Config.getProperty("SIMULTANEOUSBUILDTHROTTLE","10"));
		info.setProperty("proxool.statistics-log-level", "ERROR");
		info.setProperty("proxool.test-before-use",  "true");

		return info;
	}
	//	protected void finalize() throws Throwable
	//	{
	//		super.finalize();
	//		alias = null;
	//		poolurl = null;
	//		dbclass = null;
	//	}

	private String getAliasString(String user,String password,String dbclass,String jdbcurl)
	{
		return Config.getProperty("PROJECT")+Format.Md5(String.format("%s-%s-%s-%s", user,password,dbclass,jdbcurl));
	}

	/**
	 * @see easy.sql.Sql#initdb()
	 */
	protected void initdb()
	{
		usepool = "true".equals(Config.getProperty("USEDBPPOOL","true"));

		try
		{
			if (usepool)
			{
				Class.forName(POOLCLASS);
				Properties info = getProperties();
				//info.setProperty("proxool.statistics-log-level", "ERROR");
				//info.setProperty("house-keeping-sleep-time", "30000");
				info.setProperty("user",user);
				info.setProperty("password",password);

				String poolurl = "proxool." +getAliasString(user,password,dbclass,jdbcurl)+":";

				//				System.out.println(poolurl+dbclass+":"+jdbcurl);
				conn = DriverManager.getConnection(poolurl+dbclass+":"+jdbcurl,info);

				if (!Format.isEmpty(userwrite) || !Format.isEmpty(passwordwrite) || !Format.isEmpty(jdbcurlwrite) || !Format.isEmpty(dbclasswrite))
				{
					Properties writeinfo = getProperties();
					//info.setProperty("proxool.statistics-log-level", "ERROR");
					//info.setProperty("house-keeping-sleep-time", "30000");
					writeinfo.setProperty("user",userwrite);
					writeinfo.setProperty("password",passwordwrite);

					//					System.out.println(jdbcurlwrite);
					//					System.out.println(poolurl+dbclasswrite+":"+jdbcurlwrite);
					String writepoolurl = "proxool." +getAliasString(userwrite,passwordwrite,dbclasswrite,jdbcurlwrite)+":";

					connwrite = DriverManager.getConnection(writepoolurl+dbclasswrite+":"+jdbcurlwrite,writeinfo);
				}
				else
				{
					connwrite = conn;
				}

//				System.out.println(connwrite == conn);
			}
			else
			{
				Class.forName(dbclass);
				conn = DriverManager.getConnection(jdbcurl,user,password);

				if (!Format.isEmpty(userwrite) || !Format.isEmpty(passwordwrite) || !Format.isEmpty(jdbcurlwrite) || !Format.isEmpty(dbclasswrite))
				{
					Class.forName(dbclasswrite);
					connwrite = DriverManager.getConnection(jdbcurlwrite,userwrite,passwordwrite);
				}
				else
				{
					connwrite = conn;
				}
			}
			stmt = conn.createStatement(resultSetType,resultSetConncurrency);
			stmtwrite = connwrite.createStatement(resultSetType,resultSetConncurrency);
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

	/*public static void main(String[] args)
	{
		CPSql sql = new CPSql();

		try
		{
			BaseTable bt = new BaseTable();
			bt.setTablename("real_test");
			bt.Add("m_date","2019-02-01");
			bt.Add("m_hour",Math.random()*1000);
			bt.Insert();

			DataSet ds = sql.executeQuery("select * from real_test");
			System.out.println(ds.getRowList());
		}
		catch (SQLException e)
		{
			Log.OutException(e);
		}

		sql.close();
	}*/
}