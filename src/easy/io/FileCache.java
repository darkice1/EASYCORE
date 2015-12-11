/**
 * 
 */
package easy.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import easy.config.Config;
import easy.util.Format;


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
		return String.format("%s/%s.%s", PATH,Format.Md5(key),EXT);
	}
	
	public static void setTxt(String key,String value)
	{
		setTxt(key,value,60000);
	}
	
	public static void setTxt(String key,String value,long timeout)
	{
		
		StringBuffer buf = new StringBuffer();
		buf.append(System.currentTimeMillis()+timeout);
		buf.append("\n");
		buf.append(value);
		
		JFile file = new JFile(getFileName(key));
		file.WriteText(buf.toString());
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
		catch (IOException e)
		{
		}
		file.close();

		return txt;
	}

	@Override
	public boolean accept(File file)
	{
		String filename = new String(file.getName());
		String ext = Format.getFileExtName(filename);
		if (EXT.equals(ext))
		{
			return true;
		}
		
		return false;
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
