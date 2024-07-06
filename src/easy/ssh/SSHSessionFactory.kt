package easy.ssh

import easy.data.SSHSession
import easy.ssh.SSHSessionManager.createSession
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import java.util.*


@Suppress("unused")
class SSHSessionFactory(private val prep:Properties) : BasePooledObjectFactory<SSHSession>() {
	override fun create(): SSHSession {
		val session = createSession(prep)
		return SSHSession(session, SSHSessionManager.localPort)
	}

	override fun wrap(session: SSHSession): PooledObject<SSHSession> {
		return DefaultPooledObject(session)
	}

	override fun destroyObject(p: PooledObject<SSHSession>) {
		p.getObject().session.disconnect()
	}
}
