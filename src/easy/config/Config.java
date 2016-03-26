package easy.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import easy.util.Log;

/**
 * <p>
 * <i>Copyright: Esay (c) 2005-2005 <br>
 * Company: Esay </i>
 * </p>
 * 
 * Config������
 * 
 * @version 1.0 ( <i>2005-7-4 neo </i>)
 */

public class Config
{
	private static Config CFG = null;

	private final Properties PPS;

	private Config()
	{
		PPS = new Properties();
	}

	private static Config getInstance()
	{
		if (CFG == null)
		{
			CFG = new Config();
			Config.load();
		}
		return CFG;
	}

	/**
	 * ��ȡconfig�ļ�
	 * 
	 * @return value
	 */
	public static void load()
	{
		CFG = new Config();
		File f = new File(CFG.getClass().getResource("Config.class").toString());

		String path = f.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getPath();
		path = new String(path.replaceAll("jar:", ""));
		path = new String(path.replaceAll("file\\:", ""));
		path = new String(path.replaceAll("file/:", ""));
		path = new String(path.replaceAll("%20", " "));
		path = new String(String.format("%s/config.txt", path));

		String path1 = f.getParentFile().getParentFile().getParentFile().getParentFile().getPath();
		path1 = new String(path1.replaceAll("jar:", ""));
		path1 = new String(path1.replaceAll("file\\:", ""));
		path1 = new String(path1.replaceAll("file/:", ""));
		path1 = new String(path1.replaceAll("%20", " "));
		path1 = new String(String.format("%s/config.txt", path1));


		CFG = Config.getInstance();
		InputStream is = null;
		try
		{
			is = new FileInputStream(path);
		}
		catch (Exception ex)
		{
			try
			{
				is = new FileInputStream(path1);
			}
			catch (Exception e)
			{
			}
		}
		try
		{
			if (is != null)
			{
				CFG.PPS.load(is);
				is.close();
			}
		}
		catch (IOException ex)
		{
			Log.OutException(ex);
		}
	}

	/**
	 * ȡ��keyֵ
	 * 
	 * @param key
	 * @return value
	 */
	public static String getProperty(String key)
	{
		try
		{
			String tmp = Config.getInstance().PPS.getProperty(key);
			if (tmp != null)
			{
				return new String(Config.getInstance().PPS.getProperty(key).getBytes("latin1"),"utf-8");
			}
			else
			{
				return null;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			Log.OutException(e);
			return "";
		}
	}
	
	/**
	 * ȡ��keyֵ
	 * 
	 * @param key
	 * @param defvalue
	 * @return value
	 */
	public static String getProperty(String key,String defvalue)
	{		
		try
		{
			String tmp = Config.getInstance().PPS.getProperty(key,defvalue);
			if (tmp != null)
			{
				return new String(Config.getInstance().PPS.getProperty(key).getBytes("latin1"),"utf-8");
			}
			else
			{
				return null;
			}
		}
		catch (UnsupportedEncodingException e)
		{
			Log.OutException(e);
			return "";
		}
	}
	
	/**
	 * �޸�config��Ӧ��ֵ
	 * 
	 * @param key
	 * @param newvalue
	 * @param comment
	 */
	public static void setProperty(String key,String newvalue)
	{
		Config.getInstance().PPS.setProperty(key,newvalue);

//		File f = new File(CFG.getClass().getResource("Config.class").toString());
//
//		String path = f.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getPath();
//		path = path.replaceAll("jar:", "");
//		path = path.replaceAll("file\\:", "");
//		path = path.replaceAll("file/:", "");
//		path = path.replaceAll("%20", " ");
//		path += "/config.txt";
//
//		String path1 = f.getParentFile().getParentFile().getParentFile().getParentFile().getPath();
//		path1 = path1.replaceAll("jar:", "");
//		path1 = path1.replaceAll("file\\:", "");
//		path1 = path1.replaceAll("file/:", "");
//		path1 = path1.replaceAll("%20", " ");
//		path1 += "/config.txt";
//
//		CFG = Config.getInstance();
//		FileOutputStream fos = null;
//		try
//		{
//			fos = new FileOutputStream(path);
//		}
//		catch (Exception ex)
//		{
//			try
//			{
//				fos = new FileOutputStream(path1);
//			}
//			catch (Exception e)
//			{
//			}
//		}
//			
//		Config.getInstance().PPS.setProperty(key,newvalue);
//		Config.getInstance().PPS.store(fos,comment);
//		fos.close();
	}
}