package easy.io;

import easy.config.Config;
import easy.util.Format;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;


/**
 * @author Neo(starneo@gmail.com)2015年12月11日
 *
 */
public class FileCache implements FileFilter
{
	private final static String PATH = Config.getProperty("FILECACHEPATH",System.getProperty("java.io.tmpdir"));
	private final static String EXT = "ecache";
	
	public static String getCachePath()
	{
		return PATH;
	}
	
	private static String getFileName(String key)
	{
		return String.format("%s/%s.%s", PATH,Format.md5(key),EXT);
	}
	
	public static void setTxt(String key,String value)
	{
		setTxt(key,value,60000);
	}
	
	public static void setTxt(String key,String value,long timeout)
	{

		JFile file = new JFile(getFileName(key));
		String buf = (System.currentTimeMillis() + timeout) + "\n" + value;
		file.WriteText(buf);
		file.close();
	}
	
	public static String getTxt(String key)
	{
		String txt = null;
		JFile file = new JFile(getFileName(key));
		try
		{
			String t = file.readAllText();
			String[] ts = t.split("\n",2);
			if (Long.parseLong(ts[0]) >= System.currentTimeMillis())
			{
				txt = ts[1];
			}
		}
		catch (IOException ignored)
		{
		}
		file.close();

		return txt;
	}

	@Override
	public boolean accept(File file)
	{
		String filename = file.getName();
		String ext = Format.getFileExtName(filename);
		return EXT.equals(ext);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		setTxt("aaa","123",1000);
		System.out.println(getTxt("aaa"));
	
	}

}
