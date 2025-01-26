import easy.ssh.SSHSessionManager
import java.util.*

object Testssh {
	@JvmStatic
	fun main(args: Array<String>) {
		val srcpps = Properties()
		srcpps.setProperty("SSHUSER", "root")
		srcpps.setProperty("SSHKEYFILE",  "~/.ssh/id_rsa")

//		106.39.185.114  8.219.182.230
		srcpps.setProperty("SSHHOST", "8.219.182.230")
		srcpps.setProperty("SSHPORT", "15322")

		srcpps.setProperty("SSHLOCALPORT", "")

		srcpps.setProperty("SSHREMOTEHOST", "localhost")
		srcpps.setProperty("SSHREMOTEPORT", "5432")
		println(srcpps)
		SSHSessionManager.getSession(srcpps)
		println("localPort:${SSHSessionManager.localPort}")
		SSHSessionManager.closeSession()
	}
}