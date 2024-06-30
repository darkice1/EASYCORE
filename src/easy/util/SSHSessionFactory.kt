package easy.util

import com.jcraft.jsch.JSch
import easy.data.SSHSession
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import java.net.ServerSocket
import java.util.*


@Suppress("unused")
class SSHSessionFactory(private val prep:Properties) : BasePooledObjectFactory<SSHSession>() {
	override fun create(): SSHSession {
		val sshUser = prep.getProperty("SSHUSER") ?: throw NullPointerException("SSHUSER not found")
		val sshHost = prep.getProperty("SSHHOST") ?: throw NullPointerException("SSHHOST not found")
		val sshPort = prep.getProperty("SSHPORT")?.toInt() ?: throw NullPointerException("SSHPORT not found")
		val sshRemotePort = prep.getProperty("SSHREMOTEPORT")?.toInt() ?: throw NullPointerException("SSHREMOTEPORT not found")
		val sshKeyFilePath = prep.getProperty("SSHKEYFILE") ?: throw NullPointerException("SSHKEYFILE not found")

		val jsch = JSch()
		jsch.addIdentity(sshKeyFilePath)

		val session = jsch.getSession(sshUser, sshHost, sshPort)
		val config = Properties()
		config["StrictHostKeyChecking"] = "no"
		config["compression.s2c"] = "zlib,none"
		config["compression.c2s"] = "zlib,none"
		config["CompressionLevel"] = "9"
		session.setConfig(config)
		session.connect()

		val localPort = findFreePort()
		session.setPortForwardingL(localPort, "localhost", sshRemotePort)
		return SSHSession(session, localPort)
	}

	override fun wrap(session: SSHSession): PooledObject<SSHSession> {
		return DefaultPooledObject(session)
	}

	override fun destroyObject(p: PooledObject<SSHSession>) {
		p.getObject().session.disconnect()
	}

	private fun findFreePort(): Int {
		ServerSocket(0).use { socket ->
			socket.reuseAddress = true
			return socket.localPort
		}
	}
}
