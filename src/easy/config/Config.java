package easy.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
	
	public static void load(String filepath) throws FileNotFoundException
	{
		CFG = Config.getInstance();

		InputStream is = new FileInputStream(new File(filepath));

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

	public static void load()
	{
		CFG = new Config();
		File f = new File(CFG.getClass().getResource("Config.class").toString());

		String path = f.getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getPath();
		path = path.replaceAll("jar:", "");
		path = path.replaceAll("file\\:", "");
		path = path.replaceAll("file/:", "");
		path = path.replaceAll("%20", " ");
		path = String.format("%s/config.txt", path);

		String path1 = f.getParentFile().getParentFile().getParentFile().getParentFile().getPath();
		path1 = path1.replaceAll("jar:", "");
		path1 = path1.replaceAll("file\\:", "");
		path1 = path1.replaceAll("file/:", "");
		path1 = path1.replaceAll("%20", " ");
		path1 = String.format("%s/config.txt", path1);


		try
		{
			load(path);
		}
		catch (Exception ex)
		{
			try
			{
				load(path1);
			}
			catch (Exception ignored)
			{
			}
		}
	}

	public static String getProperty(String key)
	{
		try
		{
			String tmp = Config.getInstance().PPS.getProperty(key);
			if (tmp != null)
			{
				return new String(tmp.getBytes("latin1"), StandardCharsets.UTF_8);
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
	

	public static String getProperty(String key,String defvalue)
	{		
		try
		{
			String tmp = Config.getInstance().PPS.getProperty(key,defvalue);
			if (tmp != null)
			{
				return new String(tmp.getBytes("latin1"), StandardCharsets.UTF_8);
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

	
	public static String getString()
	{
		if (CFG == null)
		{
			getInstance();
		}
		return CFG.PPS.toString();
	}
}