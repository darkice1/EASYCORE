package easy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.ConnectException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.mozilla.universalchardet.UniversalDetector;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import easy.io.JFile;
import easy.servlet.PageInfo;
import easy.sql.CPSql;
import easy.sql.DataSet;
import easy.sql.Row;

/**
 * <p>
 * </p>
 * 格式化处理
 * 
 * @version 1.0 (<i>2005-8-17 Neo</i>)
 */

public class Format
{
	private final static Map<String, String> cmap = new HashMap<String, String>();

	private final static Format FORMAT = new Format();

	private final static String LOWSTRING = "abcdefghijklmnopqrstuvwxyz";
	private final static String NUMLOWSTRING = "abcdefghijklmnopqrstuvwxyz1234567890";
	private final static String ALLSTRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

	// private final static Pattern URLPAT
	// =Pattern.compile("(http://|https://)[^\\s]*");
	protected final transient static List<String> GURLLIST = new ArrayList<String>();
	public final transient static List<String> GAELIST = new ArrayList<String>();
	static
	{
		GAELIST.add("hwosoproxy1.appspot.com");
		GAELIST.add("hwosoproxy2.appspot.com");
		GAELIST.add("hwosoproxy3.appspot.com");
		GAELIST.add("hwosoproxy4.appspot.com");
		GAELIST.add("hwosoproxy5.appspot.com");
		GAELIST.add("hwosoproxy6.appspot.com");
		GAELIST.add("hwosoproxy7.appspot.com");
		GAELIST.add("hwosoproxy8.appspot.com");
		GAELIST.add("hwosoproxy9.appspot.com");
		GAELIST.add("hwosoproxy10.appspot.com");
		GAELIST.add("hwosoproxy11.appspot.com");
		GAELIST.add("hwosoproxy12.appspot.com");
		GAELIST.add("hwosoproxy13.appspot.com");
		GAELIST.add("hwosoproxy14.appspot.com");
		GAELIST.add("hwosoproxy15.appspot.com");
		GAELIST.add("hwosoproxy16.appspot.com");
		GAELIST.add("hwosoproxy17.appspot.com");

		for (String host : GAELIST)
		{
			GURLLIST.add(String.format("http://%s/c?action=GetUrl&z=%%s&u=%%s",
							host));
		}
		// GURLLIST.add("http://wosoproxy1.appspot.com/c?action=GetUrl&z=%s&u=%s");
		// GURLLIST.add("http://wosoproxy2.appspot.com/c?action=GetUrl&z=%s&u=%s");
		// GURLLIST.add("http://wosoproxy3.appspot.com/c?action=GetUrl&z=%s&u=%s");

		JFile file = new JFile(FORMAT.getClass().getResourceAsStream(
						"pinyin.txt"));
		List<String> list = file.getLineList();
		for (String t : list)
		{
			String[] s = t.split(" ", 2);
			if (s.length >= 2)
			{
				cmap.put(new String(s[0]), new String(s[1]));
			}
			s = null;
			t = null;
		}
		list = null;
		file = null;
	}

	public static String getGaeURL(String u)
	{
		int idx = (int) (Math.random() * GURLLIST.size());
		String url = null;
		try
		{
			url = String.format(GURLLIST.get(idx), "n",
							java.net.URLEncoder.encode(u, "utf-8"));
		}
		catch (UnsupportedEncodingException e)
		{
		}
		return url;
	}

	public static String getGaeZipURL(String u)
	{
		int idx = (int) (Math.random() * GURLLIST.size());
		String url = null;
		try
		{
			url = String.format(GURLLIST.get(idx), "y",
							java.net.URLEncoder.encode(u, "utf-8"));
		}
		catch (UnsupportedEncodingException e)
		{
		}
		return url;
	}

	/**
	 * 转换成script输出使用字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String toScriptString(String src)
	{
		String tmp = src;
		tmp = tmp.replaceAll("\r|\n", "");
		tmp = tmp.replaceAll("\"", "\\\\\"");
		tmp = tmp.replaceAll("</script>", "\"+\"<\"+\"/script>\"+\"");
		tmp = tmp.replaceAll("</SCRIPT>", "\"+\"<\"+\"/SCRIPT>\"+\"");

		return tmp;
	}

	/**
	 * 转换成HTML输出使用字符串。取出&,",',<,>。
	 * 
	 * @param src
	 * @return
	 */
	public static String toHTMLString(String src, boolean isnoquotes)
	{
		if (src == null)
		{
			return "";
		}
		String tmp = src;
		tmp = tmp.replaceAll("&", "&amp;");
		if (isnoquotes == true)
		{
			tmp = tmp.replaceAll("\"", "&quot;");
			tmp = tmp.replaceAll("'", "&#039;");
		}
		tmp = tmp.replaceAll("<", "&lt;");
		tmp = tmp.replaceAll(">", "&gt;");

		return tmp;
	}

	/**
	 * 转换成HTML输出使用字符串。取出&,",',<,>。
	 * 
	 * @param src
	 * @return
	 */
	public static String toHTMLString(String src)
	{
		return toHTMLString(src, false);
	}

	public static String toXMLString(DataSet ds)
	{
		PageInfo pi = new PageInfo();
		pi.setStartIndex(0);
		pi.setRecordCount(ds.getCount());
		pi.setPageSize(ds.getCount());

		pi.setPageNumber(1);
		pi.setTotalPage(1);

		return toXMLString(ds, pi, -1);
	}

	/**
	 * list转json.
	 * 
	 * @param list
	 * @return
	 */
	public static String listToJsonString(List<Row> list)
	{
		return listToJsonString(list, null);
	}

	/**
	 * 讲row list转成json
	 * 
	 * @param list
	 * @param addjson
	 *            新增json属性
	 * @return
	 */
	public static String listToJsonString(List<Row> list, JSONObject addjson)
	{
		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();

		for (Row r : list)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			for (String col : r.getColsNameList())
			{
				map.put(col, r.getString(col));
			}
			array.add(map);
		}

		if (addjson != null)
		{// Iterator<Entry<String, String>> paramsfields =
			// params.entrySet().iterator();

			@SuppressWarnings("rawtypes")
			Iterator iter = addjson.keys();
			while (iter.hasNext())
			{
				String key = (String) iter.next();
				json.put(key, addjson.get(key));
			}
		}
		json.put("total", list.size());
		json.put("result", array);

		return json.toString();
	}

	public static String toXMLString(DataSet ds, PageInfo pi, long use_time)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buf.append(String
						.format("<rs count=\"%s\" pageSize=\"%s\" pageCount=\"%s\" pageNum=\"%s\" use_time=\"%s\">",
										pi.getRecordCount(), pi.getPageSize(),
										pi.getTotalPage(), pi.getPageNumber(),
										use_time));

		for (int i = pi.getStartIndex(), k = 1; i < pi.getRecordCount()
						&& k <= pi.getPageSize(); i++, k++)
		{
			Row r = ds.getRow(i);
			buf.append("<r");
			for (String str : r.getColsNameList())
			{
				buf.append(String.format(" %s=\"%s\"", toHTMLString(str, true),
								toHTMLString(r.getString(str), true)));
			}
			buf.append("/>");
		}

		buf.append("</rs>");

		return buf.toString();
	}


	public static <E>String toListString(E[] array)
	{
		StringBuffer buf = new StringBuffer();
		for (E e : array)
		{        
			buf.append(e);
			buf.append(",");
        }
		int len = buf.length();
		if (len > 0)
		{
			buf.setLength(len-1);
		}

		return buf.toString();
	}
	
	/**
	 * 返回list输出字符串，使用,分割
	 * 
	 * @param list
	 * @return
	 */
	public static String toListString(String[] strs)
	{
		List<String> list = new ArrayList<String>();
		for (String t : strs)
		{
			list.add(t);
		}

		return toListString(list, ",");
	}

	/**
	 * 返回list输出字符串，使用,分割
	 * 
	 * @param list
	 * @return
	 */
	public static String toListString(List<?> list)
	{
		return toListString(list, ",");
	}

	/**
	 * 返回list输出字符串
	 * 
	 * @param list
	 *            对应list
	 * @param splitstr
	 *            分割字符
	 * @return
	 */
	public static String toListString(List<?> list, String splitstr)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0, len = list.size(); i < len; i++)
		{
			buf.append(list.get(i).toString());
			buf.append(splitstr);
		}
		if (list.size() > 0)
		{
			buf.setLength(buf.length() - splitstr.length());
		}
		return buf.toString();
	}

	public static String getContent(String str, String start, String end)
	{
		try
		{
			int si = str.indexOf(start);
			if (si < 0)
			{
				return null;
			}
			int ssi = si + start.length();
			int ei = str.indexOf(end, ssi);
			return str.substring(ssi, ei);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String replaceContent(String str, String start, String end,
					String newstring)
	{
		try
		{
			int si = str.indexOf(start);
			int ssi = si + start.length();
			int ei = str.indexOf(end, ssi);

			return String.format("%s%s%s", str.substring(0, ssi), newstring,
							str.substring(ei, str.length()));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 汉字转拼音
	 * 
	 * @param str
	 * @return
	 */
	public static String getPinyin(String str)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < str.length(); i++)
		{
			String k = str.substring(i, i + 1);
			String t = cmap.get(k);
			if (t == null)
			{
				buf.append(k);
			}
			else
			{
				buf.append(t);
			}
		}

		return buf.toString();
	}

	/**
	 * 取得拼音首字母
	 * 
	 * @param str
	 * @return
	 */
	public static String getFirstPinyin(String str)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < str.length(); i++)
		{
			String k = str.substring(i, i + 1);
			String t = cmap.get(k);
			if (t == null)
			{
				buf.append(k);
			}
			else
			{
				buf.append(t.substring(0, 1));
			}
		}

		return buf.toString();
	}

	/**
	 * 取得对象所有变量打印
	 * 
	 * @param o
	 * @return
	 */
	public static String beanToString(Object o)
	{
		StringBuffer buf = new StringBuffer();
		Field[] fields = o.getClass().getDeclaredFields();

		for (Field f : fields)
		{
			boolean accessFlag = f.isAccessible();
			f.setAccessible(true);
			try
			{
				Object po  = f.get(o);
				//System.out.println(f.getName()+" "+po.getClass().isArray()+" "+po);
				if (po!=null && po.getClass().isArray())
				{
					buf.append(f.getName());
					buf.append("[");
					for (int i=0,len=Array.getLength(po);i<len;i++)
					{
						buf.append(Array.get(po, i));
						buf.append(",");
					}
					buf.setLength(buf.length()-1);
					buf.append("]\n");
				}
				else
				{
					buf.append(String.format("%s:[%s]\n", f.getName(), po));
				}
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			f.setAccessible(accessFlag);
		}

		return buf.toString();
	}

	/**
	 * 字符串相似值 Levenshtein Distance
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int ld(String str1, String str2)
	{
		int d[][]; // 矩阵
		int n = str1.length();
		int m = str2.length();
		int i; // 遍历str1的
		int j; // 遍历str2的
		char ch1; // str1的
		char ch2; // str2的
		int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if (n == 0)
		{
			return m;
		}
		if (m == 0)
		{
			return n;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++)
		{ // 初始化第一列
			d[i][0] = i;
		}
		for (j = 0; j <= m; j++)
		{ // 初始化第一行
			d[0][j] = j;
		}
		for (i = 1; i <= n; i++)
		{ // 遍历str1
			ch1 = str1.charAt(i - 1);
			// 去匹配str2
			for (j = 1; j <= m; j++)
			{
				ch2 = str2.charAt(j - 1);
				if (ch1 == ch2)
				{
					temp = 0;
				}
				else
				{
					temp = 1;
				}
				// 左边+1,上边+1, 左上角+temp取最小
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1]
								+ temp);
			}
		}
		return d[n][m];
	}

	/**
	 * 返回字符串相似百分比
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static double sim(String str1, String str2)
	{
		int ld = ld(str1, str2);
		return 1 - (double) ld / Math.max(str1.length(), str2.length());
	}

	private static int min(int one, int two, int three)
	{
		int min = one;
		if (two < min)
		{
			min = two;
		}
		if (three < min)
		{
			min = three;
		}
		return min;
	}

	/**
	 * ip地址转成整数.
	 * 
	 * @param ip
	 * @return
	 */
	public static long ip2long(String ip)
	{
		long num = 0;

		if (ip != null)
		{
			String[] ips = ip.split("[.]", 4);
			try
			{
				for (int i = 0, len = ips.length; i < len; i++)
				{
					String s = ips[i].trim();
					long l = 0;
					try
					{
						l = Long.parseLong(s);
					}
					catch (Exception e)
					{
						// Log.OutException(e);
					}

					num += l << ((3l - i) * 8);
				}

				/*
				 * num = 16777216L * Long.parseLong(ips[0]) + 65536L
				 * Long.parseLong(ips[1]) + 256 * Long.parseLong(ips[2]) +
				 * Long.parseLong(ips[3]);
				 */
			}
			catch (Exception e)
			{
				Log.OutException(e, ip);
			}

			ips = null;
		}

		return num;
	}

	/**
	 * 整数转成ip地址.
	 * 
	 * @param ipLong
	 * @return
	 */
	public static String long2ip(long ipLong)
	{
		// long ipLong = 1037591503;
		long mask[] =
		{ 0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000 };
		long num = 0;
		StringBuffer ipInfo = new StringBuffer();
		for (int i = 0; i < 4; i++)
		{
			num = (ipLong & mask[i]) >> (i * 8);
			if (i > 0)
				ipInfo.insert(0, ".");
			ipInfo.insert(0, Long.toString(num, 10));
		}
		mask = null;
		return ipInfo.toString();
	}

	/**
	 * 返回随机字符串 只有小写字母与数字
	 * 
	 * @param num
	 * @return
	 */
	public static String getRandStringNum(int num)
	{
		StringBuffer buf = new StringBuffer();
		int len = NUMLOWSTRING.length();
		// LOWSTRING
		for (int i = 0; i < num; i++)
		{
			int pos = (int) (Math.random() * len);
			buf.append(NUMLOWSTRING.substring(pos, pos + 1));
		}
		return buf.toString();
	}

	/**
	 * 返回随机字符串 只有小写字母
	 * 
	 * @param num
	 *            生成字母数量
	 * @return
	 */
	public static String getRandString(int num)
	{
		StringBuffer buf = new StringBuffer();
		int len = LOWSTRING.length();
		// LOWSTRING
		for (int i = 0; i < num; i++)
		{
			int pos = (int) (Math.random() * len);
			buf.append(LOWSTRING.substring(pos, pos + 1));
		}
		return buf.toString();
	}

	/**
	 * 返回随机字符串 大小写与数字
	 * 
	 * @param num
	 *            生成字母数量
	 * @return
	 */
	public static String getRandAllString(int num)
	{
		StringBuffer buf = new StringBuffer();
		int len = ALLSTRING.length();
		// LOWSTRING
		for (int i = 0; i < num; i++)
		{
			int pos = (int) (Math.random() * len);
			buf.append(ALLSTRING.substring(pos, pos + 1));
		}
		return buf.toString();
	}

	/**
	 * 获取URLEncoder.encode编码的原始字符串编码类型
	 * 
	 * @param str
	 * @return 编码
	 */
	public static String getDecoderChartset(String str)
	{
		// utf8中汉字是 %E4%B8%80 到 %E9%BE%A5 gbk中汉字是 %D2%BB 到 %FD%9B
		str = str.toUpperCase();
		String charset = "UTF-8";

		Pattern RESULTPAT = Pattern.compile("%[0-9A-F]{2}");
		Matcher msc = RESULTPAT.matcher(str);
		out: while (msc.find())
		{
			String r = msc.group(0);
			String f = r.substring(1, 2);
			if ("E".equals(f))
			{
				for (int i = 0; i < 2; i++)
				{
					if (msc.find() == false)
					{
						charset = "GBK";
						break out;
					}
				}
			}
			else
			{
				if ("D".equals(f) || "F".equals(f))
				{
					charset = "GBK";
					break;
				}
			}
		}
		return charset;
	}

	public static String Sha256(String str)
	{
		return MessageDigest("sha-256", str);
	}

	public static String Sha1(String str)
	{
		return MessageDigest("sha-1", str);
	}

	public static String Md2(String str)
	{
		return MessageDigest("md2", str);
	}

	public static String fileMd5(final String inputFile) throws IOException
	{
		File file = new File(inputFile);
		String value = null;
		FileInputStream filein = new FileInputStream(file);
		MappedByteBuffer byteBuffer = filein.getChannel().map(
						FileChannel.MapMode.READ_ONLY, 0, file.length());
		MessageDigest md5;
		try
		{
			md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);

			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		}
		catch (NoSuchAlgorithmException e)
		{
			Log.OutException(e);
		}

		filein.close();

		return value;

	}

	public static String Md5(String str)
	{
		return MessageDigest("md5", str);
	}

	/**
	 * 返回文件扩展名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getFileExtName(String filename)
	{
		int idx = filename.lastIndexOf(".") + 1;
		String ext = filename.substring(idx);

		return ext;
	}

	public static Long getNumber(String str)
	{
		String number = "0123456789";
		StringBuffer buf = new StringBuffer("0");

		for (char t : str.toCharArray())
		{
			for (int i = 0, len = number.length(); i < len; i++)
			{
				char nt = number.charAt(i);
				if (nt == t)
				{
					buf.append(t);
					break;
				}
			}
		}

		return Long.parseLong(buf.toString());
	}

	public static String MessageDigest(String m, String str)
	{
		String mstr = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance(m);
			mstr = Format.byte2hex(md.digest(str.getBytes()));
		}
		catch (NoSuchAlgorithmException e)
		{
			Log.OutException(e);
		}
		return mstr;
	}

	public static String byte2hex(byte[] b) // 二行制转字符串
	{
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++)
		{
			String hex = Integer.toHexString(0xff & b[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String encodeBase64(final byte[] buf) throws IOException
	{
		BASE64Encoder en = new sun.misc.BASE64Encoder();
		return en.encode(buf);
	}

	public static byte[] decodeBase64(final String str) throws IOException
	{
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(str);
	}

	public static String encodeDes(String mykey, String encryptedString)
					throws UnsupportedEncodingException, InvalidKeyException,
					NoSuchAlgorithmException, NoSuchPaddingException,
					InvalidKeySpecException
	{
		byte[] keyAsBytes = mykey.getBytes("utf-8");
		DESKeySpec myKeySpec = new DESKeySpec(keyAsBytes);
		SecretKeyFactory mySecretKeyFactory = SecretKeyFactory
						.getInstance("DES");
		SecretKey key = mySecretKeyFactory.generateSecret(myKeySpec);

		Cipher cipher = Cipher.getInstance("DES/ecb/pkcs5padding");

		String encryptedText = null;
		try
		{
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = cipher.doFinal(encryptedString.getBytes());

			encryptedText = new String(Base64UrlSafe.encodeBase64(plainText));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return encryptedText;
	}

	public static String decodeDes(String mykey, String encryptedString)
					throws UnsupportedEncodingException, InvalidKeyException,
					NoSuchAlgorithmException, NoSuchPaddingException,
					InvalidKeySpecException
	{
		byte[] keyAsBytes = mykey.getBytes("utf-8");
		KeySpec myKeySpec = new DESKeySpec(keyAsBytes);
		SecretKeyFactory mySecretKeyFactory = SecretKeyFactory
						.getInstance("DES");
		Cipher cipher = Cipher.getInstance("DES/ecb/pkcs5padding");
		SecretKey key = mySecretKeyFactory.generateSecret(myKeySpec);

		String decryptedText = null;
		try
		{
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptedText = Base64UrlSafe.decodeBase64(encryptedString);
			byte[] plainText = cipher.doFinal(encryptedText);
			decryptedText = new String(plainText);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return decryptedText;
	}

	/**
	 * 返回svm字符串
	 * 
	 * @param sql
	 * @param type
	 *            类别字段
	 * @param fields
	 *            特征字段（,分割）
	 * @return
	 */
	public static String sqlToSvm(String sqlstr, String type, String fields)
	{
		StringBuffer buf = new StringBuffer();

		String[] fs = fields.split(",");
		for (int i = 0, len = fs.length; i < len; i++)
		{
			fs[i] = new String(fs[i].trim());
		}

		CPSql sql = new CPSql();
		try
		{
			DataSet ds = sql.executeQuery(sqlstr);
			while (ds.next())
			{
				// buf.append(String.format("", arg1));
				buf.append(ds.getFloat(type));
				for (int i = 0, len = fs.length; i < len; i++)
				{
					buf.append(String.format(" %d:%f", i + 1,
									ds.getDouble(fs[i])));
				}
				buf.append("\n");
			}
		}
		catch (SQLException e)
		{
			Log.OutException(e);
		}
		sql.close();
		fs = null;

		return buf.toString();
	}

	public static List<String> getUrls(final String str)
	{
		final Pattern URLPAT = Pattern
						.compile("(http(|s)://[-a-zA-Z0-9@:%_\\+.~,#?&//=]+)");
		List<String> list = new LinkedList<String>();
		Matcher matcher = URLPAT.matcher(str);
		while (matcher.find())
		{
			list.add(matcher.group());
		}
		return list;
	}

	public static List<String> getAts(final String str)
	{
		List<String> list = new LinkedList<String>();

		if (str != null)
		{
			final Pattern ATPAT = Pattern
							.compile(String.format(
											"@[[^@\\s%s]0-9]{1,20}",
											"`~!@#\\$%\\^&*()=+\\[\\]{}\\|/\\?<>,\\.:\\u00D7\\u00B7\\u2014-\\u2026\\u3001-\\u3011\\uFE30-\\uFFE5"));
			Matcher matcher = ATPAT.matcher(str);
			while (matcher.find())
			{
				list.add(matcher.group());
			}
		}

		return list;
	}

	public static String getMapString(Map<?, ?> map)
	{
		StringBuffer buf = new StringBuffer();

		for (Map.Entry<?, ?> entry : map.entrySet())
		{
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();

			buf.append(String.format("[%s]:[%s]\n", key, val));
		}

		return buf.toString();
	}

	public static Map<String, String> getMyIpAll()
	{
		Map<String, String> all = new HashMap<String, String>();

		try
		{
			String content = JFile.loadHttpFile(
							"http://iframe.ip138.com/ic.asp", null, null,
							"gbk", "http://ip138.com/");
			String ip = Format.getContent(content, "[", "]");
			String area = Format.getContent(content, "来自：", "</center>");
			all.put("ip", ip);
			all.put("area", area);
		}
		catch (ConnectException e)
		{
			Log.OutException(e);
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}

		return all;
	}

	public static String getMyIp()
	{
		return getMyIpAll().get("ip");
	}

	public static String getDomain(final String url)
	{
		String t[] = url.split("/");
		String domain = "";

		if (t.length >= 3)
		{
			domain = new String(t[2]);
		}

		t = null;

		return domain;
	}

	public static String getChartset(byte[] bytes)
	{
		String code = null;
		
		UniversalDetector detector =  new UniversalDetector(null);
	    detector.handleData(bytes, 0, bytes.length);  
	    detector.dataEnd();  
	    code = detector.getDetectedCharset();  
	    detector.reset();  
	    if (code == null) 
	    {  
	    	code = "utf-8";  
	    }  
		/*
		if (bytes == null || bytes.length < 2)
		{
			return code;
		}

		int p = ((int) bytes[0] & 0x00ff) << 8 | ((int) bytes[1] & 0x00ff);
		switch (p)
		{
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}
		*/
		return code;

	}
	/*
	 * public static void main(String[] args) { long a =
	 * Format.ip2long("192.168.1.2"); System.out.println(a); }
	 */
	/*
	 * public static void main(String[] args) { try { HashMap<String,Row> map =
	 * new HashMap<String,Row>(); Row r = new Row(); r.putString("rrr", "rvrv");
	 * map.put("aaa", r); System.out.println(Format.getMapString(map));
	 * 
	 * String key = "aa123456"; String str = encodeDes(key,
	 * "http://s.click.taobao.com/t?e=m%3D2%26s%3DWXz%2BpQdcHYccQipKwQzePOeEDrYVVa64Qih%2F7PxfOKS5VBFTL4hn2dFYoGP7L3a4NGaA%2Fv7qa0ST6eDNmvF6jBLOI4%2FU6Dke38XymDefB1EyH37S5WIg5fE%2FJZ20M53%2Bcy7llBOuH5ssPuCO17knxsYOae24fhW0"
	 * ); System.out.println(str); System.out.println(decodeDes(key,str));
	 * 
	 * //http://wap.tk.woso100.com/nsfUlTdTr8Y_tR7WXBWIgfbSHVc7VQ3Dh/B0OvI3cIUDlFk
	 * /zyY0zbKAwtNrdnLfMLxpnJVSq2Yc5MitswkRI72p
	 * System.out.println(decodeDes("UlTdTr8Y",
	 * "tR7WXBWIgfbSHVc7VQ3Dh/B0OvI3cIUDlFk/zyY0zbKAwtNrdnLfMLxpnJVSq2Yc5MitswkRI72p"
	 * )); } catch (InvalidKeyException e) { Log.OutException(e); } catch
	 * (UnsupportedEncodingException e) { Log.OutException(e); } catch
	 * (NoSuchAlgorithmException e) { Log.OutException(e); } catch
	 * (NoSuchPaddingException e) { Log.OutException(e); } catch
	 * (InvalidKeySpecException e) { Log.OutException(e); } }
	 */
}