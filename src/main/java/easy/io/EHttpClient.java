package easy.io;

import easy.model.WebAgent;
import easy.util.Format;
import easy.util.Log;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author Neo(starneo@gmail.com)2013-11-12
 */
@SuppressWarnings("deprecation")
public class EHttpClient
{
	private final BasicCookieStore cookieStore = new BasicCookieStore();

	private final HttpClientBuilder httpbuilder = HttpClients.custom();

	private CloseableHttpClient client;
	// private CloseableHttpClient client = HttpClients.custom().build();
	private String agent = WebAgent.getRandAgent();
	private RequestConfig requestconfig;
	private PoolingHttpClientConnectionManager connectionManager = null;
	private String baseAuthorization = null;

	public final static String POSTSPLIT = new String(new char[] { 0, 9 });

	// private HttpClient client = HttpsClient.getInstance();

	public EHttpClient()
	{
		init(null, null);
	}

	@SuppressWarnings("unused")
	public EHttpClient(final String host, final Integer port)
	{
		init(host, port);
	}

	public void setProxy(final String host, final Integer port)
	{
		if (host != null && !host.isEmpty() && port != null)
		{
			// System.out.println(host+"##"+port);
			HttpHost proxy = new HttpHost(host, port);
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
					proxy);
			httpbuilder.setRoutePlanner(routePlanner);
			client = httpbuilder.build();

		}
	}

	public void setBaseAuthorization(String name, String passwd)
	{
		baseAuthorization = String.format("Basic %s", Format.encodeBase64(
				String.format("%s:%s", name, passwd).getBytes()));
	}

	public void setProxyAuthorization(String name, String passwd)
	{
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(name, passwd));

		client = httpbuilder.setDefaultCredentialsProvider(credentialsProvider)
				.build();
	}

	public void closeExpiredConnections()
	{
		connectionManager.closeExpiredConnections();
	}

	public void closeIdleConnections(long idleTimeout, TimeUnit tunit)
	{
		connectionManager.closeIdleConnections(idleTimeout, tunit);
	}

	@SuppressWarnings("unchecked")
	private void init(final String host, final Integer port)
	{
		setProxy(host, port);

		try
		{
			SSLContext sslContext  = SSLContexts.custom().loadTrustMaterial(null, (arg0, arg1) -> true).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sslContext, new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			@SuppressWarnings("rawtypes")
			Registry registry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslsf).build();

			connectionManager = new PoolingHttpClientConnectionManager(registry);
			connectionManager.setMaxTotal(1000);
			connectionManager.setDefaultMaxPerRoute(200);
			httpbuilder.setConnectionManager(connectionManager);

			httpbuilder.setDefaultCookieStore(cookieStore);
//			httpbuilder.setSSLSocketFactory(sslsf);

			client = httpbuilder.build();
		}
		catch (Exception e)
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
		for (int i = 0, len = arr.size(); i < len; i++)
		{
			JSONObject cj = arr.getJSONObject(i);

			// System.out.println(cj.getString("name")+"
			// "+cj.getString("value"));
			BasicClientCookie pc = new BasicClientCookie(cj.getString("name"),
					cj.getString("value"));

			// String domain = cj.getString("domain");

			// if (domain.indexOf(".") == 0)
			// {
			// domain = domain.substring(1);
			// }

			// domain = ".alimama.com";
			pc.setDomain(cj.getString("domain"));
			pc.setPath(cj.getString("path"));
			pc.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
			// cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
			// pc.setComment(cj.getString("comment"));

			Date d = null;
			JSONObject dj = cj.getJSONObject("expiryDate");
			if (dj != null && !dj.isEmpty())
			{
				d = new Date();
				d.setTime(dj.getLong("time"));
			}
			else
			{
				dj = cj.getJSONObject("expires");
				if (dj != null && !dj.isEmpty())
				{
					d = new Date();
					d.setTime(dj.getLong("time"));
				}
			}

			pc.setExpiryDate(d);
			pc.setSecure(cj.getBoolean("secure"));
			// pc.setVersion(cj.getInt("version"));
			// c.setComment(comment);
			cookieStore.addCookie(pc);
		}

		// System.out.println(cookieStore);
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
		String[] t = cookie.split(";");
		for (String c : t)
		{
			c = c.trim();
			String[] ts = c.split("=", 2);
			if (ts.length >= 2)
			{
				// System.out.println(ts[0]+" "+ts[1]);
				BasicClientCookie pc = new BasicClientCookie(ts[0], ts[1]);
				pc.setDomain(domain);
				pc.setPath("/");
				pc.setAttribute(ClientCookie.DOMAIN_ATTR, "true");

				cookieStore.addCookie(pc);

			}

			// Log.OutLog("%s=%s; ",ts[0], ts[1]);

		}
	}

	public void setConnectionTimeout(final int time)
	{
		// 设置请求和传输超时时间
		requestconfig = RequestConfig.custom().setConnectionRequestTimeout(time)
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


	public String dump(HttpEntity entity, String charset) throws IOException
	{
		String html = null;
		if (entity != null)
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
						HeaderElement[] values = ct.getElements();
						// for (int i=0; i<values.length ;i++)
						// {
						// System.out.println(values[i]);
						// }

						if (values.length > 0)
						{
							NameValuePair param = values[0]
									.getParameterByName("charset");
							if (param != null)
							{
								charset = param.getValue();
							}
						}
					}
				}
			}

			JFile file = new JFile(entity.getContent());

			html = file.readAllText(charset);
		}

		// System.out.println(charset);

		// System.out.println(charset);
		// String responseString = new
		// String(EntityUtils.toString(entity).getBytes("gbk"));
		// String html = EntityUtils.toString(entity, charset);
		// String html = new
		// String(EntityUtils.toString(entity).getBytes("ISO_8859_1"),charset);

		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(entity.getContent(), charset));
		// br.close();

		return html;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(String agent)
	{
		this.agent = agent;
	}

	public String getAgent()
	{
		return agent;
	}

	public HttpResponse execute(HttpPost post)
			throws IOException
	{
		post.setConfig(requestconfig);
		return client.execute(post);
	}

	public CloseableHttpClient getClient()
	{
		return client;
	}

	private Map<String, String> procHead(Map<String, String> header)
	{
		if (header == null)
		{
			header = new HashMap<>();
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
			// if (proxyAuthorization != null)
			// {
			// header.put("Proxy-Authorization", proxyAuthorization);
			// }
		}
		else
		{
			baseAuthorization = auth;
		}

		return header;
	}

	public HashMap<String, String> post(final String url,
			final HashMap<String, String> requst)
			throws IOException
	{
		return post(url, requst, null);
	}

	public HashMap<String, String> post(final String url,
			final Map<String, String> request,
			final Map<String, String> header)
			throws IOException
	{
		return post(url, request, header, null, null);
	}

	public HashMap<String, String> post(final String url,
			final HashMap<String, String> request,
			final HashMap<String, String> header,
			final HashMap<String, String> files)
			throws IOException
	{
		return post(url, request, header, files, null);
	}

	public HashMap<String, String> post(final String url,
			final Map<String, String> request,
			final Map<String, String> header,
			final Map<String, String> files, final String localpath)
			throws IOException
	{
		return post(url, request, header, files, localpath, null);
	}

	public HashMap<String, String> post(final String url,
			final Map<String, String> request, Map<String, String> header,
			final Map<String, String> files, final String localpath,
			String postchartset) throws IOException
	{
		// List<NameValuePair> qparams
		HttpPost post = new HttpPost(url);
		post.setConfig(requestconfig);

		header = procHead(header);
		for (Entry<String, String> e : header.entrySet())
		{
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
			for (Entry<String, String> e : files.entrySet())
			{
				post.setHeader(e.getKey(), e.getValue());

				File file = new File(e.getValue());
				// FileBody fb = new FileBody(file);
				// System.out.println(fb.getContentType());
				// builder.addPart(e.getKey(), fb);
				// System.out.println(URLConnection.getFileNameMap().getContentTypeFor(localpath));
				// URL u = new URL();
				String ct = URLConnection.getFileNameMap().getContentTypeFor(file.getAbsolutePath());
				// URLConnection uc = u.openConnection();
				// String ct = uc.getContentType();
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
				for (Entry<String, String> e : request.entrySet())
				{
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
				List<NameValuePair> plist = new ArrayList<>();
				if (request != null)
				{
					for (Entry<String, String> e : request.entrySet())
					{
						String v = e.getValue();
						String[] vs = v.split(POSTSPLIT);
						for (String nv : vs)
						{
							// if ("".equals(nv)==false)
							// {
							// plist.add(new BasicNameValuePair(e.getKey(),
							// nv));
							// }
							plist.add(new BasicNameValuePair(e.getKey(), nv));
						}
					}

					post.setEntity(
							new UrlEncodedFormEntity(plist, postchartset));
				}
			}
			else
			{
				post.setEntity(new StringEntity(estr, postchartset));
			}
		}

		HashMap<String, String> info = new HashMap<>();
		response = client.execute(post);

		Header[] hs = response.getAllHeaders();
		for (Header h : hs)
		{
			info.put(h.getName(), h.getValue().trim());
		}

		info.put("code", "" + response.getStatusLine().getStatusCode());
		HttpEntity entity = response.getEntity();
		if (localpath != null)
		{
			// String path = "/Users/Neo/Desktop/";
			// System.out.println(localpath);
			File storeFile = new File(localpath);
			FileOutputStream output = new FileOutputStream(storeFile);
			InputStream input = entity.getContent();
			byte[] b = new byte[1024];
			int j;
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
			throws IOException
	{
		return postToString(url, request, null, null);
	}

	public String postToString(final String url,
			final Map<String, String> request, final Map<String, String> header)
			throws IOException
	{
		return postToString(url, request, header, null);
	}

	public String postToString(final String url,
			final Map<String, String> request, final Map<String, String> header,
			final Map<String, String> files, String postcharset)
			throws IOException
	{
		HashMap<String, String> info = post(url, request, header, files, null,
				postcharset);
		return info.get("html");
	}

	public String postToString(final String url,
			final Map<String, String> request, final Map<String, String> header,
			final Map<String, String> files)
			throws IOException
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
			head = new HashMap<>();
			head.put("Referer", ref);
		}

		// System.out.println(result);
		return get(url, head, null);
	}

	public String get(final String url, Map<String, String> head,
			final String chartset) throws IOException
	{
		return getPro(url, head, chartset).get("html");
	}

	public HashMap<String, String> getPro(final String url,
			Map<String, String> head, final String chartset) throws IOException
	{
		return getPro(url,head,chartset,null);
	}

	public HashMap<String, String> getPro(final String url,
			Map<String, String> head, final String chartset,String localpath) throws IOException
	{
		HashMap<String, String> result = new HashMap<>();
		HttpGet get = new HttpGet(url);
		get.setConfig(requestconfig);

		head = procHead(head);
		for (Entry<String, String> e : head.entrySet())
		{
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
		if (localpath == null)
		{
			String html = dump(entity, chartset);
			result.put("code", "" + response.getStatusLine().getStatusCode());
			result.put("html", html);
			get.abort();			
		}
		else
		{
			File storeFile = new File(localpath);    
            FileOutputStream output = new FileOutputStream(storeFile);  
            //得到网络资源并写入文件  
            InputStream input = entity.getContent();
			byte[] b = new byte[1024];
            int j;
            while( (j = input.read(b))!=-1){  
                output.write(b,0,j);  
            }  
            output.flush();  
            output.close();   
            
//			EntityUtils.toByteArray(response.getEntity());
			get.abort();			
		}

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
		StringBuilder buf = new StringBuilder();

		List<Cookie> clist = cookieStore.getCookies();
		for (Cookie cc : clist)
		{
			String name = cc.getName();
			String cdomain = cc.getDomain();

			if ((key == null || key.equals(name))
					&& cdomain.contains(domain))
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

		return buf.toString();
	}

	public String getCookieString(String domain)
	{
		return getCookieString(null, domain);
	}

	public String getCookieString()
	{
		StringBuilder buf = new StringBuilder();
		List<Cookie> clist = cookieStore.getCookies();
		for (Cookie cc : clist)
		{
			buf.append(String.format("%s=%s; ", cc.getName(), cc.getValue()));
		}

		return buf.toString();
	}
	
//	 public static void main(String[] args)
//	 {
//		EHttpClient client = new EHttpClient();
//		String localpath = String.format("%stmp_uucode_%d_%f.jpg", System.getProperty("java.io.tmpdir"),System.currentTimeMillis(),Math.random());
//
//		try
//		{
//			client.get("https://publisher.rgyun.com/");
//
//			System.out.println(localpath);
////			HashMap<String,String> head = new HashMap<>();
////			head.put("Referer", "https://publisher.rgyun.com/auth/login.htm");
//			System.out.println(client.getPro("https://publisher.rgyun.com/auth/checkcode.rest",null,null,localpath));
//		}
//		catch (IOException e)
//		{
//			Log.OutException(e);
//		}
//	 }

	// /**
	// * @param args
	// * @throws IOException
	// */
	// public static void main(String[] args) throws IOException
	// {
	// EHttpClient ec = new EHttpClient();
	// //String html =
	// ec.get("https://oauth.jd.com/oauth/authorize?response_type=code&client_id=73E703FFBB8441580C1768740CF3F1E9&redirect_uri=http://woso100.com");
	// //ec.addCookie("cn=2; ipLoc-djd=1-2805-2854-0.138160319;
	// ipLocation=%u5317%u4EAC; areaId=1;
	// mt_xid=V2_52007VwMUUFVfWlsXTxlsAW4CElddWQZGHklNXhliUEcCQVAHWRhVHV0AMlcVW15cAggaeRpdBWAfElJBWlNLH0wSXAVsARZiX2hRahtKH1wAYDMSVlw%3D;
	// _jrda=3;
	// 3AB9D23F7A4B3C9B=GMIG25XXVVUKH7SZ335VCMNDRGGYP3BRPIPNGUXBN4OKH5FR5JCEBK2NUSIS42VYY6V3BXMONNGC4DXJQQ3CVU6CSU;
	// TrackID=1KfXup1Gp07HUO6RKTzHJbB1n1lUaEKzBTCe0xwj3F_S_cnDk5ClkxnJgM7UG1db7ctyEwhgg42aIvqtu3aKaGBZP2hGZrj-gqlo7GPO9IbCGCKpyNQtqJSJaidJjebOq;
	// pinId=LjXN_5Na6XW1ffsfn98I-w; pin=darkice1; unick=darkice1;
	// _tp=EuGvUUYZNcbcdhC8j3Z1yg%3D%3D; _pst=darkice1;
	// ssid=\"cqfF2qxcR0u2Ew/Eayn8wQ==\";
	// unpl=V2_ZjNsbUtTQxd0CBUHcksPUWIKFFwRUBdBdAhEXHIaCAE3BhtcclRCFXIURldnGFwUZgsZXUNcQRNFCHZUeR5cB1czIl1BZ0IldQ5EXHwRXQ1hAyI%3D;
	// mt_subsite=||1111%2C1480554638;
	// __jdv=122270672|item.jd.com|t_13613_|tuiguang|851301bb8cbe4870b6ee013982e5a480|1480554640593;
	// thor=DB7A1DA02FEB3BE8862D2E41F963BBF2882735E40A32BFDE0EAC15EFD4E965292DB318AF01447457222BAFBC7FDFE3A73D8B9ECD77EFA7B86F064E53FA7CB34E8E20BCB373904DA00E77F5FD4C99ECE882D3DA353F7C4ADE41B5BB30CDFAE02779CB550EE78C46FF35ACE72E5A8ACA36A26148AD89494CD019083DEDA77946305DF4E78C8024456428BB526CBD903C48;
	// __jda=108460702.1739690971.1454555254.1480504399.1480554589.312;
	// __jdb=108460702.7.1739690971|312.1480554589; __jdc=108460702;
	// __jdu=1739690971; masterClose=yes", "www.woso100.com");
	// ec.addCookie("cn=2; ipLoc-djd=1-2805-2854-0.138160319;
	// ipLocation=%u5317%u4EAC; areaId=1;
	// mt_xid=V2_52007VwMUUFVfWlsXTxlsAW4CElddWQZGHklNXhliUEcCQVAHWRhVHV0AMlcVW15cAggaeRpdBWAfElJBWlNLH0wSXAVsARZiX2hRahtKH1wAYDMSVlw%3D;
	// _jrda=3;
	// 3AB9D23F7A4B3C9B=GMIG25XXVVUKH7SZ335VCMNDRGGYP3BRPIPNGUXBN4OKH5FR5JCEBK2NUSIS42VYY6V3BXMONNGC4DXJQQ3CVU6CSU;
	// TrackID=1KfXup1Gp07HUO6RKTzHJbB1n1lUaEKzBTCe0xwj3F_S_cnDk5ClkxnJgM7UG1db7ctyEwhgg42aIvqtu3aKaGBZP2hGZrj-gqlo7GPO9IbCGCKpyNQtqJSJaidJjebOq;
	// pinId=LjXN_5Na6XW1ffsfn98I-w; pin=darkice1; unick=darkice1;
	// _tp=EuGvUUYZNcbcdhC8j3Z1yg%3D%3D; _pst=darkice1;
	// ssid=\"cqfF2qxcR0u2Ew/Eayn8wQ==\";
	// unpl=V2_ZjNsbUtTQxd0CBUHcksPUWIKFFwRUBdBdAhEXHIaCAE3BhtcclRCFXIURldnGFwUZgsZXUNcQRNFCHZUeR5cB1czIl1BZ0IldQ5EXHwRXQ1hAyI%3D;
	// mt_subsite=||1111%2C1480554638;
	// __jdv=122270672|item.jd.com|t_13613_|tuiguang|851301bb8cbe4870b6ee013982e5a480|1480554640593;
	// thor=DB7A1DA02FEB3BE8862D2E41F963BBF2882735E40A32BFDE0EAC15EFD4E965292DB318AF01447457222BAFBC7FDFE3A73D8B9ECD77EFA7B86F064E53FA7CB34E8E20BCB373904DA00E77F5FD4C99ECE882D3DA353F7C4ADE41B5BB30CDFAE02779CB550EE78C46FF35ACE72E5A8ACA36A26148AD89494CD019083DEDA77946305DF4E78C8024456428BB526CBD903C48;
	// __jda=108460702.1739690971.1454555254.1480504399.1480554589.312;
	// __jdb=108460702.7.1739690971|312.1480554589; __jdc=108460702;
	// __jdu=1739690971; masterClose=yes", "media.jd.com");
	//
	// HashMap<String,String> post = new HashMap<String,String>();
	//
	// HashMap<String,String> resp =
	// ec.post("https://media.jd.com/gotoadv/getCustomCode/1", post);
	// //HashMap<String,String> resp = ec.post("http://www.woso100.com/ip.jsp",
	// post);
	//
	// //System.out.println(JFile.loadHttpFile("https://media.jd.com/gotoadv/getCustomCode/1",
	// "cn=2; ipLoc-djd=1-2805-2854-0.138160319; ipLocation=%u5317%u4EAC;
	// areaId=1;
	// mt_xid=V2_52007VwMUUFVfWlsXTxlsAW4CElddWQZGHklNXhliUEcCQVAHWRhVHV0AMlcVW15cAggaeRpdBWAfElJBWlNLH0wSXAVsARZiX2hRahtKH1wAYDMSVlw%3D;
	// _jrda=3;
	// 3AB9D23F7A4B3C9B=GMIG25XXVVUKH7SZ335VCMNDRGGYP3BRPIPNGUXBN4OKH5FR5JCEBK2NUSIS42VYY6V3BXMONNGC4DXJQQ3CVU6CSU;
	// TrackID=1KfXup1Gp07HUO6RKTzHJbB1n1lUaEKzBTCe0xwj3F_S_cnDk5ClkxnJgM7UG1db7ctyEwhgg42aIvqtu3aKaGBZP2hGZrj-gqlo7GPO9IbCGCKpyNQtqJSJaidJjebOq;
	// pinId=LjXN_5Na6XW1ffsfn98I-w; pin=darkice1; unick=darkice1;
	// _tp=EuGvUUYZNcbcdhC8j3Z1yg%3D%3D; _pst=darkice1;
	// ssid=\"cqfF2qxcR0u2Ew/Eayn8wQ==\";
	// unpl=V2_ZjNsbUtTQxd0CBUHcksPUWIKFFwRUBdBdAhEXHIaCAE3BhtcclRCFXIURldnGFwUZgsZXUNcQRNFCHZUeR5cB1czIl1BZ0IldQ5EXHwRXQ1hAyI%3D;
	// mt_subsite=||1111%2C1480554638;
	// __jdv=122270672|item.jd.com|t_13613_|tuiguang|851301bb8cbe4870b6ee013982e5a480|1480554640593;
	// thor=DB7A1DA02FEB3BE8862D2E41F963BBF2882735E40A32BFDE0EAC15EFD4E965292DB318AF01447457222BAFBC7FDFE3A73D8B9ECD77EFA7B86F064E53FA7CB34E8E20BCB373904DA00E77F5FD4C99ECE882D3DA353F7C4ADE41B5BB30CDFAE02779CB550EE78C46FF35ACE72E5A8ACA36A26148AD89494CD019083DEDA77946305DF4E78C8024456428BB526CBD903C48;
	// __jda=108460702.1739690971.1454555254.1480504399.1480554589.312;
	// __jdb=108460702.7.1739690971|312.1480554589; __jdc=108460702;
	// __jdu=1739690971; masterClose=yes", null, null, null));
	//
	// System.out.println(ec.getCookieStoreJson());
	// System.out.println(ec.getCookieString());
	//
	// System.out.println(resp);
	// }

	// public static void main(String[] args) throws IOException
	// {
	// String s =
	// "{\"version\":\"1.0\",\"pid\":\"179543\",\"action_type\":1,\"nativead\":{\"required_fields\":[\"4\"],\"title_max_safe_length\":25,\"desc_max_safe_length\":90,\"source_max_safe_length\":10,\"img_width\":1000,\"img_height\":500,\"img_num\":1,\"logo_width\":30,\"logo_height\":30},\"device\":{\"devicetype\":0,\"os\":0,\"imei_md5\":\"F1C7976BC455CB548BFC550EB7687F06\",\"m_ip\":\"14.18.52.69\",\"m_ua\":\"Mozilla/5.0(Linux;Android4.0.4;GT-I9220
	// Build/IMM76D)\",\"m_ts\":\"1374225975\"},\"app\":{\"m_app\":\"wantu\",\"m_app_pn\":\"com.weitu.wantu\"},\"reqid\":\"179543\"}";
	//
	//
	// HashMap<String,String> request = new HashMap<String,String>();
	// request.put("", s);
	//
	// HashMap<String,String> header = new HashMap<String,String>();
	// header.put("Content-Type", "application/json");
	//
	// EHttpClient client = new EHttpClient();
	// //System.out.println(client.post("http://s.x.cn.xtgreat.com/bx?l=179543",
	// request, header));
	// System.out.println(client.post("http://s.x.cn.xtgreat.com/cx", request,
	// header));
	// }
//	public static void main (String args[])
//	{
//		EHttpClient client = new EHttpClient();
//		
//		try
//		{
//			System.out.println(client.get("https://www.zbjuran.com/quweitupian/list_2_2.html"));
//		}
//		catch (IOException e)
//		{
//			Log.OutException(e);
//		}
//	}

}
