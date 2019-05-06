package easy.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import easy.config.Config;
import easy.net.Proxy;
import easy.sql.Col;
import easy.sql.DataSet;
import easy.sql.Row;
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
		return f.exists();
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

	public String readAllText() throws IOException
	{
		return readAllText(null);
	}

	public byte[] readAllBytes() throws IOException
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
		
		return content;
	}

	public String readAllText(String chartset) throws IOException
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

		zfs.finish();
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

			HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
			uc.setHostnameVerifier(new HostnameVerifier() {
				 @Override
				 public boolean verify(String arg0, SSLSession arg1) {
				 return true;
				 }
				 });
			
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
					byte[] post, boolean followredirects) throws ConnectException, IOException
	{
		return loadHttpFilePost(url,head,chartset,timeout,post,followredirects,true);
	}

	public static HashMap<String, String> loadHttpFilePost(String url,
					HashMap<String, String> head, String chartset, int timeout,
					byte[] post, boolean followredirects,boolean useproxy)
					throws ConnectException, IOException
	{
		if (head == null)
		{
			head = new HashMap<String, String>();
		}
		HashMap<String, String> all = new HashMap<String, String>();
		if (useproxy)
		{
			Proxy.initCfgProxy();
		}
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
				out.close();
			}

			int code = uc.getResponseCode();

			if ((code == 302 || code == 301) && followredirects)
			{
				url = uc.getHeaderField("Location");
				if (useproxy)
				{
					Proxy.closeProxy();	
				}
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
				if (useproxy)
				{
					Proxy.closeProxy();
				}
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

			if (useproxy)
			{
				Proxy.closeProxy();
			}
			return all;
		}
		catch (IOException e)
		{
			//Log.OutException(e, String.format("url[%s]", url));
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
	
	public static void saveHttpFile(final String url, final String localpath,final String ref) throws IOException
	{
		saveHttpFile(url,localpath,ref,null,5000);
	}

	public static void saveHttpFile(final String url, final String localpath,final String ref,final String cookie,final int timeout) throws IOException
	{
		Proxy.initCfgProxy();
		URL u = new URL(url);

		HttpURLConnection uc = (HttpURLConnection) u.openConnection();
		uc.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
		if (ref!= null && "".equals(ref) == false)
		{
			uc.setRequestProperty(REFERER, ref);
		}

		if (cookie!= null && "".equals(cookie) == false)
		{
			uc.setRequestProperty(COOKIE, cookie);
		}
		
		uc.setConnectTimeout(timeout);
		uc.setReadTimeout(timeout);

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
	
	public static void copyDir(String sourcepath, String targetpath) throws IOException
	{
		Path source = Paths.get(sourcepath);

		if (Files.isExecutable(source) && Files.isDirectory(source))
		{
			Path target = Paths.get(targetpath);
			
			try
			{				
				Files.deleteIfExists(target);
			}
			catch (DirectoryNotEmptyException e)
			{
				Files.walkFileTree(target, new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                    Files.delete(file);
	                    return FileVisitResult.CONTINUE;
	                }
	                @Override
	                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	                    Files.delete(dir);
	                    return super.postVisitDirectory(dir, exc);
	                }
	            });
			}

			SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					//System.out.println("post visit directory: " + dir);
					//return preVisitDirectory(dir, attrs);
					String t = dir.toAbsolutePath().toString().replace(sourcepath, targetpath);

					Path pt = Paths.get(t);
					if (Files.isExecutable(pt) == false)
					{
						Files.createDirectories(pt);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException
				{
					String t = file.toAbsolutePath().toString().replace(sourcepath, targetpath);
					// 
//					System.out.println(t);

					Files.copy(file, Paths.get(t));

					return FileVisitResult.CONTINUE;
				}
			};

			Files.walkFileTree(Paths.get(sourcepath), finder);
		}
	}
	
	public static Kryo getKryo() 
	{
        Kryo kryo = new Kryo();
        
		kryo.getFieldSerializerConfig().setOptimizedGenerics(true);
        kryo.setReferences(false); 
        kryo.setRegistrationRequired(false);
        
        kryo.register(Message.class);
        kryo.register(DataSet.class);
        kryo.register(Row.class);
        kryo.register(Col.class);

        kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
        kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());

        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(LinkedList.class);
        kryo.register(HashSet.class);
        kryo.register(TreeSet.class);
        kryo.register(Hashtable.class);
        kryo.register(Date.class);
        kryo.register(Calendar.class);
        kryo.register(ConcurrentHashMap.class);
        kryo.register(SimpleDateFormat.class);
        kryo.register(GregorianCalendar.class);
        kryo.register(Vector.class);
        kryo.register(BitSet.class);
        kryo.register(StringBuffer.class);
        kryo.register(StringBuilder.class);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        kryo.register(String[].class);
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);
        return kryo;
    }

	public static Object kryoCompressBytesUnSerialize(byte[] bytes)
	{
		Object obj;
		if (bytes != null)
		{
			Kryo kryo = getKryo();

//			Input input = new Input(bytes,0,bytes.length);
//			InputStream cin = new InflaterInputStream(input);
			Input input = new Input(new InflaterInputStream(new ByteArrayInputStream(bytes)));
			obj = kryo.readClassAndObject(input);
			input.close();

			return obj;
		}

		return null;
	}

	public static byte[] kryoSerializeToCompressBytes(Object object)
	{
		byte[] bytes = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			DeflaterOutputStream cout = new DeflaterOutputStream(baos);
//			bytes = baos.toByteArray();

			kryoSerialize(object,cout);
			cout.finish();

			bytes = baos.toByteArray();
			baos.close();
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}

		return bytes;
	}

	public static Object kryoBytesUnSerialize(byte[] bytes)
	{
		if (bytes != null)
		{
			Kryo kryo = getKryo();

			Input input = new Input(bytes,0,bytes.length);
			return kryo.readClassAndObject(input);
			
//		      ByteArrayInputStream bais = null;
//		      try
//		      {
//		        // 反序列化
//		        bais = new ByteArrayInputStream(bytes);
//		        ObjectInputStream ois = new ObjectInputStream(bais);
//		        return kryoUnserialize(ois);
//		      }
//		      catch (Exception e)
//		      {
//		        Log.OutException(e);
//		      }	
		}

		return null;
	}
	
	public static byte[] kryoSerializeToBytes(Object object)
	{
		byte[] bytes = null;
		
		ByteArrayOutputStream baos = null;
		baos = new ByteArrayOutputStream();
		try
		{
			
//			bytes = baos.toByteArray();
			kryoSerialize(object,baos);			
			bytes = baos.toByteArray();

			baos.close();
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
		
		return bytes;
	}
	
	public static void kryoSerialize(Object object,OutputStream out)
	{
		Kryo kryo = getKryo();

		Output output = new Output(out,1024*1024);
//		kryo.writeObject(output, ds);
		kryo.writeClassAndObject(output, object);
		output.flush();
//		output.close();
	}
	
	public static Object kryoUnserialize(InputStream in)
	{
		Kryo kryo = getKryo();
        
		Input input = new Input(in,1024*1024);
		return kryo.readClassAndObject(input);
	}
	
	public static byte[] serialize(Object object)
	{
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try
		{
			// 序列化
			baos = new ByteArrayOutputStream();
			GZIPOutputStream go = new GZIPOutputStream(baos);
			oos = new ObjectOutputStream(go);
			oos.writeObject(object);
			go.flush();
			go.finish();
			
			byte[] bytes = baos.toByteArray();
//			System.out.println(bytes.length);
			return bytes;
		}
		catch (Exception e)
		{
			Log.OutException(e);
		}
		return null;
	}

	public static Object unserialize(byte[] bytes)
	{
		if (bytes != null)
		{
			ByteArrayInputStream bais = null;
			try
			{
				// 反序列化
				bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(bais));
				return ois.readObject();
			}
			catch (Exception e)
			{
				Log.OutException(e);
			}			
		}

		return null;
	}
	
	public static void main(String[] args) throws ConnectException, IOException
	{
		String a = "ASDf   adfadsf";
		byte[] bs = JFile.kryoSerializeToBytes(a);
		System.out.println(new String(bs));
		System.out.println(JFile.kryoBytesUnSerialize(bs));

	}
}