/**
 * 
 */
package easy.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.conn.ConnectTimeoutException;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;

import easy.model.WebAgent;

/**
 * @author starneo@gmail.com 2016年8月17日
 */
public class EWebClient
{
	private WebClient client;
	private final String AGENT = WebAgent.getRandAgent();
//	private String proxyAuthorization = null;
	
	public EWebClient()
	{
		client = new WebClient();//BrowserVersion.CHROME
		
		client.getOptions().setUseInsecureSSL(true);

		client.getOptions().setCssEnabled(true);
		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setRedirectEnabled(true);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false); // 防止js语法错误抛出异常
		client.getCookieManager().setCookiesEnabled(true);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		client.getOptions().setTimeout(5000);
		client.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		
		client.addRequestHeader("User-Agent", AGENT);
	}
	
	/**
	 * @return the aGENT
	 */
	public String getAGENT()
	{
		return AGENT;
	}

	public void setProxyAuthorization(String name,String passwd)
	{
		DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) client.getCredentialsProvider();
		credentialsProvider.addCredentials(name, passwd);

//
//		try
//		{
//			proxyAuthorization = String.format("Basic %s",Format.encodeBase64(String.format("%s:%s", name,passwd).getBytes()));
//		}
//		catch (IOException e)
//		{
//			Log.OutException(e);
//		}
	}
	
	public void setProxy(String host, int port)
	{
		ProxyConfig proxyConfig = new ProxyConfig();
		proxyConfig.setProxyHost(host);
		proxyConfig.setProxyPort(port);
		
		client.getOptions().setProxyConfig(proxyConfig);
	}
	
	/**
	 * @return the webclient
	 */
	public WebClient getWebclient()
	{
		return client;
	}

	public void setTimeout(int timeout)
	{
		client.getOptions().setTimeout(timeout);
	}
	
	public void addFakeIp(final String fakeip)
	{
		//client.addRequestHeader("x-forwarded-for", fakeip);
		client.addRequestHeader("X-FORWARDED-FOR", fakeip);
	}
	
	public String getCookieJson()
	{
		//String cookie = null;
		
		
		CookieManager cm = client.getCookieManager();
//		List<Cookie> list = new ArrayList<Cookie>();
//		for (Cookie c : cm.getCookies())
//		{
//			list.add(c);
//		}
//		//Set<Cookie> cs = cm.getCookies();
		
		//System.out.println( JSONObject.fromObject(cm));
		
		
		return JSONObject.fromObject(cm).toString();
	}
	
	public void jsonToCookieManager(String jsonstr)
	{
		
		//client.setCookieManager(cookieManager);
		
		JSONObject json = JSONObject.fromObject(jsonstr);
		
		CookieManager cm = client.getCookieManager();
		
		JSONArray arr = json.getJSONArray("cookies");
		for (int i=0,len=arr.size();i<len;i++)
		{
			JSONObject cj = arr.getJSONObject(i);

			//    public Cookie(final String domain, final String name, final String value, final String path, final Date expires,
//	        final boolean secure, final boolean httpOnly)
			
			Date d = null;
			JSONObject dj = cj.getJSONObject("expires");
			if (dj != null && dj.isEmpty() == false)
			{
				d = new Date();
				d.setTime(dj.getLong("time"));
			}
			
			//System.out.println(d);
			
			Cookie pc = new Cookie(cj.getString("domain"),cj.getString("name"),cj.getString("value"),cj.getString("path"),d,cj.getBoolean("secure"),cj.getBoolean("httpOnly"));

			cm.addCookie(pc);
		}
		
		
//		System.out.println("#"+ JSONObject.fromObject(cm).toString());
		
		client.setCookieManager(cm);
	}
	
	public void close()
	{
		client.close();
	}
	
	public <P extends Page> P getPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{	
		return getPage(url,null);
	}
	
	public <P extends Page> P getPage(String url,String ref) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
//		if (proxyAuthorization != null)
//		{
//			client.addRequestHeader("Proxy-Authorization", proxyAuthorization);
//		}
		
		if (ref != null && "".equals(ref) == false)
		{
			client.addRequestHeader("referer", ref);
		}
		
		P  p = null;
		for (int i=0; i<3 ;i++)
		{
			try
			{
				p = client.getPage(url);
				
				if (p.getWebResponse().getStatusCode() != 429)
				{
					break;
				}
			}
			catch (SocketTimeoutException | ConnectTimeoutException e)
			{
			}

//			else
//			{
//				try
//				{
//					Thread.sleep(500);
//				}
//				catch (InterruptedException e)
//				{
//					Log.OutException(e);
//				}
//			}
		}
		
		return p;
	}
	
	public String get(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		return get(url,null);
	}
	
	public String get(String url,String ref) throws FailingHttpStatusCodeException, MalformedURLException, IOException
	{
		Page hp = getPage(url,ref);
//		if (hp instanceof HtmlPage)
//		{
//			return ((HtmlPage)hp).getWebResponse().getContentAsString();			
//		}
//		else
//		{
//			return ((TextPage)hp).getw.getContent();			
//		}	
//		System.out.println(Format.beanToString(hp.getWebResponse()));
		return hp.getWebResponse().getContentAsString();
	}
}