package easy.net;

import java.io.IOException;
import java.net.Authenticator;

import easy.config.Config;
import easy.io.JFile;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2008</i></p>
 *
 * 设置代理Proxy class说明
 *
 * @version 1.0 (<i>2008-10-6 neo(starneo@gmail.com)</i>)
 */

public class Proxy
{
	/**
	 * 打开代理服务器
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 */
	public static void openProxy(String host,String port,String user,String password)
	{
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("proxyHost", host);
		System.getProperties().put("proxyPort", port);
		
		if (user != null)
		{
			Authenticator.setDefault(new ProxyAuthenticator(user,password));
		}
	}
	
	public static void openSocksProxy(String host,String port,String user,String password)
	{
		System.getProperties().put("proxySet",Boolean.TRUE);
		System.getProperties().put("socksProxyHost", host);
		System.getProperties().put("socksProxyPort", port);
		
		if (user != null)
		{
			Authenticator.setDefault(new ProxyAuthenticator(user,password));
		}
	}	
	
	/**
	 * 关闭代理
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 */
	public static void closeProxy()
	{
		System.getProperties().put("proxySet", Boolean.FALSE);
		System.getProperties().put("proxyHost", "");
		System.getProperties().put("proxyPort", "");
		System.getProperties().put("socksProxyHost", "");
		System.getProperties().put("socksProxyPort", "");
	}
	
	public static void initCfgProxy()
	{
		String host = Config.getProperty("PROXYHOST");
		String port = Config.getProperty("PROXYPORT");
		String user = Config.getProperty("PROXYUSER");
		String password = Config.getProperty("PROXYPASSWORD");
		
		if (host != null && "".equals(host) == false)
		{
			openProxy(host,port,user,password);
		}
		
		String shost = Config.getProperty("SOCKSPROXYHOST");
		String sport = Config.getProperty("SOCKSPROXYPORT");
		String suser = Config.getProperty("SOCKSPROXYUSER");
		String spassword = Config.getProperty("SOCKSPROXYPASSWORD");
		
		if (shost != null && "".equals(shost) == false)
		{
			openSocksProxy(shost,sport,suser,spassword);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			System.out.println(JFile.loadHttpFile("http://woso100.com"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}
