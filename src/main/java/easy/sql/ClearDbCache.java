package easy.sql;

import easy.config.Config;
import easy.io.JFile;
import easy.util.Format;
import easy.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import static easy.util.Tools.JUniqueOne;

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
		int delnum = 0;

		for (File f : fs)
		{
			idx++;
			try
			{
				String rpath = f.getAbsolutePath();
				CDataSet cds = (CDataSet)JFile.readGZipObject(rpath);
				if (cds.getEndtime() < System.currentTimeMillis())
				{
					delnum++;
					Log.OutLog("[%d/%d][%d]删除[%s][%s]",idx,total,delnum,rpath,f.delete());
				}
			}
			catch (ClassNotFoundException | IOException e)
			{
				Log.OutException(e);
			}
		}
		
	}

	@Override
	public boolean accept(File file)
	{
		String filename = file.getName();
		String ext = Format.getFileExtName(filename);
		return "db".equals(ext);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		JUniqueOne("ClearDbCache.pid");
		
		String path = Config.getProperty("DBCACHE",System.getProperty("java.io.tmpdir"));
		Log.OutLog("读取目录[%s]",path);
		cleardbcache(path);
	}
}
