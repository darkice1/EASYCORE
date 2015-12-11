/**
 * 
 */
package easy.sql;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import easy.config.Config;
import easy.io.JFile;
import easy.util.EDate;
import easy.util.Log;

/**
 * 将sql执行存入文件方便顺序执行
 * @author Neo(starneo@gmail.com)2014-3-13
 *
 */
public class SqlExeCache implements Runnable,FileFilter
{
	protected static String DBEXECACHEPATH = Config.getProperty("DBEXECACHEPATH",System.getProperty("java.io.tmpdir"));
	protected static String DBEXECACHEEXT = Config.getProperty("DBEXECACHEEXT","ec");
	protected static int DBEXECACHEMAXTHREAD = Integer.parseInt(Config.getProperty("DBEXECACHEMAXTHREAD","5"));
	
	protected ConcurrentLinkedQueue <String> filelist;
	private int idx;

	/**
	 * 
	 */
	public SqlExeCache()
	{
	}
	
	public static void writeCache(final String tablename,final String formate,Object... strs)
	{
		String sql = String.format(formate,strs);
		writeCache(tablename,sql);
	}
	
	/**
	 * 写入缓存
	 * @param tablename 表名
	 * @param sql
	 */
	public static void writeCache(final String tablename,final String sql)
	{
		String filename = String.format("%s/%s_%s_%f.%s", DBEXECACHEPATH,tablename,EDate.getLogDate(new Date()),Math.random(),DBEXECACHEEXT);
		String tmpfilename = String.format("%s_tmp", filename);
		JFile file = new JFile(tmpfilename);
		file.WriteText(sql);
		file.close();
		JFile.renameTo(tmpfilename, filename);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		writeCache("test1","sql你好");
//		writeCache("test2","sql你好");
//		writeCache("test3","sql你好");
//		writeCache("test4","sql你好");
//		writeCache("test5","sql你好");
//		writeCache("test6","sql你好");
//		writeCache("test7","sql你好");
//		writeCache("test8","sql你好");
//		writeCache("test9","sql你好");
//		writeCache("test10","sql你好");
//		writeCache("test11","sql你好");
		//放置每个线程的sql
		boolean isexist;
		final String PID = "sqlexecache.pid";
		try
		{
			JUnique.acquireLock(PID);
			isexist = false;
		}
		catch (AlreadyLockedException e1)
		{
			// Log.OutException(e1);
			isexist = true;
		}
		
		Log.OutLog("DBEXECACHEPATH:%s",DBEXECACHEPATH);
		Log.OutLog("DBEXECACHEEXT:%s",DBEXECACHEEXT);
		Log.OutLog("DBEXECACHEMAXTHREAD:%s",DBEXECACHEMAXTHREAD);
//		protected static String DBEXECACHEPATH = Config.getProperty("DBEXECACHEPATH",System.getProperty("java.io.tmpdir"));
//		protected static String DBEXECACHEEXT = Config.getProperty("DBEXECACHEEXT","ec");
//		protected static int DBEXECACHEMAXTHREAD = Integer.parseInt(Config.getProperty("DBEXECACHEMAXTHREAD","5"));
		// boolean isexist = JFile.exists(PID);
		if (isexist)
		{
			Log.OutLog("%s已开启,无需再次启动.", PID);
			System.exit(0);
		}
		CPSql sql = new CPSql();
		try
		{
			sql.executeQuery("select 1");
		}
		catch (SQLException e)
		{
			Log.OutException(e);
		}
		sql.close();
		
		List<ConcurrentLinkedQueue <String>> list = new ArrayList<ConcurrentLinkedQueue <String>>();

		//存表名与对应的所属数组
		HashMap<String,Integer> tablemap = new HashMap<String,Integer>();
		File file = new File(DBEXECACHEPATH);
		File[] fs =  file.listFiles(new SqlExeCache());
		//按时间排序
		Arrays.sort(fs, new Comparator<File>()
		{
			public int compare(File f1, File f2)
			{
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
				{
					return 1;
				}
				else if (diff == 0)
				{
					return 0;
				}
				else
				{
					return -1;
				}	
			}

			public boolean equals(Object obj)
			{
				return true;
			}

		});
		for (File f : fs)
		{
			//System.out.println(f.getName());
			String path = f.getAbsolutePath();
			
			String name = f.getName();
			String t[] = name.split("_");
			String tablename = t[0];
			//System.out.println(tablename);
			Integer idx = tablemap.get(tablename);
			
			if (idx == null)
			{
				//如果没找到
				//DBEXECACHEMAXTHREAD
				idx = tablemap.size() % DBEXECACHEMAXTHREAD;
				if (list.size() <= idx)
				{
					ConcurrentLinkedQueue <String> tlist = new ConcurrentLinkedQueue <String>();
					list.add(tlist);
				}
				tablemap.put(tablename, idx);
			}
			list.get(idx).offer(path);
		}
		
		int idx = 0;
		for (ConcurrentLinkedQueue <String> l : list)
		{
			Log.OutLog("初始化线程[%d]",idx);
			SqlExeCache sec = new SqlExeCache();
			sec.filelist = l;
			sec.idx = idx;
			new Thread(sec).start();
			idx++;
		}
		
//		System.out.println(tablemap.size());
//		for (ConcurrentLinkedQueue <String> t : list)
//		{
//			System.out.println(t.size());
//			for (String tt : t)
//			{
//				System.out.println(tt+"#"+new File(tt).lastModified());
//			}
//		}
	}
	
	public boolean accept(File file)
	{
		String filename = file.getName();

		if (filename.endsWith(DBEXECACHEEXT))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void run()
	{
		CPSql sql = new CPSql();
		int total = filelist.size();
		int c = 0;
		Log.OutLog("线程[%d]开始,共[%d]条",idx,total);
		while (filelist.isEmpty() == false)
		{
			c++;
			
			if (c % 100 == 0)
			{
				Log.OutLog("线程[%d][%d/%d]",idx,c,total);
			}
			
			String path =filelist.poll();
			JFile f = new JFile(path);
			String sqlstr = null;
			try
			{
				sqlstr = f.readAllText();
			}
			catch (IOException e)
			{
				Log.OutException(e);
			}
			f.close();
			
			if (sqlstr!= null)
			{
				sql.executeUpdate(sqlstr);
			}
			
			JFile.delete(path);
		}
		Log.OutLog("线程[%d][%d/%d]",idx,c,total);

		sql.close();
	}
}
