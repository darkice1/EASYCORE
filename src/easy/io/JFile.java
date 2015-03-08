package easy.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import easy.config.Config;
import easy.net.Proxy;
import easy.util.Format;
import easy.util.Log;

/**
 * <p>
 * <i>Copyright: youhow.net(c) 2005-2011</i>
 * </p>
 * JFile
 * 
 * @version 1.0 (<i>2005-7-4 neo(starneo@gmail.com)</i>)
 */

public class JFile
{
	// Reader r = new InputStreamReader(new
	// BufferedInputStream(uc.getInputStream ()));
	private String filename;
	private InputStream in;
	private BufferedWriter out;

	private final static String USER_AGENT = "User-Agent";
	private final static String REFERER = "Referer";

	private final static String COOKIE = "cookie";
	private final static String USER_AGENT_VALUE = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0)";

	private final static boolean GAEOPEN = "Y".equals(Config.getProperty(
					"GAEOPEN", "Y"));

	public JFile(String p_filename)
	{
		this(p_filename, false);
	}

	public static boolean exists(String filename)
	{
		File f = new File(filename);
		boolean b = f.exists();
		return b;
	}

	public static boolean delete(String filename)
	{
		File f = new File(filename);
		boolean b = f.delete();
		return b;
	}

	public JFile(String p_filename, boolean append)
	{
		filename = new String(p_filename);

		if (append)
		{
			try
			{
				out = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(
												filename, append), "UTF-8"));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public JFile(InputStream p_in)
	{
		in = p_in;
	}

	public List<String> getLineList()
	{
		return getLineList(null);
	}

	public List<String> getLineList(final String charset)
	{
		try
		{
			BufferedReader p_in = null;
			if (filename != null)
			{
				if (charset == null)
				{
					p_in = new BufferedReader(new FileReader(filename));
				}
				else
				{
					p_in = new BufferedReader(new InputStreamReader(
									new FileInputStream(filename), charset));
				}
			}
			else if (in != null)
			{
				if (charset == null)
				{
					p_in = new BufferedReader(new InputStreamReader(in));
				}
				else
				{
					p_in = new BufferedReader(
									new InputStreamReader(in, charset));
				}
			}

			if (p_in != null)
			{
				List<String> list = new ArrayList<String>();

				String s = p_in.readLine();
				while (s != null)
				{
					list.add(s);
					s = p_in.readLine();
				}
				p_in.close();
				return list;
			}
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String readAllText()
	{
		return readAllText(null);
	}

	public byte[] readAllBytes()
	{
		byte[] content = null;
		try
		{
			BufferedInputStream p_in = null;
			if (filename != null)
			{
				p_in = new BufferedInputStream(new FileInputStream(filename));
			}
			else if (in != null)
			{
				p_in = new BufferedInputStream(in);
			}

			 ByteArrayOutputStream out = new ByteArrayOutputStream(1024);        
		       
			 //System.out.println("Available bytes:" + in.available());        
		       
			 byte[] temp = new byte[1024];        
			 int size = 0;        
			 while ((size = p_in.read(temp)) != -1) 
			 {        
				 out.write(temp, 0, size);        
			}     
		    
			content = out.toByteArray();
		}
		catch (SocketException ex)
		{
			Log.OutException(ex);
			return null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		
		return content;
	}

	public String readAllText(String chartset)
	{
		String con = null;

		byte[] buf = readAllBytes();
		//System.out.println("chartset1"+chartset);
		if (chartset == null)
		{
			chartset = Format.getChartset(buf);
		}
		//System.out.println("chartset2"+chartset);

		if (chartset == null)
		{
			con = new String(buf);
		}
		else
		{
			try
			{
				con = new String(buf,chartset);
			}
			catch (UnsupportedEncodingException e)
			{
				Log.OutException(e);
			}
		}
		
		return con;
	}

	/**
	 * 写入对象
	 * 
	 * @param file
	 *            写入文件名
	 * @param obj
	 *            写入对象
	 * @throws IOException
	 */
	public static void writeObject(String file, Object obj) throws IOException
	{
		FileOutputStream fs = new FileOutputStream(file);
		writeObject(fs, obj);

		fs.close();
	}

	public static void writeObject(OutputStream out, Object obj)
					throws IOException
	{
		ObjectOutputStream os = new ObjectOutputStream(
						new BufferedOutputStream(out));
		os.writeObject(obj);
		os.reset();
		os.flush();

		os.close();
		os = null;
	}

	public static void writeGZipObject(String file, Object obj)
					throws IOException
	{
		FileOutputStream fis = new FileOutputStream(file);
		writeGZipObject(fis, obj);
		fis.close();
		fis = null;
	}

	public static void writeGZipObject(OutputStream out, Object obj)
					throws IOException
	{
		GZIPOutputStream zfs = new GZIPOutputStream(out);
		writeObject(zfs, obj);

		zfs.close();
		zfs = null;
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 *            读取文件名
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readObject(InputStream in)
					throws ClassNotFoundException, IOException
	{
		ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
						in));
		// ois.reset();
		Object o = null;
		try
		{
			o = ois.readObject();
		}
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		ois.close();
		return o;
	}

	public static Object readObject(String file) throws ClassNotFoundException,
					IOException
	{
		FileInputStream fis = new FileInputStream(file);

		Object o = readObject(fis);
		fis.close();

		return o;
	}

	public static Object readGZipObject(String file)
					throws ClassNotFoundException, IOException
	{
		FileInputStream fis = new FileInputStream(file);
		Object o = readGZipObject(fis);
		fis.close();

		return o;
	}

	public static Object readGZipObject(InputStream in)
					throws ClassNotFoundException, IOException
	{
		GZIPInputStream zis = new GZIPInputStream(in);

		Object o = readObject(zis);
		zis.close();

		return o;
	}

	public void WriteText(String p_str)
	{
		WriteText(p_str, null);
	}

	public void WriteText(String p_str, String chartset)
	{
		if (out != null)
		{
			try
			{
				out.write(new String(p_str.getBytes()));

				out.flush();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			if (filename != null)
			{
				try
				{
					// p_in = new BufferedReader(new InputStreamReader(in,
					// chartset));
					BufferedWriter out;
					if (chartset == null)
					{
						out = new BufferedWriter(new OutputStreamWriter(
										new FileOutputStream(filename)));
					}
					else
					{
						out = new BufferedWriter(new OutputStreamWriter(
										new FileOutputStream(filename),
										chartset));
					}
					out.write(p_str);
					out.close();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	public String getFilename()
	{
		return (this.filename);
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public InputStream getIn()
	{
		return (this.in);
	}

	public void setIn(InputStream in)
	{
		this.in = in;
	}

	public static String loadGaeFile(String url) throws ConnectException,
					IOException
	{
		return loadGaeFile(url, null, null, null, null);
	}

	public static String loadGaeFile(String url, String cookie,
					String useragent, String chartset, String ref)
					throws ConnectException, IOException
	{
		String u;
		if (GAEOPEN)
		{
			u = Format.getGaeURL(url);
		}
		else
		{
			u = url;
		}
		return loadHttpFile(u, cookie, useragent, chartset, ref);
	}

	public static String loadGaeZipFile(String url, String cookie,
					String useragent, String ref) throws ConnectException,
					IOException
	{
		String u = url;
		String html;

		if (GAEOPEN)
		{
			u = Format.getGaeURL(url);
			html = (String) loadHttpGZipObject(u, cookie, useragent, ref);
		}
		else
		{
			html = loadHttpFile(u, cookie, useragent, null, ref);
		}

		return html;
	}

	public static String loadHttpFile(String url) throws ConnectException,
					IOException
	{
		return loadHttpFile(url, null, null, null, null);
	}

	public static Object loadHttpObject(String url) throws ConnectException,
					IOException
	{
		return loadHttpObject(url, null, null);
	}

	public static Object loadHttpObject(String url, String cookie,
					String useragent) throws ConnectException, IOException
	{
		Proxy.initCfgProxy();
		String httpstr = null;
		try
		{
			URL u = new URL(url);

			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);// POST
			if (useragent == null)
			{
				uc.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
			}
			else
			{
				uc.setRequestProperty(USER_AGENT, useragent);
			}

			if (cookie != null)
			{
				uc.setRequestProperty(COOKIE, cookie);
			}

			Object o;
			try
			{

				InputStream in = uc.getInputStream();
				o = readObject(in);
				in.close();
			}
			catch (SocketException e)
			{
				throw e;
			}

			return o;
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return httpstr;
		}
	}

	public static Object loadHttpGZipObject(String url)
					throws ConnectException, IOException
	{
		return loadHttpGZipObject(url, null, null);
	}

	public static Object loadHttpGZipObject(String url, String cookie,
					String useragent) throws ConnectException, IOException
	{
		return loadHttpGZipObject(url, cookie, useragent, null);
	}

	public static Object loadHttpGZipObject(String url, String cookie,
					String useragent, String ref) throws IOException
	{
		Proxy.initCfgProxy();
		String httpstr = null;
		try
		{
			URL u = new URL(url);

			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.setDoOutput(true);// POST
			if (useragent == null)
			{
				uc.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
			}
			else
			{
				uc.setRequestProperty(USER_AGENT, useragent);
			}

			if (ref != null)
			{
				uc.setRequestProperty(REFERER, ref);
			}

			if (cookie != null)
			{
				uc.setRequestProperty(COOKIE, cookie);
			}

			Object o;
			try
			{

				InputStream in = uc.getInputStream();
				o = readGZipObject(in);
				in.close();
			}
			catch (SocketException e)
			{
				throw e;
			}

			return o;
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return httpstr;
		}
	}

	public static String loadHttpFile(String url, String cookie,
					String useragent, String chartset, String ref, int timeout,
					String post) throws ConnectException, IOException
	{
		if (post != null)
		{
			return loadHttpFilePost(url, cookie, useragent, chartset, ref,
							timeout, post.getBytes());
		}
		else
		{
			return loadHttpFilePost(url, cookie, useragent, chartset, ref,
							timeout, null);
		}
	}

	public static HashMap<String, String> loadHttpFilePost(String url,
					HashMap<String, String> head, String chartset, int timeout,
					byte[] post) throws ConnectException, IOException
	{
		return loadHttpFilePost(url, head, chartset, timeout, post, true);
	}

	public static HashMap<String, String> loadHttpFilePost(String url,
					HashMap<String, String> head, String chartset, int timeout,
					byte[] post, boolean followredirects)
					throws ConnectException, IOException
	{
		if (head == null)
		{
			head = new HashMap<String, String>();
		}
		HashMap<String, String> all = new HashMap<String, String>();
		Proxy.initCfgProxy();
		String httpstr = null;
		try
		{
			URL u = new URL(url);

			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			// System.out.println(timeout);
			// uc.setReadTimeout(timeout);
			uc.setConnectTimeout(timeout);
			uc.setReadTimeout(timeout);

			if (post != null)
			{
				uc.setDoOutput(true);// POST
				uc.setDoInput(true);
				uc.setRequestMethod("POST");
			}

			uc.setUseCaches(false);
			uc.setAllowUserInteraction(false);

			HttpURLConnection.setFollowRedirects(false);
			uc.setInstanceFollowRedirects(false);

			String useragent = head.get(USER_AGENT);
			if (useragent == null)
			{
				uc.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
			}

			Iterator<Entry<String, String>> iter = head.entrySet().iterator();
			while (iter.hasNext())
			{
				Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String val = entry.getValue();
				uc.setRequestProperty(key, val);
			}

			if (post != null)
			{
				OutputStream out = uc.getOutputStream();
				out.write(post);
				out.flush();
			}

			int code = uc.getResponseCode();

			if ((code == 302 || code == 301) && followredirects)
			{
				url = uc.getHeaderField("Location");
				Proxy.closeProxy();
				return loadHttpFilePost(url, head, chartset, timeout, post,
								followredirects);
			}

			all.put("resonpsenmessgae", uc.getResponseMessage());
			all.put("code", "" + code);
			all.put("url", url);

			Map<String, List<String>> heads = uc.getHeaderFields();
			Iterator<Entry<String, List<String>>> headiter = heads.entrySet()
							.iterator();
			while (headiter.hasNext())
			{
				Entry<String, List<String>> entry = headiter.next();
				String key = entry.getKey();
				List<String> hvlist = entry.getValue();
				StringBuffer buf = new StringBuffer();
				for (String v : hvlist)
				{
					buf.append(String.format("%s\n", v));
				}

				all.put(key, buf.toString().trim());
			}

			JFile file;
			try
			{
				file = new JFile(uc.getInputStream());
			}
			catch (SocketException e)
			{
				Proxy.closeProxy();
				throw e;
			}

			if (chartset == null)
			{
				chartset = uc.getContentEncoding();

				if (chartset == null)
				{
					String tc = uc.getContentType();
					if (tc != null)
					{
						int idx = tc.indexOf("charset=");
						if (idx >= 0)
						{
							chartset = tc.substring(idx + 8, tc.length());
						}
					}
				}

			}

			if (chartset == null)
			{
				httpstr = file.readAllText();
			}
			else
			{
				httpstr = file.readAllText(chartset);
			}

			all.put("html", httpstr);

			Proxy.closeProxy();
			return all;
		}
		catch (IOException e)
		{
			Log.OutException(e, String.format("url[%s]", url));
			Proxy.closeProxy();
			throw e;
		}

	}

	public static String loadHttpFilePost(String url, String cookie,
					String useragent, String chartset, String ref, int timeout,
					byte[] post) throws ConnectException, IOException
	{
		HashMap<String, String> request = new HashMap<String, String>();

		if (cookie != null)
		{
			request.put(COOKIE, cookie);
		}
		if (useragent != null)
		{
			request.put(USER_AGENT, useragent);
		}
		if (ref != null)
		{
			request.put(REFERER, ref);
		}

		return JFile.loadHttpFilePost(url, request, chartset, timeout, post)
						.get("html");
	}

	public static String loadHttpFile(String url, String cookie,
					String useragent, String chartset, String ref)
					throws ConnectException, IOException
	{
		return loadHttpFilePost(url, cookie, useragent, chartset, ref, 30000,
						null);
	}

	public static void saveHttpFile(final String url, final String localpath,
					final String ref) throws IOException
	{
		Proxy.initCfgProxy();
		URL u = new URL(url);

		HttpURLConnection uc = (HttpURLConnection) u.openConnection();
		uc.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
		uc.setRequestProperty(REFERER, ref);

		File newFile = new File(localpath);
		if (!newFile.exists())
		{
			newFile.createNewFile();
		}
		int byteread = 0;

		InputStream inStream = uc.getInputStream();
		FileOutputStream fs = new FileOutputStream(newFile);
		byte[] buffer = new byte[1444];
		while ((byteread = inStream.read(buffer)) != -1)
		{
			fs.write(buffer, 0, byteread);
		}
		inStream.close();
		fs.close();
		Proxy.closeProxy();
	}

	public static void saveHttpFile(final String url, final String localpath)
					throws IOException
	{
		saveHttpFile(url, localpath, null);
	}

	public void close()
	{
		if (out != null)
		{
			try
			{
				out.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * ȡ���ļ��ߴ�
	 * 
	 * @param file
	 * @return
	 */
	public static long getFileSize(String file)
	{
		File f = new File(file);
		return f.length();
	}

	public static boolean mkdirs(String name)
	{
		File f = new File(name);
		return f.mkdirs();
	}

	/**
	 * 改名或移动文件
	 * 
	 * @param oldname
	 * @param newname
	 * @return
	 */
	public static boolean renameTo(String oldname, String newname)
	{
		File file = new File(oldname);
		File nfile = new File(newname);

		if (file.renameTo(nfile))
		{
			return true;
		}
		else
		{
			if (copyFile(oldname, newname))
			{
				return file.delete();
			}
			else
			{
				return false;
			}
		}
	}

	public static boolean copyFile(String oldPath, String newPath)
	{
		boolean result = false;
		try
		{
			File newFile = new File(newPath);
			if (!newFile.exists())
			{
				newFile.createNewFile();
			}
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists())
			{ // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newFile);
				byte[] buffer = new byte[10240];
				while ((byteread = inStream.read(buffer)) != -1)
				{
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				buffer = null;
				inStream.close();
				fs.close();
				result = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}