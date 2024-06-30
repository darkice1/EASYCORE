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
		val sshPasswd = prep.getProperty("SSHPASSWORD") ?: ""

		val sshHost = prep.getProperty("SSHHOST") ?: throw NullPointerException("SSHHOST not found")
		val sshPort = prep.getProperty("SSHPORT")?.toInt() ?: throw NullPointerException("SSHPORT not found")


		val sshlocalHost = prep.getProperty("SSHLOCALHOST")?:"127.0.0.1"
		val sshlocalPort = prep.getProperty("SSHLOCALPORT")?.toInt() ?: findFreePort()

		val sshRemoteHost = prep.getProperty("SSHREMOTEHOST")?: throw NullPointerException("SSHREMOTEHOST not found")
		val sshRemotePort = prep.getProperty("SSHREMOTEPORT")?.toInt() ?: throw NullPointerException("SSHREMOTEPORT not found")
		val sshKeyFilePath = prep.getProperty("SSHKEYFILE")

		val jsch = JSch()
		val session = jsch.getSession(sshUser, sshHost, sshPort)
		if (sshKeyFilePath.isNotBlank())
		{
			jsch.addIdentity(sshKeyFilePath)
		}
		else
		{
			if (sshPasswd.isNotBlank()) {
				session.setPassword(sshPasswd)
			}
			else
			{
				throw NullPointerException("SSHPASSWORD and SSHKEYFILE not found")
			}
		}

		val config = Properties()
		config["StrictHostKeyChecking"] = "no"
		config["compression.s2c"] = "zlib,none"
		config["compression.c2s"] = "zlib,none"
		config["CompressionLevel"] = "9"
		session.setConfig(config)
		session.connect()

		session.setPortForwardingL(sshlocalHost, sshlocalPort, sshRemoteHost, sshRemotePort)
		return SSHSession(session, sshlocalPort)
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
