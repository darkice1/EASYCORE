package easy.ssh

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.net.ServerSocket
import java.util.*

@Suppress("unused")
object SSHSessionManager {
	private var session: Session? = null
	private var pLocalPort: Int = -1

	init {
		// Register a shutdown hook to close the SSH session on JVM exit
		Runtime.getRuntime().addShutdownHook(Thread {
			closeSession()
		})
	}

	val localPort: Int
		get() = pLocalPort

	@Suppress("MemberVisibilityCanBePrivate")
	fun getSession(prep: Properties): Session? {
		if (session == null || !session!!.isConnected) {
			session = createSession(prep)
		}
		return session
	}

	fun getLocalPort(prep: Properties): Int {
		if (pLocalPort == -1) {
			getSession(prep) // Ensure session is created
		}
		return pLocalPort
	}

	@Suppress("MemberVisibilityCanBePrivate")
	fun createSession(prep: Properties): Session {
		val sshUser = prep.getProperty("SSHUSER") ?: throw NullPointerException("SSHUSER not found")
		val sshPasswd = prep.getProperty("SSHPASSWORD") ?: ""

		val sshHost = prep.getProperty("SSHHOST") ?: throw NullPointerException("SSHHOST not found")
		val sshPort = prep.getProperty("SSHPORT")?.toInt() ?: throw NullPointerException("SSHPORT not found")

		val sshLocalHost = prep.getProperty("SSHLOCALHOST") ?: "127.0.0.1"
		pLocalPort = if (prep.getProperty("SSHLOCALPORT").isNullOrBlank()) {
			findFreePort()
		} else {
			prep.getProperty("SSHLOCALPORT")?.toInt() ?: findFreePort()
		}

		val sshRemoteHost = prep.getProperty("SSHREMOTEHOST") ?: throw NullPointerException("SSHREMOTEHOST not found")
		val sshRemotePort = prep.getProperty("SSHREMOTEPORT")?.toInt() ?: throw NullPointerException("SSHREMOTEPORT not found")
		val sshKeyFilePath = prep.getProperty("SSHKEYFILE")


		val jsch = JSch()
		val session = jsch.getSession(sshUser, sshHost, sshPort)
		if (!sshKeyFilePath.isNullOrBlank()) {
			jsch.addIdentity(sshKeyFilePath)
		} else {
			if (sshPasswd.isNotBlank()) {
				session.setPassword(sshPasswd)
			} else {
				throw NullPointerException("SSHPASSWORD and SSHKEYFILE not found")
			}
		}

		val config = Properties().apply {
			put("StrictHostKeyChecking", "no")
			put("compression.s2c", "zlib,none")
			put("compression.c2s", "zlib,none")
			put("CompressionLevel", "9")
		}
		session.setConfig(config)
		session.connect()
		session.setPortForwardingL(sshLocalHost, pLocalPort, sshRemoteHost, sshRemotePort)

		return session
	}

	private fun findFreePort(): Int {
		ServerSocket(0).use { socket ->
			socket.reuseAddress = true
			return socket.localPort
		}
	}

	@Suppress("MemberVisibilityCanBePrivate")
	fun closeSession() {
		session?.disconnect()
		session = null
		pLocalPort = -1
	}
}