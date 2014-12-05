/**
 * 
 */
package easy.util;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import easy.io.JFile;
import easy.net.Proxy;

/**
 * 获取代理列表
 * @author Neo(starneo@gmail.com)2012-1-21
 *
 */
public class GetProxyList
{
	//http://www.cnproxy.com/proxy2.html
	private final static String URL = "http://www.cnproxy.com/proxy%d.html";
	private final static Pattern RESULTPAT = Pattern.compile("<tr><td>(.*?)<SCRIPT type=text/javascript>document.write\\(\":\"\\+(.*?)\\)</SCRIPT></td><td>(.*?)</td><td>");
	private final static Pattern JS_RESULTPAT = Pattern.compile("(.*?)=\"(.*?)\";");


	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Proxy.openProxy("190.121.135.179", "8080", null, null);
		try
		{
			//119.46.92.69:80
			//190.121.135.179:8080
			//190.130.49.157:80
			String th = JFile.loadHttpFile("http://wosoproxy2.appspot.com/",null,null,null,null,30000,null);
			System.out.println(th);
		}
		catch (ConnectException e)
		{
			Log.OutLog(e.toString());
		}
		catch (IOException e)
		{
			Log.OutLog(e.toString());
		}
		Proxy.closeProxy();	
		
		for (int i=1; i<=10;i++)
		{
			try
			{
				String html = JFile.loadHttpFile(String.format(URL, i),null,null,"gbk",null);
				
				//String js = Format.getContent(html, "<SCRIPT type=\"text/javascript\">\n", "</SCRIPT>");
				//System.out.println(js);
				Map<String,String> jshash = new HashMap<String,String>(); 
				Matcher jsms = JS_RESULTPAT.matcher(html);
				while (jsms.find())
				{
					if (jsms.groupCount() == 2)
					{
						jshash.put(jsms.group(1), jsms.group(2));
						//System.out.println(jsms.group(1));
						//System.out.println(jsms.group(2));
					}
				}

				
				//System.out.println(html);
				Matcher ms = RESULTPAT.matcher(html);
				//DataSet ds = new DataSet();
				
				while (ms.find())
				{
					if (ms.groupCount() == 3)
					{
						String host = ms.group(1);
						String type = ms.group(3);
						
						//System.out.println(ms.group(1));
						//System.out.println(ms.group(3));
						String []jsl = ms.group(2).split("\\+");
						StringBuffer buf = new StringBuffer();
						for (String t : jsl)
						{
							buf.append(jshash.get(t));
						}
						String port = buf.toString();
						
						if (type.equals("HTTP"))
						{
							Log.OutLog(host+":"+port);
							boolean isok = false;
							Proxy.openProxy(host, port, null, null);
							try
							{
								//119.46.92.69:80
								//190.121.135.179:8080
								//190.130.49.157:80
								String th = JFile.loadHttpFile("http://darkneospace.appspot.com/",null,null,null,null,5000,null);
								System.out.println(th);
								isok = true;
							}
							catch (ConnectException e)
							{
								//Log.OutLog(e.toString());
							}
							catch (IOException e)
							{
								//Log.OutLog(e.toString());
							}
							Proxy.closeProxy();
							if (isok)
							{
								Log.OutLog("##################"+host+":"+port);
							}
						}
							
						//Row r = new Row();

						//ds.AddRow(r);
					}
				}
			}
			catch (ConnectException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
