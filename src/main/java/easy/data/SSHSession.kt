package easy.data

import com.jcraft.jsch.Session

data class SSHSession(val session: Session, val localPort: Int)
