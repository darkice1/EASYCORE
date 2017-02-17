/**
 * 
 */
package easy.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
@SuppressWarnings("deprecation")
public class EHttpClient
{
	private BasicCookieStore cookieStore = new BasicCookieStore();

	private HttpClientBuilder httpbuilder = HttpClients.custom();

	private CloseableHttpClient client;
	// private CloseableHttpClient client = HttpClients.custom().build();
	private String agent = WebAgent.getRandAgent();
	private RequestConfig requestconfig;
	private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
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
			baseAuthorization = String.format("Basic %s",Format.encodeBase64(String.format("%s:%s", name,passwd).getBytes()));
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}

	public void setProxyAuthorization(String name,String passwd)
	{
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(name, passwd));
		
		client = httpbuilder.setDefaultCredentialsProvider(credentialsProvider).build();
	}
	
	public void closeExpiredConnections()
	{
		connectionManager.closeExpiredConnections();
	}
	
	public void closeIdleConnections(long idleTimeout,TimeUnit tunit)
	{
		connectionManager.closeIdleConnections(idleTimeout, tunit);
	}
	
	private void init(final String host, final Integer port)
	{
		setProxy(host, port);

		try
		{
			connectionManager.setMaxTotal(1000);  
			connectionManager.setDefaultMaxPerRoute(200);  
			httpbuilder.setConnectionManager(connectionManager);
			
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
	
	public void clearCookie()
	{
		cookieStore.clear();
	}
	
	public static BasicCookieStore jsonToBasicCookieStore(JSONObject json)
	{
		BasicCookieStore cookieStore = new BasicCookieStore();
		
		JSONArray arr = json.getJSONArray("cookies");
		for (int i=0,len=arr.size();i<len;i++)
		{
			JSONObject cj = arr.getJSONObject(i);

			BasicClientCookie pc = new BasicClientCookie(cj.getString("name"), cj.getString("value"));
			pc.setDomain(cj.getString("domain"));
			pc.setPath(cj.getString("path"));
			//pc.setComment(cj.getString("comment"));
			
			Date d = null;
			JSONObject dj = cj.getJSONObject("expiryDate");
			if (dj != null && dj.isEmpty() == false)
			{
				d = new Date();
				d.setTime(dj.getLong("time"));
			}
			else
			{
				dj = cj.getJSONObject("expires");
				if (dj != null && dj.isEmpty() == false)
				{
					d = new Date();
					d.setTime(dj.getLong("time"));
				}				
			}
			

			
			pc.setExpiryDate(d);
			pc.setSecure(cj.getBoolean("secure"));
//			pc.setVersion(cj.getInt("version"));
//			c.setComment(comment);
			cookieStore.addCookie(pc);
		}
		
		return cookieStore;
	}
	
	public JSONObject getCookieStoreJson()
	{
		return JSONObject.fromObject(cookieStore);
	}
	
	public void setCookieStore(String jsonstr)
	{
		setCookieStore(jsonToBasicCookieStore(JSONObject.fromObject(jsonstr)));
	}
	
	public void setCookieStore(JSONObject json)
	{
		setCookieStore(jsonToBasicCookieStore(json));
	}
	
	public void setCookieStore(BasicCookieStore cookieStore)
	{
		List<Cookie> clist = cookieStore.getCookies();
		for (Cookie cc : clist)
		{
			this.cookieStore.addCookie(cc);
		}
	}

	public void addCookie(final String cookie, final String domain)
	{
		String t[] = cookie.split(";");
		for (String c : t)
		{
			c = c.trim();
			String ts[] = c.split("=",2);
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
		if (entity != null)
		{
			return dump(entity, null);
		}
		return null;
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

	/**
	 * @param agent the agent to set
	 */
	public void setAgent(String agent)
	{
		this.agent = agent;
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
//			if (proxyAuthorization != null)
//			{
//				header.put("Proxy-Authorization", proxyAuthorization);
//			}
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
				//FileBody fb = new FileBody(file);
				//System.out.println(fb.getContentType());
				//builder.addPart(e.getKey(), fb);
				//System.out.println(URLConnection.getFileNameMap().getContentTypeFor(localpath));
//				URL u = new URL();
				String ct =  URLConnection.getFileNameMap().getContentTypeFor(file.getAbsolutePath());
//				URLConnection uc = u.openConnection();  
//			    String ct = uc.getContentType();  
				if (ct == null)
				{
					String ext = Format.getFileExtName(file.getName());
					if ("png".equals(ext))
					{
						ct = "image/png";
					}
				}
				builder.addBinaryBody(e.getKey(), file, ContentType.create(ct), file.getName());
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
//							if ("".equals(nv)==false)
//							{
//								plist.add(new BasicNameValuePair(e.getKey(), nv));
//							}
							plist.add(new BasicNameValuePair(e.getKey(), nv));
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
			connectionManager.close();
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

//	/**
//	 * @param args
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException
//	{
//		EHttpClient ec = new EHttpClient();
//		//String html = ec.get("https://oauth.jd.com/oauth/authorize?response_type=code&client_id=73E703FFBB8441580C1768740CF3F1E9&redirect_uri=http://woso100.com");
//		//ec.addCookie("cn=2; ipLoc-djd=1-2805-2854-0.138160319; ipLocation=%u5317%u4EAC; areaId=1; mt_xid=V2_52007VwMUUFVfWlsXTxlsAW4CElddWQZGHklNXhliUEcCQVAHWRhVHV0AMlcVW15cAggaeRpdBWAfElJBWlNLH0wSXAVsARZiX2hRahtKH1wAYDMSVlw%3D; _jrda=3; 3AB9D23F7A4B3C9B=GMIG25XXVVUKH7SZ335VCMNDRGGYP3BRPIPNGUXBN4OKH5FR5JCEBK2NUSIS42VYY6V3BXMONNGC4DXJQQ3CVU6CSU; TrackID=1KfXup1Gp07HUO6RKTzHJbB1n1lUaEKzBTCe0xwj3F_S_cnDk5ClkxnJgM7UG1db7ctyEwhgg42aIvqtu3aKaGBZP2hGZrj-gqlo7GPO9IbCGCKpyNQtqJSJaidJjebOq; pinId=LjXN_5Na6XW1ffsfn98I-w; pin=darkice1; unick=darkice1; _tp=EuGvUUYZNcbcdhC8j3Z1yg%3D%3D; _pst=darkice1; ssid=\"cqfF2qxcR0u2Ew/Eayn8wQ==\"; unpl=V2_ZjNsbUtTQxd0CBUHcksPUWIKFFwRUBdBdAhEXHIaCAE3BhtcclRCFXIURldnGFwUZgsZXUNcQRNFCHZUeR5cB1czIl1BZ0IldQ5EXHwRXQ1hAyI%3D; mt_subsite=||1111%2C1480554638; __jdv=122270672|item.jd.com|t_13613_|tuiguang|851301bb8cbe4870b6ee013982e5a480|1480554640593; thor=DB7A1DA02FEB3BE8862D2E41F963BBF2882735E40A32BFDE0EAC15EFD4E965292DB318AF01447457222BAFBC7FDFE3A73D8B9ECD77EFA7B86F064E53FA7CB34E8E20BCB373904DA00E77F5FD4C99ECE882D3DA353F7C4ADE41B5BB30CDFAE02779CB550EE78C46FF35ACE72E5A8ACA36A26148AD89494CD019083DEDA77946305DF4E78C8024456428BB526CBD903C48; __jda=108460702.1739690971.1454555254.1480504399.1480554589.312; __jdb=108460702.7.1739690971|312.1480554589; __jdc=108460702; __jdu=1739690971; masterClose=yes", "www.woso100.com");
//		ec.addCookie("cn=2; ipLoc-djd=1-2805-2854-0.138160319; ipLocation=%u5317%u4EAC; areaId=1; mt_xid=V2_52007VwMUUFVfWlsXTxlsAW4CElddWQZGHklNXhliUEcCQVAHWRhVHV0AMlcVW15cAggaeRpdBWAfElJBWlNLH0wSXAVsARZiX2hRahtKH1wAYDMSVlw%3D; _jrda=3; 3AB9D23F7A4B3C9B=GMIG25XXVVUKH7SZ335VCMNDRGGYP3BRPIPNGUXBN4OKH5FR5JCEBK2NUSIS42VYY6V3BXMONNGC4DXJQQ3CVU6CSU; TrackID=1KfXup1Gp07HUO6RKTzHJbB1n1lUaEKzBTCe0xwj3F_S_cnDk5ClkxnJgM7UG1db7ctyEwhgg42aIvqtu3aKaGBZP2hGZrj-gqlo7GPO9IbCGCKpyNQtqJSJaidJjebOq; pinId=LjXN_5Na6XW1ffsfn98I-w; pin=darkice1; unick=darkice1; _tp=EuGvUUYZNcbcdhC8j3Z1yg%3D%3D; _pst=darkice1; ssid=\"cqfF2qxcR0u2Ew/Eayn8wQ==\"; unpl=V2_ZjNsbUtTQxd0CBUHcksPUWIKFFwRUBdBdAhEXHIaCAE3BhtcclRCFXIURldnGFwUZgsZXUNcQRNFCHZUeR5cB1czIl1BZ0IldQ5EXHwRXQ1hAyI%3D; mt_subsite=||1111%2C1480554638; __jdv=122270672|item.jd.com|t_13613_|tuiguang|851301bb8cbe4870b6ee013982e5a480|1480554640593; thor=DB7A1DA02FEB3BE8862D2E41F963BBF2882735E40A32BFDE0EAC15EFD4E965292DB318AF01447457222BAFBC7FDFE3A73D8B9ECD77EFA7B86F064E53FA7CB34E8E20BCB373904DA00E77F5FD4C99ECE882D3DA353F7C4ADE41B5BB30CDFAE02779CB550EE78C46FF35ACE72E5A8ACA36A26148AD89494CD019083DEDA77946305DF4E78C8024456428BB526CBD903C48; __jda=108460702.1739690971.1454555254.1480504399.1480554589.312; __jdb=108460702.7.1739690971|312.1480554589; __jdc=108460702; __jdu=1739690971; masterClose=yes", "media.jd.com");
//		
//		HashMap<String,String> post = new HashMap<String,String>();
//
//		HashMap<String,String> resp = ec.post("https://media.jd.com/gotoadv/getCustomCode/1", post);
//		//HashMap<String,String> resp = ec.post("http://www.woso100.com/ip.jsp", post);
//		
//		//System.out.println(JFile.loadHttpFile("https://media.jd.com/gotoadv/getCustomCode/1", "cn=2; ipLoc-djd=1-2805-2854-0.138160319; ipLocation=%u5317%u4EAC; areaId=1; mt_xid=V2_52007VwMUUFVfWlsXTxlsAW4CElddWQZGHklNXhliUEcCQVAHWRhVHV0AMlcVW15cAggaeRpdBWAfElJBWlNLH0wSXAVsARZiX2hRahtKH1wAYDMSVlw%3D; _jrda=3; 3AB9D23F7A4B3C9B=GMIG25XXVVUKH7SZ335VCMNDRGGYP3BRPIPNGUXBN4OKH5FR5JCEBK2NUSIS42VYY6V3BXMONNGC4DXJQQ3CVU6CSU; TrackID=1KfXup1Gp07HUO6RKTzHJbB1n1lUaEKzBTCe0xwj3F_S_cnDk5ClkxnJgM7UG1db7ctyEwhgg42aIvqtu3aKaGBZP2hGZrj-gqlo7GPO9IbCGCKpyNQtqJSJaidJjebOq; pinId=LjXN_5Na6XW1ffsfn98I-w; pin=darkice1; unick=darkice1; _tp=EuGvUUYZNcbcdhC8j3Z1yg%3D%3D; _pst=darkice1; ssid=\"cqfF2qxcR0u2Ew/Eayn8wQ==\"; unpl=V2_ZjNsbUtTQxd0CBUHcksPUWIKFFwRUBdBdAhEXHIaCAE3BhtcclRCFXIURldnGFwUZgsZXUNcQRNFCHZUeR5cB1czIl1BZ0IldQ5EXHwRXQ1hAyI%3D; mt_subsite=||1111%2C1480554638; __jdv=122270672|item.jd.com|t_13613_|tuiguang|851301bb8cbe4870b6ee013982e5a480|1480554640593; thor=DB7A1DA02FEB3BE8862D2E41F963BBF2882735E40A32BFDE0EAC15EFD4E965292DB318AF01447457222BAFBC7FDFE3A73D8B9ECD77EFA7B86F064E53FA7CB34E8E20BCB373904DA00E77F5FD4C99ECE882D3DA353F7C4ADE41B5BB30CDFAE02779CB550EE78C46FF35ACE72E5A8ACA36A26148AD89494CD019083DEDA77946305DF4E78C8024456428BB526CBD903C48; __jda=108460702.1739690971.1454555254.1480504399.1480554589.312; __jdb=108460702.7.1739690971|312.1480554589; __jdc=108460702; __jdu=1739690971; masterClose=yes", null, null, null));
//		
//		System.out.println(ec.getCookieStoreJson());
//		System.out.println(ec.getCookieString());
//		
//		System.out.println(resp);
//	}
	
//	public static void main(String[] args) throws IOException
//	{
//		String s = "{\"version\":\"1.0\",\"pid\":\"179543\",\"action_type\":1,\"nativead\":{\"required_fields\":[\"4\"],\"title_max_safe_length\":25,\"desc_max_safe_length\":90,\"source_max_safe_length\":10,\"img_width\":1000,\"img_height\":500,\"img_num\":1,\"logo_width\":30,\"logo_height\":30},\"device\":{\"devicetype\":0,\"os\":0,\"imei_md5\":\"F1C7976BC455CB548BFC550EB7687F06\",\"m_ip\":\"14.18.52.69\",\"m_ua\":\"Mozilla/5.0(Linux;Android4.0.4;GT-I9220 Build/IMM76D)\",\"m_ts\":\"1374225975\"},\"app\":{\"m_app\":\"wantu\",\"m_app_pn\":\"com.weitu.wantu\"},\"reqid\":\"179543\"}";
//
//
//		HashMap<String,String> request = new HashMap<String,String>();
//					request.put("", s);
//					
//					HashMap<String,String> header = new HashMap<String,String>();
//					header.put("Content-Type", "application/json");
//					
//					EHttpClient client = new EHttpClient();
//					//System.out.println(client.post("http://s.x.cn.xtgreat.com/bx?l=179543", request, header));
//					System.out.println(client.post("http://s.x.cn.xtgreat.com/cx", request, header));
//	}

}
