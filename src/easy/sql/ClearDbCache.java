/**
 * 
 */
package easy.sql;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import easy.config.Config;
import easy.io.JFile;
import easy.util.Format;
import easy.util.Log;

/**
 * 清除数据库缓存文件
 * @author Neo(starneo@gmail.com)2014-12-29
 *
 */
public class ClearDbCache implements FileFilter
{
	public static void cleardbcache(String path)
	{
		File file = new File(path);
		ClearDbCache cdb = new ClearDbCache();
		File[] fs =  file.listFiles(cdb);
		int total = fs.length;
		int idx = 0;
		for (File f : fs)
		{
			idx++;
			try
			{
				String rpath = f.getAbsolutePath();
				CDataSet cds = (CDataSet)JFile.readGZipObject(rpath);
				if (cds.getEndtime() < System.currentTimeMillis())
				{
					Log.OutLog("[%d/%d]删除[%s][%s]",idx,total,rpath,f.delete());
				}
			}
			catch (ClassNotFoundException e)
			{
				Log.OutException(e);
			}
			catch (IOException e)
			{
				Log.OutException(e);
			}
		}
		
	}

	@Override
	public boolean accept(File file)
	{
		String filename = new String(file.getName());
		String ext = Format.getFileExtName(filename);
		if ("db".equals(ext))
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
		boolean isexist;
		String PID ="ClearDbCache.pid";
		if (args.length > 0)
		{
			PID = args[0];
		}
		
		try
		{
			JUnique.acquireLock(PID);
			isexist = false;
		}
		catch (AlreadyLockedException e1)
		{
			isexist = true;
		}
		if (isexist)
		{
			Log.OutLog("%s已开启，无需再次启动。",PID);
			System.exit(0);
		}
		
		String path = Config.getProperty("DBCACHE",System.getProperty("java.io.tmpdir"));
		Log.OutLog("读取目录[%s]",path);
		cleardbcache(path);
	}
}
