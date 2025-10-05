package easy.ssh

import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import java.net.ServerSocket
import java.util.*

@Suppress("unused")
object SSHSessionManager {
	private var session: Session? = null
	private var pLocalPort: Int = -1
	private var sessionConfigFingerprint: String? = null

	init {
		// Register a shutdown hook to close the SSH session on JVM exit
		Runtime.getRuntime().addShutdownHook(Thread {
			closeSession()
		})
	}

	val localPort: Int
		get() = pLocalPort

	/**
	 * 对外提供的获取 Session 的方法：如果 session 尚未创建或已断开，则重新创建。
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun getSession(prep: Properties): Session? {
		val newFingerprint = computeConfigFingerprint(prep)
		val isSameConfig = session != null && session!!.isConnected && sessionConfigFingerprint == newFingerprint
		if(!isSameConfig) {
			closeSession()
			session = createSession(prep)
			sessionConfigFingerprint = newFingerprint
		}
		return session
	}

	/**
	 * 对外提供的获取本地映射端口的方法：如果本地端口尚未生成，则会先确保 Session 创建。
	 */
	fun getLocalPort(prep: Properties): Int {
		if (pLocalPort == -1) {
			getSession(prep) // Ensure session is created
		}
		return pLocalPort
	}

	/**
	 * 真正创建 Session 的核心逻辑。
	 * 在这里做所有 SSH 参数的解析、Session 配置、端口映射和异常处理。
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun createSession(prep: Properties): Session {
		// 1) 解析必需的 SSH 参数
		val sshUser = prep.getProperty("SSHUSER")
			?: throw NullPointerException("SSHUSER not found in prep")
		val sshHost = prep.getProperty("SSHHOST")
			?: throw NullPointerException("SSHHOST not found in prep")
		val sshPort = prep.getProperty("SSHPORT")?.toInt()
			?: throw NullPointerException("SSHPORT not found in prep")

		// 密码或秘钥
		val sshPasswd = prep.getProperty("SSHPASSWORD") ?: ""
		val sshKeyFilePath = prep.getProperty("SSHKEYFILE")

		// 本地转发端口
		val sshLocalHost = prep.getProperty("SSHLOCALHOST", "127.0.0.1")
		pLocalPort = if (prep.getProperty("SSHLOCALPORT").isNullOrBlank()) {
			findFreePort()
		} else {
			prep.getProperty("SSHLOCALPORT")?.toInt() ?: findFreePort()
		}

		// 远程映射的目标与端口
		val sshRemoteHost = prep.getProperty("SSHREMOTEHOST")
			?: throw NullPointerException("SSHREMOTEHOST not found in prep")
		val sshRemotePort = prep.getProperty("SSHREMOTEPORT")?.toInt()
			?: throw NullPointerException("SSHREMOTEPORT not found in prep")

		// 2) 构建 JSCH
		val jsch = JSch()
		// 如果有 SSHKEYFILE，就用它；否则，必须有 SSHPASSWORD
		if (!sshKeyFilePath.isNullOrBlank()) {
			jsch.addIdentity(sshKeyFilePath)
		}

		// 3) 创建并配置 session
		val session = jsch.getSession(sshUser, sshHost, sshPort)
		if (sshKeyFilePath.isNullOrBlank()) {
			// 若无 key，则需要密码
			if (sshPasswd.isNotBlank()) {
				session.setPassword(sshPasswd)
			} else {
				throw NullPointerException("SSHPASSWORD and SSHKEYFILE both not provided")
			}
		}

		// 4) 合并 prep 和默认配置
		val config = buildConfig(prep)
		session.setConfig(config)

		// 5) 设置超时和保活（从 config 中读取，若无则用默认值）
		val connectTimeout = config.getProperty("SSHCONNECTTIMEOUT", "10000").toInt()
		// session.setTimeout(...) 主要影响 Socket 读超时
		session.timeout = config.getProperty("SSHREADTIMEOUT", "15000").toInt()
		// 保活
		session.serverAliveInterval = config.getProperty("SSHSERVERALIVEINTERVAL", "5000").toInt()
		session.serverAliveCountMax = config.getProperty("SSHSERVERALIVECOUNTMAX", "3").toInt()

		// 6) 开始连接，若失败则断开并抛异常
		try {
			session.connect(connectTimeout)
		} catch (e: JSchException) {
			// 避免 session 占用资源或卡住
			session.disconnect()
			// 视情况，这里可以记录日志或自定义异常
			throw e
		}

		// 7) 设置本地转发端口
		session.setPortForwardingL(sshLocalHost, pLocalPort, sshRemoteHost, sshRemotePort)

		return session
	}

	/**
	 * 合并外部 prep 与默认 config 的方法。
	 *
	 * - 先把 prep 中的所有键值对都复制到新的 Properties。
	 * - 没有声明的就用默认值。
	 * - 也可以在这里做特定 key 的覆盖或转换。
	 */
	private fun buildConfig(prep: Properties): Properties {
		val config = Properties()

		// 把 prep 里的所有 key 都放到 config 中
		config.putAll(prep)

		// 如果用户没写，则为其设置一些常见的默认值
		config.putIfAbsent("StrictHostKeyChecking", "no")
		config.putIfAbsent("compression.s2c", "zlib,none")
		config.putIfAbsent("compression.c2s", "zlib,none")
		config.putIfAbsent("CompressionLevel", "9")
		// 你也可以根据需要添加更多缺省值

		return config
	}

	/**
	 * 动态寻找空闲端口，若 'SSHLOCALPORT' 未配置，就自动分配。
	 */
	private fun findFreePort(): Int {
		ServerSocket(0).use { socket ->
			socket.reuseAddress = true
			return socket.localPort
		}
	}

	/**
	 * 手动关闭已有的 SSH 会话。
	 * 也会在 ShutdownHook 中自动调用。
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun closeSession() {
		session?.disconnect()
		session = null
		pLocalPort = -1
		sessionConfigFingerprint = null
	}

	private fun computeConfigFingerprint(prep: Properties): String {
		return prep.entries
			.asSequence()
			.map { it.key.toString() to (it.value?.toString() ?: "") }
			.sortedBy { it.first }
			.joinToString(separator = "|") { (key, value) -> "$key=$value" }
	}
}
