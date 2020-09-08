package easy.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import easy.config.Config;
import easy.util.Format;
import easy.util.Log;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
	protected String dbclass;
	protected String dbclasswrite;

	protected boolean usepool = true;

	protected HikariConfig config = null;

	public CPSql(HikariConfig config)
	{
		this.config = config;
	}


	/**
	 * @see easy.sql.Sql#init()
	 */
	protected void init()
	{
		if (config == null)
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
		else
		{
			user = config.getUsername();
			password = config.getPassword();
			jdbcurl = config.getJdbcUrl();
			dbclass = config.getDriverClassName();
		}
	}


	private HikariConfig getProperties()
	{
		HikariConfig conf = new HikariConfig();
		conf.setAutoCommit(true);

//		此属性控制允许连接在池中空闲的最长时间。 此设置仅在minimumIdle定义为小于时才适用maximumPoolSize。
//		一旦池到达连接，空闲连接将不会退出minimumIdle。连接是否空闲退出的最大变化为+30秒，平均变化为+15秒。
//		在此超时之前，连接永远不会被空闲。值为0表示永远不会从池中删除空闲连接。允许的最小值为10000毫秒（10秒）。
//		默认值：600000（10分钟
		conf.setConnectionTimeout(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBCONNECTIONTIMEOUT", "10000"))));

/*		此属性控制池中连接的最长生命周期。使用中的连接永远不会退役，只有当它关闭时才会被删除。
		在逐个连接的基础上，应用轻微的负衰减以避免池中的大量灭绝。
		我们强烈建议设置此值，它应比任何数据库或基础结构强加的连接时间限制短几秒。
		值0表示没有最大寿命（无限寿命），当然主题是idleTimeout设置。 默认值：1800000（30分钟）*/
		conf.setMaxLifetime(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBMAXCONNECTIONLIFTIME", "30000"))));

		conf.setIdleTimeout(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBIDLETIMEOUT", "10000"))));
//		此属性控制HikariCP尝试在池中维护的最小空闲连接数。
//		如果空闲连接低于此值并且池中的总连接数小于maximumPoolSize，
//		则HikariCP将尽最大努力快速有效地添加其他连接。但是，为了获得最高性能和对峰值需求的响应，我们建议不要设置此值，
//		而是允许HikariCP充当固定大小的连接池。 默认值：与maximumPoolSize相同
		conf.setMinimumIdle(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBMINIDEL", "3"))));

		conf.setValidationTimeout(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBVALIDATIONTIMEOUT", "10000"))));
		conf.setLeakDetectionThreshold(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBLEAKDETECTIONTHRESHOLD", "100"))));
/*		此属性控制允许池到达的最大大小，包括空闲和正在使用的连接。基本上，此值将确定数据库后端的最大实际连接数。
		对此的合理值最好由您的执行环境决定。当池达到此大小且没有空闲连接可用时，对getConnection（）
		的调用将connectionTimeout在超时前阻塞最多毫秒。请阅读有关连接池尺寸的信息。 默认值：10*/
		conf.setMaximumPoolSize(Integer.parseInt(Objects.requireNonNull(Config.getProperty("DBCONNECTMAX", "20"))));
		conf.addDataSourceProperty("cachePrepStmts", "true");
		conf.addDataSourceProperty("prepStmtCacheSize", "250");
		conf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		conf.addDataSourceProperty("useLocalSessionState", "true");
		conf.addDataSourceProperty("useLocalTransactionState", "true");
		conf.addDataSourceProperty("rewriteBatchedStatements", "true");
		conf.addDataSourceProperty("cacheResultSetMetadata", "true");
		conf.addDataSourceProperty("cacheServerConfiguration", "true");
		conf.addDataSourceProperty("elideSetAutoCommits", "true");
		conf.addDataSourceProperty("maintainTimeStats", "false");
		conf.addDataSourceProperty("leakDetectionThreshold","true");


		return conf;
	}
	//	protected void finalize() throws Throwable
	//	{
	//		super.finalize();
	//		alias = null;
	//		poolurl = null;
	//		dbclass = null;
	//	}

/*	private String getAliasString(String user,String password,String dbclass,String jdbcurl)
	{
		return Config.getProperty("PROJECT")+Format.Md5(String.format("%s-%s-%s-%s", user,password,dbclass,jdbcurl));
	}*/

	private static final Map<String,DataSource> DSMAP = new ConcurrentHashMap<>();

	private DataSource getDataSource(String user,String password,String jdbcurl,String dbclass)
	{
		DataSource ds;
		String poolname;
		if (config == null)
		{
			poolname = jdbcurl+user;
			ds = DSMAP.get(poolname);

			if (ds == null)
			{
				HikariConfig conf = getProperties();
				conf.setUsername(user);
				conf.setPassword(password);
				conf.setJdbcUrl(jdbcurl);
				conf.setDriverClassName(dbclass);

				conf.setPoolName(poolname);

				config = conf;
			}
		}
		else
		{
			poolname = config.getPoolName();
			ds = DSMAP.get(poolname);
		}

		if (ds == null)
		{
			ds = new HikariDataSource(config);
			DSMAP.put(poolname,ds);
		}

		return ds;
	}


	private DataSource getDataSource()
	{
		return getDataSource(user,password,jdbcurl,dbclass);
	}

	private DataSource getWriteDataSource()
	{
		return getDataSource(userwrite,passwordwrite,jdbcurlwrite,dbclasswrite);
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
				conn = getDataSource().getConnection();

				if (!Format.isEmpty(userwrite) || !Format.isEmpty(passwordwrite) || !Format.isEmpty(jdbcurlwrite) || !Format.isEmpty(dbclasswrite))
				{
					connwrite = getWriteDataSource().getConnection();
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

/*	public static void main(String[] args)
	{
		HikariConfig conf = new HikariConfig();
		conf.setPoolName("test");
		conf.setJdbcUrl( Config.getProperty("DBURL"));
		conf.setDriverClassName( Config.getProperty("DBCLASS"));
		conf.setUsername( Config.getProperty("DBUSER"));
		conf.setPassword( Config.getProperty("DBPASSWORD"));


		CPSql sql = new CPSql(conf);
		try
		{
			DataSet ds = sql.executeQuery("select * from real_test");
			System.out.println(ds.getRowList());

		}
		catch (SQLException throwables)
		{
			Log.OutException(throwables);
		}
		sql.close();
	}*/
}