package easy.config;

import easy.io.JFile;
import easy.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

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
//		System.out.println(filepath);
		CFG = Config.getInstance();

		InputStream is = new FileInputStream(new File(filepath));

		try
		{
			CFG.PPS.load(is);
			is.close();
		}
		catch (IOException ex)
		{
			Log.OutException(ex);
		}
	}

	private static String getConfigPath(String startpath)
	{
		final String CONFIGNAME = "/config.txt";

		String fpath = null;
		File cf = new File(startpath);

		while(true)
		{
			//			System.out.println(cf.getPath());
			String tpath = cf.getPath();
			if (JFile.exists(tpath+CONFIGNAME))
			{
				fpath = tpath+CONFIGNAME;
				break;
			}
			if (JFile.exists(tpath+"/WEB-INF"+CONFIGNAME))
			{
				fpath = tpath+CONFIGNAME;
				break;
			}


			if (tpath.equals("/"))
			{
				break;
			}
			else
			{
				cf = new File(cf.getParent());
			}
		}

		return fpath;
	}

	public static void load()
	{
		CFG = new Config();
		String cpath = CFG.getClass().getResource("/").getPath();
		if (cpath.indexOf("file:") == 0)
		{
			cpath = cpath.substring(5);
		}

//		System.out.println(String.format("载入配置文件错误[%s]",cpath));

		String fpath = getConfigPath(cpath);

		try
		{
			load(fpath);
		}
		catch (Exception ex)
		{
			System.out.println(String.format("载入配置文件错误[%s]->[%s]",cpath,fpath));
			ex.printStackTrace();
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