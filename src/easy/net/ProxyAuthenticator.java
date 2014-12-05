package easy.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * <p>
 * <i>Copyright: youhow.net(c) 2005-2008</i>
 * </p>
 * 
 * ProxyAuthenticator class说明
 * 
 * @version 1.0 (<i>2008-10-6 neo(starneo@gmail.com)</i>)
 */

public class ProxyAuthenticator extends Authenticator
{
	private String name;

	private String password;

	public ProxyAuthenticator(String name, String password)
	{
		super();
		this.name = name;
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(this.getName(), this.getPassword().toCharArray());
	}
}
