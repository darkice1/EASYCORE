package easy.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import easy.net.Proxy;
import easy.sql.Col;
import easy.sql.DataSet;
import easy.sql.Row;
import easy.util.Format;
import easy.util.Log;

import javax.mail.Message;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

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
	private String filename;
	private InputStream in;
	private BufferedWriter out;

	private final static String USER_AGENT = "User-Agent";
	private final static String REFERER = "Referer";

	private final static String COOKIE = "cookie";
	private final static String USER_AGENT_VALUE = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; Tablet PC 2.0)";

	public JFile(String p_filename)
	{
		this(p_filename, true);
	}

	public static boolean exists(String filename)
	{
		File f = new File(filename);
		return f.exists();
	}

	public static boolean delete(String filename)
	{
		File f = new File(filename);
		return f.delete();
	}

	@SuppressWarnings("unused")
	public static byte[] getFileBytes(String filename) throws IOException
	{
		try(RandomAccessFile rf = new RandomAccessFile(filename,"r"))
		{
			byte[] result;
//			fc = new RandomAccessFile(filename,"r").getChannel();
			FileChannel fc = rf.getChannel();
			MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load();
//			System.out.println(byteBuffer.isLoaded());
			result = new byte[(int)fc.size()];
			if (byteBuffer.remaining() > 0)
			{
				//              System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		}
	}

	public static void  writeFileBytes(String path,byte[] data) throws IOException
	{
		BufferedOutputStream outputStream  = new BufferedOutputStream(new FileOutputStream(path));
		outputStream.write(data);
		outputStream.close();
	}

	public JFile(String p_filename, boolean append)
	{
		filename = p_filename;

		try
		{
			out = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(
							filename, append), StandardCharsets.UTF_8));
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
				List<String> list = new ArrayList<>();

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
		byte[] content;
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

			final int BUFSIZE = 1024;

			 ByteArrayOutputStream out = new ByteArrayOutputStream(BUFSIZE);
		       
			 //System.out.println("Available bytes:" + in.available());        
		       
			 byte[] temp = new byte[BUFSIZE];
			 int size;
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

	@SuppressWarnings("unused")
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
	}

	@SuppressWarnings("unused")
	public static void writeGZipObject(String file, Object obj)
					throws IOException
	{
		FileOutputStream fis = new FileOutputStream(file);
		writeGZipObject(fis, obj);
		fis.close();
	}

	public static void writeGZipObject(OutputStream out, Object obj)
					throws IOException
	{
		GZIPOutputStream zfs = new GZIPOutputStream(out);
		writeObject(zfs, obj);

		zfs.finish();
		zfs.close();
	}

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

	@SuppressWarnings("unused")
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


	public static String loadHttpFile(String url) throws IOException
	{
		return loadHttpFile(url, null, null, null, null);
	}

	@SuppressWarnings("unused")
	public static Object loadHttpObject(String url) throws IOException
	{
		return loadHttpObject(url, null, null);
	}

	public static Object loadHttpObject(String url, String cookie,
					String useragent) throws IOException
	{
		Proxy.initCfgProxy();
		String httpstr = null;
		try
		{
			URL u = new URL(url);

			HttpsURLConnection uc = (HttpsURLConnection) u.openConnection();
			uc.setHostnameVerifier((arg0, arg1) -> true);
			
			uc.setDoOutput(true);// POST
			uc.setRequestProperty(USER_AGENT, Objects.requireNonNullElse(useragent, USER_AGENT_VALUE));

			if (cookie != null)
			{
				uc.setRequestProperty(COOKIE, cookie);
			}

			Object o;

			InputStream in = uc.getInputStream();
			o = readObject(in);
			in.close();

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

	@SuppressWarnings("unused")
	public static Object loadHttpGZipObject(String url)
					throws IOException
	{
		return loadHttpGZipObject(url, null, null);
	}

	public static Object loadHttpGZipObject(String url, String cookie,
					String useragent) throws IOException
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
			uc.setRequestProperty(USER_AGENT, Objects.requireNonNullElse(useragent, USER_AGENT_VALUE));

			if (ref != null)
			{
				uc.setRequestProperty(REFERER, ref);
			}

			if (cookie != null)
			{
				uc.setRequestProperty(COOKIE, cookie);
			}

			Object o;

			InputStream in = uc.getInputStream();
			o = readGZipObject(in);
			in.close();

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
					String post) throws IOException
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
					byte[] post) throws IOException
	{
		return loadHttpFilePost(url, head, chartset, timeout, post, true);
	}
	
	public static HashMap<String, String> loadHttpFilePost(String url,
					HashMap<String, String> head, String chartset, int timeout,
					byte[] post, boolean followredirects) throws IOException
	{
		return loadHttpFilePost(url,head,chartset,timeout,post,followredirects,true);
	}

	public static HashMap<String, String> loadHttpFilePost(String url,
					HashMap<String, String> head, String chartset, int timeout,
					byte[] post, boolean followredirects,boolean useproxy)
					throws IOException
	{
		if (head == null)
		{
			head = new HashMap<>();
		}
		HashMap<String, String> all = new HashMap<>();
		if (useproxy)
		{
			Proxy.initCfgProxy();
		}
		String httpstr;
		try
		{
			URL u = new URL(url);

			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
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

			for (Entry<String, String> entry : head.entrySet())
			{
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
			for (Entry<String, List<String>> entry : heads.entrySet())
			{
				String key = entry.getKey();
				List<String> hvlist = entry.getValue();
				StringBuilder buf = new StringBuilder();
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
							chartset = tc.substring(idx + 8);
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
					byte[] post) throws IOException
	{
		HashMap<String, String> request = new HashMap<>();

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
					throws IOException
	{
		return loadHttpFilePost(url, cookie, useragent, chartset, ref, 30000,
						null);
	}
	
	public static void saveHttpFile(final String url, final String localpath,final String ref) throws IOException
	{
		saveHttpFile(url,localpath,ref,null,5000);
	}

	public static byte[] inputStreamToBytes(InputStream in) throws IOException
	{
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int rc;
		while ((rc = in.read(buff, 0, 1024)) > 0)
		{
			swapStream.write(buff, 0, rc);
		}
		return swapStream.toByteArray();
	}

	public static byte[] getHttpFileBytes(final String url,final String ref,final String cookie,final int timeout) throws IOException
	{
		byte[] bs;
		Proxy.initCfgProxy();
		URL u = new URL(url);

		HttpURLConnection uc = (HttpURLConnection) u.openConnection();
		uc.setRequestProperty(USER_AGENT, USER_AGENT_VALUE);
		if (ref!= null && !ref.isEmpty())
		{
			uc.setRequestProperty(REFERER, ref);
		}

		if (cookie!= null && !cookie.isEmpty())
		{
			uc.setRequestProperty(COOKIE, cookie);
		}

		uc.setConnectTimeout(timeout);
		uc.setReadTimeout(timeout);

		InputStream inStream = uc.getInputStream();

		bs = inputStreamToBytes(inStream);

		inStream.close();

		Proxy.closeProxy();

		return bs;
	}

	public static void saveHttpFile(final String url, final String localpath,final String ref,final String cookie,final int timeout) throws IOException
	{
		byte[] bs = getHttpFileBytes(url,ref,cookie,timeout);
		if (bs != null)
		{
			JFile.writeFileBytes(localpath,bs);
		}
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

	@SuppressWarnings("unused")
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
			int byteread;
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
				inStream.close();
				fs.close();
				result = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			result = false;
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
				Files.walkFileTree(target, new SimpleFileVisitor<>()
				{
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
					{
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
					{
						Files.delete(dir);
						return super.postVisitDirectory(dir, exc);
					}
				});
			}

			SimpleFileVisitor<Path> finder = new SimpleFileVisitor<>()
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					String t = dir.toAbsolutePath().toString().replace(sourcepath, targetpath);

					Path pt = Paths.get(t);
					if (!Files.isExecutable(pt))
					{
						Files.createDirectories(pt);
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					String t = file.toAbsolutePath().toString().replace(sourcepath, targetpath);
					// 
//					System.out.println(t);

					Files.copy(file, Paths.get(t));

					return FileVisitResult.CONTINUE;
				}
			};

			Files.walkFileTree(source, finder);
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
		return kryoCompressBytesUnSerialize(null, bytes);
	}

	public static Object kryoCompressBytesUnSerialize(Kryo kryo,byte[] bytes)
	{
		Object obj;
		if (bytes != null)
		{
			if (kryo == null)
			{
				kryo = getKryo();
			}
			Input input = new Input(new InflaterInputStream(new ByteArrayInputStream(bytes)));

			obj = kryo.readClassAndObject(input);
			input.close();

			return obj;
		}

		return null;
	}

	public static byte[] kryoSerializeToCompressBytes(Object object)
	{
		return kryoSerializeToCompressBytes(null,object);
	}

	public static byte[] kryoSerializeToCompressBytes(Kryo kryo,Object object)
	{
		byte[] bytes = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			DeflaterOutputStream cout = new DeflaterOutputStream(baos);

			kryoSerialize(kryo,object,cout);
			cout.finish();

			bytes = baos.toByteArray();
			cout.close();
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}

		return bytes;
	}

	public static Object kryoBytesUnSerialize(Kryo kryo,byte[] bytes)
	{
		if (bytes != null)
		{
			if (kryo == null)
			{
				kryo = getKryo();
			}

			Input input = new Input(bytes,0,bytes.length);
			return kryo.readClassAndObject(input);
		}

		return null;
	}

	@SuppressWarnings("unused")
	public static Object kryoBytesUnSerialize(byte[] bytes)
	{
		return kryoBytesUnSerialize(null,bytes);
	}
	
	public static byte[] kryoSerializeToBytes(Kryo kryo,Object object)
	{
		byte[] bytes = null;
		
		ByteArrayOutputStream baos;
		baos = new ByteArrayOutputStream();
		try
		{

//			bytes = baos.toByteArray();
			kryoSerialize(kryo,object,baos);
			bytes = baos.toByteArray();

			baos.close();
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}

		return bytes;
	}

	@SuppressWarnings("unused")
	public static byte[] kryoSerializeToBytes(Object object)
	{
		return kryoSerializeToBytes(null,object);
	}

	public static void kryoSerialize(Kryo kryo,Object object,OutputStream out)
	{
		if (kryo == null)
		{
			kryo = getKryo();
		}

		Output output = new Output(out,4*1024);
		//		kryo.writeObject(output, ds);
		kryo.writeClassAndObject(output, object);
		output.flush();
		//		output.close();
	}


	@SuppressWarnings("unused")
	public static void kryoSerialize(Object object,OutputStream out)
	{
		kryoSerialize(null, object, out);
	}

	@SuppressWarnings("unused")
	public static Object kryoUnserialize(InputStream in)
	{
		Kryo kryo = getKryo();
        
		Input input = new Input(in,4*1024);
		return kryo.readClassAndObject(input);
	}

	@SuppressWarnings("unused")
	public static byte[] serialize(Object object)
	{
		ObjectOutputStream oos;
		ByteArrayOutputStream baos;
		try
		{
			// 序列化
			baos = new ByteArrayOutputStream();
			GZIPOutputStream go = new GZIPOutputStream(baos);
			oos = new ObjectOutputStream(go);
			oos.writeObject(object);
			go.flush();
			go.finish();

			//			System.out.println(bytes.length);
			return baos.toByteArray();
		}
		catch (Exception e)
		{
			Log.OutException(e);
		}
		return null;
	}

	@SuppressWarnings("unused")
	public static Object unserialize(byte[] bytes)
	{
		if (bytes != null)
		{
			ByteArrayInputStream bais;
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

	@SuppressWarnings("unused")
	public static Object cloneObject(Object obj)
	{
		return JFile.kryoCompressBytesUnSerialize(JFile.kryoSerializeToCompressBytes(obj));
	}
}