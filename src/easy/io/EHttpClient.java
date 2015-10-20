/**
 * 
 */
package easy.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import easy.model.WebAgent;
import easy.util.Format;
import easy.util.Log;

/**
 * @author Neo(starneo@gmail.com)2013-11-12
 */
public class EHttpClient
{
	private BasicCookieStore cookieStore = new BasicCookieStore();

	private HttpClientBuilder httpbuilder = HttpClients.custom();

	private CloseableHttpClient client;
	// private CloseableHttpClient client = HttpClients.custom().build();
	private String agent = WebAgent.getRandAgent();
	private RequestConfig requestconfig;
	private PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	private String baseAuthorization = null;
	
	public final static String POSTSPLIT = new String(new char[]{0,9});

	// private HttpClient client = HttpsClient.getInstance();

	public EHttpClient()
	{
		init(null, null);
	}

	public EHttpClient(final String host, final Integer port)
	{
		init(host, port);
	}

	public void setProxy(final String host, final Integer port)
	{
		if (host != null && "".equals(host) == false && port != null)
		{
			// System.out.println(host+"##"+port);
			HttpHost proxy = new HttpHost(host, port);
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			httpbuilder.setRoutePlanner(routePlanner);
			client = httpbuilder.build();

			proxy = null;
		}
	}
	
	public void setBaseAuthorization(String name,String passwd)
	{
		try
		{
			baseAuthorization = String.format("Basic %s",Format.encodeBase64(String.format("%s:%s", "dsp_wisemedia","wisemedia2015").getBytes()));
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}

	private void init(final String host, final Integer port)
	{
		setProxy(host, port);

		try
		{
			cm.setMaxTotal(1000);  
			cm.setDefaultMaxPerRoute(20);  
			//httpbuilder.setConnectionManager(cm);

			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
							null, new TrustStrategy()
							{
								@Override
								public boolean isTrusted(X509Certificate[] chain,
												String authType)
												throws java.security.cert.CertificateException
								{
									return true;
								}
							}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
							sslContext,new X509HostnameVerifier() {  
								  
		                        @Override  
		                        public boolean verify(String arg0, SSLSession arg1) {  
		                            return true;  
		                        }  
		  
		                        @Override  
		                        public void verify(String host, SSLSocket ssl)  
		                                throws IOException {  
		                        }  
		  
		                        @Override  
		                        public void verify(String host, X509Certificate cert)  
		                                throws SSLException {  
		                        }  
		  
		                        @Override  
		                        public void verify(String host, String[] cns,  
		                                String[] subjectAlts) throws SSLException {  
		                        }  
		  
		                    });

			httpbuilder.setDefaultCookieStore(cookieStore);
			httpbuilder.setSSLSocketFactory(sslsf);
			
			client = httpbuilder.build();
		}
		catch (KeyManagementException | NoSuchAlgorithmException
						| KeyStoreException e)
		{
			Log.OutException(e);
		}

		setConnectionTimeout(30 * 1000);
	}

	public BasicCookieStore getCookieStore()
	{
		return cookieStore;
	}

	public void addCookie(final String cookie, final String domain)
	{
		String t[] = cookie.split(";");
		for (String c : t)
		{
			c = c.trim();
			String ts[] = c.split("=");
			if (ts.length >= 2)
			{
				BasicClientCookie pc = new BasicClientCookie(ts[0], ts[1]);
				pc.setDomain(domain);
				cookieStore.addCookie(pc);

				pc = null;
			}

			// Log.OutLog("%s=%s; ",ts[0], ts[1]);

			ts = null;
			c = null;
		}
		t = null;
	}

	public void setConnectionTimeout(final int time)
	{
		// 设置请求和传输超时时间
		requestconfig = RequestConfig.custom()
						.setConnectionRequestTimeout(time)
						.setConnectTimeout(time).setSocketTimeout(time).build();
	}

	public String dump(HttpEntity entity) throws IOException
	{
		return dump(entity, null);
	}

	/**
	 * 打印页面
	 * 
	 * @param entity
	 * @throws IOException
	 */
	public String dump(HttpEntity entity, String charset) throws IOException
	{
		if (charset == null)
		{
			Header h = entity.getContentEncoding();
			if (h != null)
			{
				charset = h.getValue();
			}
			if (charset == null)
			{
				Header ct = entity.getContentType();
				if (ct != null)
				{
					HeaderElement values[] = ct.getElements();
//					for (int i=0; i<values.length ;i++)
//					{
//						System.out.println(values[i]);
//					}
					
					if (values.length > 0)
					{
						NameValuePair param = values[0].getParameterByName("charset");
						if (param != null)
						{
							charset = param.getValue();
						}
					}
				}
			}
		}
		
		//System.out.println(charset);
		
		// System.out.println(charset);
		// String responseString = new
		// String(EntityUtils.toString(entity).getBytes("gbk"));
		// String html = EntityUtils.toString(entity, charset);
		// String html = new
		// String(EntityUtils.toString(entity).getBytes("ISO_8859_1"),charset);

		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(entity.getContent(), charset));
		// br.close();
		JFile file = new JFile(entity.getContent());
		//byte[] con = file.readAllBytes();
		//System.out.println(Format.getEncode(con));
		//String html = new String(con,"gbk");
		String html = file.readAllText(charset);

		return html;
	}

	public String getAgent()
	{
		return agent;
	}

	public HttpResponse execute(HttpPost post) throws ClientProtocolException,
					IOException
	{
		post.setConfig(requestconfig);
		return client.execute(post);
	}

	public CloseableHttpClient getClient()
	{
		return client;
	}
	
	private Map<String,String> procHead(Map<String,String> header)
	{
		if (header == null)
		{
			header = new HashMap<String,String>();
			header.put("User-Agent", agent);
		}
		
		String tagent = header.get("User-Agent");
		if (tagent == null)
		{
			header.put("User-Agent", agent);
		}
		else
		{
			agent = tagent;
		}
		String auth = header.get("Authorization");
		if (auth == null)
		{
			if (baseAuthorization != null)
			{
				header.put("Authorization", baseAuthorization);
			}
		}
		else
		{
			baseAuthorization = auth;
		}
		
		return header;
	}

	public HashMap<String, String> post(final String url,
					final HashMap<String, String> requst)
					throws ClientProtocolException, IOException
	{
		return post(url, requst, null);
	}

	public HashMap<String, String> post(final String url,
					final HashMap<String, String> request,
					final HashMap<String, String> header)
					throws ClientProtocolException, IOException
	{
		return post(url, request, header, null, null);
	}

	public HashMap<String, String> post(final String url,
					final HashMap<String, String> request,
					final HashMap<String, String> header,
					final HashMap<String, String> files)
					throws ClientProtocolException, IOException
	{
		return post(url, request, header, files, null);
	}

	public HashMap<String, String> post(final String url,
					final HashMap<String, String> request,
					final HashMap<String, String> header,
					final HashMap<String, String> files, final String localpath)
					throws ClientProtocolException, IOException
	{
		return post(url, request, header, files, localpath, null);
	}

	public HashMap<String, String> post(final String url,
					final Map<String, String> request,
					Map<String, String> header,
					final Map<String, String> files,
					final String localpath, String postchartset)
					throws ClientProtocolException, IOException
	{
		// List<NameValuePair> qparams
		HttpPost post = new HttpPost(url);
		post.setConfig(requestconfig);

		header = procHead(header);
		Iterator<Entry<String, String>> headfields = header.entrySet().iterator();
		while (headfields.hasNext())
		{
			Entry<String, String> e = headfields.next();
			post.setHeader(e.getKey(), e.getValue());
		}

		HttpResponse response;
		if (files != null)
		{
			// 文件上传
			// PostMethod postMethod = new PostMethod(url);
			// MultipartEntity multipartEntity = new MultipartEntity();
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			// 处理图片
			Iterator<Entry<String, String>> fileps = files.entrySet()
							.iterator();
			while (fileps.hasNext())
			{
				Entry<String, String> e = fileps.next();
				post.setHeader(e.getKey(), e.getValue());

				File file = new File(e.getValue());
				FileBody fb = new FileBody(file);
				builder.addPart(e.getKey(), fb);
			}

			if (request != null)
			{
				Iterator<Entry<String, String>> paramsfields = request.entrySet().iterator();
				while (paramsfields.hasNext())
				{
					Entry<String, String> e = paramsfields.next();
					builder.addTextBody(e.getKey(), e.getValue());
					// StringBody par = new StringBody(e.getValue());
					// builder.addPart(e.getKey(),par);
					// post.setHeader(e.getKey(), e.getValue());
					// System.out.println(e.getKey()+"#"+e.getValue());
					// parts[i++] = new
					// StringPart(e.getKey(),e.getValue(),"utf-8");
				}
			}

			post.setEntity(builder.build());
		}
		else
		{
			// 非文件上传
			String estr = null;
			
			if (request != null)
			{
				estr = request.get("");
			}
			
			if (postchartset == null)
			{
				postchartset = "utf-8";
			}
			
			if (estr == null)
			{
				List<NameValuePair> plist = new ArrayList<NameValuePair>();
				if (request != null)
				{
					Iterator<Entry<String, String>> paramsfields = request.entrySet().iterator();
					while (paramsfields.hasNext())
					{
						Entry<String, String> e = paramsfields.next();
						String v =  e.getValue();
						String[] vs = v.split(POSTSPLIT);
						for (String nv : vs)
						{
							if ("".equals(nv)==false)
							{
								plist.add(new BasicNameValuePair(e.getKey(), nv));
							}
						}
					}

					post.setEntity(new UrlEncodedFormEntity(plist, postchartset));
				}
			}
			else
			{
				post.setEntity(new StringEntity(estr,postchartset));
			}
		}

		HashMap<String, String> info = new HashMap<String, String>();
		response = client.execute(post);

		Header[] hs = response.getAllHeaders();
		for (Header h : hs)
		{
			info.put(h.getName(), h.getValue().trim());
		}

		info.put("code",""+response.getStatusLine().getStatusCode());
		HttpEntity entity = response.getEntity();
		if (localpath != null)
		{
			// String path = "/Users/Neo/Desktop/";
			// System.out.println(localpath);
			File storeFile = new File(localpath);
			FileOutputStream output = new FileOutputStream(storeFile);
			InputStream input = entity.getContent();
			byte b[] = new byte[1024];
			int j = 0;
			while ((j = input.read(b)) != -1)
			{
				output.write(b, 0, j);
			}
			output.flush();
			output.close();

			info.put("filepath", localpath);
		}
		else
		{
			info.put("html", dump(entity));
		}
		post.abort();

		return info;
	}

	public String postToString(final String url,
					final Map<String, String> request)
					throws ClientProtocolException, IOException
	{
		return postToString(url, request, null, null);
	}

	public String postToString(final String url,
					final Map<String, String> request,
					final Map<String, String> header)
					throws ClientProtocolException, IOException
	{
		return postToString(url, request, header, null);
	}

	public String postToString(final String url,
					final Map<String, String> request,
					final Map<String, String> header,
					final Map<String, String> files, String postcharset)
					throws ClientProtocolException, IOException
	{
		HashMap<String, String> info = post(url, request, header, files, null,
						postcharset);
		return info.get("html");
	}

	public String postToString(final String url,
					final Map<String, String> request,
					final Map<String, String> header,
					final Map<String, String> files)
					throws ClientProtocolException, IOException
	{
		return postToString(url, request, header, files, null);
	}

	public String get(final String url) throws IOException
	{
		HashMap<String, String> head = null;
		return get(url, head, null);
	}

	public String get(final String url, final String ref) throws IOException
	{
		HashMap<String, String> head = null;
		if (ref != null)
		{
			head = new HashMap<String, String>();
			head.put("Referer", ref);
		}

		// System.out.println(result);
		return get(url, head, null);
	}

	public String get(final String url, HashMap<String, String> head,
					final String chartset) throws IOException
	{
		return getPro(url, head, chartset).get("html");
	}

	public HashMap<String, String> getPro(final String url,
					Map<String, String> head, final String chartset)
					throws IOException
	{
		HashMap<String, String> result = new HashMap<String, String>();
		HttpGet get = new HttpGet(url);
		get.setConfig(requestconfig);

		head = procHead(head);
		Iterator<Entry<String, String>> headfields = head.entrySet().iterator();
		while (headfields.hasNext())
		{
			Entry<String, String> e = headfields.next();
			get.setHeader(e.getKey(), e.getValue());
		}
		// get.setHeader("Cookie", getCookieString());

		HttpResponse response = client.execute(get);
		Header[] hs = response.getAllHeaders();
		for (Header h : hs)
		{
			result.put(h.getName(), h.getValue().trim());
		}
		// System.out.println(response.getStatusLine());
		HttpEntity entity = response.getEntity();
		String html = dump(entity, chartset);
		result.put("code",""+response.getStatusLine().getStatusCode());
		result.put("html", html);
		get.abort();

		// System.out.println(result);
		return result;
	}
	
	public void close()
	{
		try
		{
			client.close();
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}

	public String getCookieString(String key, String domain)
	{
		StringBuffer buf = new StringBuffer();

		List<Cookie> clist = cookieStore.getCookies();
		for (Cookie cc : clist)
		{
			String name = cc.getName();
			String cdomain = cc.getDomain();

			if ((key == null || key.equals(name))
							&& cdomain.indexOf(domain) >= 0)
			{
				if (key == null)
				{
					buf.append(String.format("%s=%s; ", name, cc.getValue()));
				}
				else
				{
					buf.append(cc.getValue());
					break;
				}
			}
		}
		clist = null;

		return buf.toString();
	}

	public String getCookieString(String domain)
	{
		return getCookieString(null, domain);
	}

	public String getCookieString()
	{
		StringBuffer buf = new StringBuffer();
		List<Cookie> clist = cookieStore.getCookies();
		for (Cookie cc : clist)
		{
			buf.append(String.format("%s=%s; ", cc.getName(), cc.getValue()));
		}
		clist = null;

		return buf.toString();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		//EHttpClient c = new EHttpClient();
		//System.out.println(c.get("http://login.sina.com.cn/member/testify/testify.php?entry=account"));
		
		//System.out.println(c.get("http://woso100.com"));
		// EHttpClient c = new EHttpClient();
		// c.setProxy("127.0.0.1", 8087);
		// c.setProxy("121.196.129.91", 30002);
		// c.setProxy("101.66.251.53", 8087);
		// c.setProxy("106.185.26.64", 8798);

		// String url = "http://woso100.com/ip.jsp";
		// System.out.println(c.get(url));
		// JFile f = new JFile("/Users/Neo/Desktop/aaa.csv");
		// System.out.println(f.readAllText("utf-16le"));

		/*
		 * c.setProxy(null, 8087); System.out.println(c.get(url));
		 * 
		 * c.setProxy("127.0.0.1", 8087); System.out.println(c.get(url));
		 */

		// c.postToString(url, null);

		// System.out.println(c.get("http://woso100.com/t.php"));
		// System.out.println("-----------------");
		//
		// HashMap<String,String> head = new HashMap<String,String>();
		// head.put("User-Agent", WebAgent.getRandAgent());
		// System.out.println(c.postToString("http://woso100.com/t.php",
		// null,head));
		// System.out.println("-----------------");
		//
		//
		// System.out.println(c.get("http://woso100.com/t.php"));
	}

}
