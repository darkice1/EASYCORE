package easy.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * <p><i>Copyright: Esay (c) 2005-2005<br>
 * Company: Esay</i></p>
 *
 * mail验证类
 *
 * @version 1.0 (<i>2005-7-4 neo</i>)
 */

public class MailAuthenticator extends Authenticator
{
    private String username = null;
    private String userpasswd = null;
    
    public MailAuthenticator(String username,String userpasswd)
    {
        this.username = username;
        this.userpasswd = userpasswd;
    }
    
    public void setUserName(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.userpasswd = password;
    }

    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(username,userpasswd);
    }
}
