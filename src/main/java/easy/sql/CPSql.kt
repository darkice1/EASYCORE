package easy.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import easy.config.Config.getProperty
import easy.util.Format.isEmpty
import easy.util.Log
import org.json.JSONObject
import java.sql.DriverManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 *
 * *Copyright: Easy (c) 2005-2005<br></br>
 * Company: Easy*
 *
 * 连接池操作��
 *
 * @version 1.0 (*2005-7-5 neo*)
 */
@Suppress("unused")
open class CPSql : Sql {
    companion object {
        private val POOL_CACHE = ConcurrentHashMap<String, HikariDataSource>()

        fun getOrCreate(key: String, configProvider: () -> HikariConfig): HikariDataSource =
            POOL_CACHE.computeIfAbsent(key) { HikariDataSource(configProvider()) }

        fun closeAll() = POOL_CACHE.values.forEach { runCatching { it.close() } }
    }

	protected var dbclass: String? = null
	protected var dbclasswrite: String? = null

	protected var usepool: Boolean = true

	protected var config: HikariConfig? = null

	constructor(config: HikariConfig?) {
		this.config = config
	}


	/**
	 * @see Sql.init
	 */
	override fun init() {
		if(config == null) {
			user = getProperty("DBUSER")
			password = getProperty("DBPASSWORD")
			jdbcurl = getProperty("DBURL")
			dbclass = getProperty("DBCLASS")


			userwrite = getProperty("DBUSERWRITE")
			passwordwrite = getProperty("DBPASSWORDWRITE")
			jdbcurlwrite = getProperty("DBURLWRITE")
			dbclasswrite = getProperty("DBCLASSWRITE")
		} else {
			user = config!!.username
			password = config!!.password
			jdbcurl = config!!.jdbcUrl
			dbclass = config!!.driverClassName
		}
	}


	private val properties: HikariConfig
		get() {
			val conf = HikariConfig()
			conf.setAutoCommit(true)

			// 此属性控制从连接池获取连接的等待超时时间（毫秒）。超过该时间未成功获取到连接将抛出 SQLException。默认值：30000。
			conf.connectionTimeout =
				Objects.requireNonNull(getProperty("DBCONNECTIONTIMEOUT", "10000")).toLong()

			/*		此属性控制池中连接的最长生命周期。使用中的连接永远不会退役，只有当它关闭时才会被删除。
			在逐个连接的基础上，应用轻微的负衰减以避免池中的大量灭绝。
			我们强烈建议设置此值，它应比任何数据库或基础结构强加的连接时间限制短几秒。
			值0表示没有最大寿命（无限寿命），当然主题是idleTimeout设置。 默认值：1800000（30分钟）*/
				conf.maxLifetime = Objects.requireNonNull(getProperty("DBMAXCONNECTIONLIFTIME", "30000")).toLong()

			//		此属性控制允许连接在池中空闲的最长时间。 此设置仅在minimumIdle定义为小于时才适用maximumPoolSize。
//		一旦池到达连接，空闲连接将不会退出minimumIdle。连接是否空闲退出的最大变化为+30秒，平均变化为+15秒。
//		在此超时之前，连接永远不会被空闲。值为0表示永远不会从池中删除空闲连接。允许的最小值为10000毫秒（10秒）。
//		默认值：600000（10分钟
			conf.setIdleTimeout(
				Objects.requireNonNull(getProperty("DBIDLETIMEOUT", "10000")).toLong()
			                   )

			// 此属性控制空闲连接的心跳间隔（毫秒）。当连接空闲达到该时长，HikariCP 会调用 Connection.isValid() 保持或刷新连接；0 表示禁用。
			// keepaliveTime 方法仅在 HikariCP 3.4.0+ 提供。
			// 为了兼容旧版本，使用反射尝试调用，若不存在则忽略。
			runCatching {
				HikariConfig::class.java
					.getMethod("setKeepaliveTime", java.lang.Long.TYPE)
					.invoke(conf, Objects.requireNonNull(getProperty("DBKEEPALIVETIME", "10000")).toLong())
			}
//			conf.keepaliveTime = getProperty("DBKEEPALIVETIME", "10000").toLong()

			//		此属性控制HikariCP尝试在池中维护的最小空闲连接数。
//		如果空闲连接低于此值并且池中的总连接数小于maximumPoolSize，
//		则HikariCP将尽最大努力快速有效地添加其他连接。但是，为了获得最高性能和对峰值需求的响应，我们建议不要设置此值，
//		而是允许HikariCP充当固定大小的连接池。 默认值：与maximumPoolSize相同
			conf.setMinimumIdle(
				Objects.requireNonNull(getProperty("DBMINIDEL", "3")).toInt()
			                   )

			conf.setValidationTimeout(
				Objects.requireNonNull(getProperty("DBVALIDATIONTIMEOUT", "10000")).toLong()
			                         )
			// 用于检测连接泄漏的阈值（毫秒）。若连接被借出且在该时间内未归还，将记录堆栈跟踪。建议 ≥ 10000，0 表示禁用。
			conf.leakDetectionThreshold =
				Objects.requireNonNull(getProperty("DBLEAKDETECTIONTHRESHOLD", "2000")).toLong()/*		此属性控制允许池到达的最大大小，包括空闲和正在使用的连接。基本上，此值将确定数据库后端的最大实际连接数。
		对此的合理值最好由您的执行环境决定。当池达到此大小且没有空闲连接可用时，对getConnection（）
		的调用将connectionTimeout在超时前阻塞最多毫秒。请阅读有关连接池尺寸的信息。 默认值：10*/
			// 此属性控制连接池允许达到的最大连接数（空闲 + 活跃）。当无可用连接且已达该上限时，请求将在 connectionTimeout 内阻塞。默认值：10。
			conf.setMaximumPoolSize(Objects.requireNonNull(getProperty("DBCONNECTMAX", "20")).toInt())

			conf.addDataSourceProperty("cachePrepStmts", "true")
			conf.addDataSourceProperty("prepStmtCacheSize", "250")
			conf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
			conf.addDataSourceProperty("useLocalSessionState", "true")
			conf.addDataSourceProperty("useLocalTransactionState", "true")
			conf.addDataSourceProperty("rewriteBatchedStatements", "true")
			conf.addDataSourceProperty("cacheResultSetMetadata", "true")
			conf.addDataSourceProperty("cacheServerConfiguration", "true")
			conf.addDataSourceProperty("elideSetAutoCommits", "true")
			conf.addDataSourceProperty("maintainTimeStats", "false")


			return conf
		}

	private fun getDataSource(user: String?, password: String?, jdbcurl: String?, dbclass: String?): DataSource? {
	    val poolKey = "$user@$jdbcurl"

	    return getOrCreate(poolKey) {
	        val conf = if (this.config == null) {
	            val conf = this.properties
	            conf.username = user
	            conf.setJdbcUrl(jdbcurl)
	            conf.setDriverClassName(dbclass)
	            conf.setPoolName(poolKey)
		        Log.OutLog("初始化数据库[${JSONObject(conf)}]")
	            conf.password = password
	            conf
	        } else {
	            this.config!!
	        }
	        conf
	    }
	}


	private val dataSource by lazy {
		getDataSource(user, password, jdbcurl, dbclass)
	}

	private val writeDataSource by lazy {
		getDataSource(userwrite, passwordwrite, jdbcurlwrite, dbclasswrite)
	}

    init {
        // Ensure all pools are closed when the JVM shuts down
        Runtime.getRuntime().addShutdownHook(Thread { closeAll() })
    }

	/**
	 * @see Sql.initdb
	 */
	override fun initdb() {
		usepool = "true" == getProperty("USEDBPPOOL", "true")

		try {
			if(usepool) {
				conn = this.dataSource?.connection

				connwrite = if(!isEmpty(userwrite) && !isEmpty(jdbcurlwrite) && !isEmpty(dbclasswrite)) {
					this.writeDataSource?.connection
				} else {
					conn
				}

				//				System.out.println(connwrite == conn);
			} else {
				Class.forName(dbclass)
				conn = DriverManager.getConnection(jdbcurl, user, password)

				if(!isEmpty(userwrite) && !isEmpty(jdbcurlwrite) && !isEmpty(dbclasswrite)) {
					Class.forName(dbclasswrite)
					connwrite = DriverManager.getConnection(jdbcurlwrite, userwrite, passwordwrite)
				} else {
					connwrite = conn
				}
			}
			stmt = conn.createStatement(resultSetType, resultSetConncurrency)
			stmtwrite = connwrite.createStatement(resultSetType, resultSetConncurrency)
		} catch (ex: Exception) {
			Log.OutException(ex, jdbcurl)
		}
	}

	constructor()

	constructor(resultSetType: Int, resultSetConncurrency: Int) : super(resultSetType, resultSetConncurrency)
}