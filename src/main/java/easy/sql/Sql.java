package easy.sql;

import easy.config.Config;
import easy.io.JFile;
import easy.util.EDate;
import easy.util.Format;
import easy.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <i>Copyright: Easy (c) 2005-2005 <br>
 * Company: Easy </i>
 * </p>
 *
 * 数据库基类
 *
 * @version 1.0 ( <i>2005-7-5 neo </i>)
 */

public abstract class Sql implements  AutoCloseable
{
	//System.getProperty("java.io.tmpdir"))
	//protected final String CACHEPATH = Config.getProperty("DBCACHE",System.getProperty("java.io.tmpdir"));
	protected final long CACHEKEEPTIME = Long.parseLong(Config.getProperty("CACHEKEEPTIME","600000"));

	protected Connection conn;
	protected Connection connwrite;

	protected Statement stmt;
	protected Statement stmtwrite;

	//protected ResultSet rs;

	protected String user = "";
	protected String password = "";
	protected String db = "";
	protected String host;
	protected int port;
	protected String jdbcurl = "";


	protected String userwrite = "";
	protected String passwordwrite = "";
	protected String dbwrite = "";
	protected String hostwrite;
	protected int portwrite;
	protected String jdbcurlwrite = "";

	protected PreparedStatement ps;

	protected int resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;//TYPE_FORWARD_ONLY

	protected int resultSetConncurrency = ResultSet.CONCUR_READ_ONLY;


//	protected long upcount=0;
//	protected long insertcount=0;
//	protected long deltcount=0;


	protected abstract void init();
	protected boolean isinit = false;

	protected abstract void initdb();

	protected void instance()
	{
		init();
		initdb();

		isinit = true;
	}

	public Sql()
	{
	}

	public Sql(int resultSetType, int resultSetConncurrency)
	{
		this.resultSetType = resultSetType;
		this.resultSetConncurrency = resultSetConncurrency;
		instance();
	}

	public DataSet executeQuery(String formate,Object... strs) throws SQLException
	{
		String sql = String.format(formate,strs);
		return executeQuery(sql);
	}

	public static String getDBCachePath()
	{
		return Config.getProperty("DBCACHE",System.getProperty("java.io.tmpdir"));
	}


	public DataSet executeQuery(String sql) throws SQLException
	{
		try
		{
			Log.OutSql(sql);
			ResultSet rs = getStmt().executeQuery(sql);
			DataSet ds = new DataSet(rs);
			rs.close();
			return ds;
		}
		catch (SQLException ex)
		{
			Log.OutException(ex, jdbcurl + "\r\n" + sql);
			throw ex;
		}
	}

	public DataSet executeQueryCache(String formate,Object... strs) throws SQLException, IOException
	{
		String sql = String.format(formate,strs);
		return executeQueryCache(sql);
	}

	public DataSet executeQueryCache(long keeptime,String formate,Object... strs) throws SQLException, IOException
	{
		String sql = String.format(formate,strs);
		return executeQueryCache(sql,keeptime);
	}

	public DataSet executeQueryCache(String sql) throws SQLException, IOException
	{
		return executeQueryCache(sql,CACHEKEEPTIME);
	}

	public DataSet executeQueryCache(String sql,long keeptime) throws SQLException, IOException
	{
		DataSet ds = null;
		if (keeptime <= 0)
		{
			ds = executeQuery(sql);
		}
		else
		{
			File file = new File(getDBCachePath());
			boolean pathok;
			if (!file.exists())
			{
				pathok = file.mkdir();
			}
			else
			{
				if (!file.isDirectory())
				{
					file.delete();
					pathok = file.mkdirs();
				}
				else
				{
					pathok = true;
				}
			}

			if (!pathok)
			{
				throw new IOException(String.format("%s not find.", getDBCachePath()));
			}

			sql = sql.trim();
			/*
			user = Config.getProperty("DBUSER");
			password = Config.getProperty("DBPASSWORD");
			jdbcurl = Config.getProperty("DBURL");
			dbclass = Config.getProperty("DBCLASS");
			*/

			String md5 = Format.Md5(String.format("%s %s %s %s %s",user,password,jdbcurl,jdbcurl, sql));
			String path = String.format("%s/%s.db", getDBCachePath(),md5);
			//System.out.println(path);

			boolean neednew;
			if (JFile.exists(path))
			{
				try
				{
					CDataSet cds = (CDataSet)JFile.readGZipObject(path);

					long now = System.currentTimeMillis();
					long end = cds.getEndtime();

					if (now > end)
					{
						neednew = true;
					}
					else
					{
						ds = cds.getDataSet();
						ds.setCount(-1);
//						System.out.println(ds.getCursor());
//						ds = new DataSet();
//
//						List<Row> list = cds.getDataSet().getRowList();
//						for (Row r: list)
//						{
//							ds.AddRow(r);
//							r = null;
//						}
//						ds.setRowList(cds.getDataSet().getRowList());
						EDate d = new EDate();
						d.setTime(end);
						Log.OutSql(String.format("[CACHE/%s]%s",d,sql));

						return ds;
					}

				}
				catch (Exception e)
				{
					neednew = true;
				}//					Log.OutException(e);

			}
			else
			{
				neednew = true;
			}

			if (neednew)
			{
				ds = executeQuery(sql);
				CDataSet cds = new CDataSet();

				long now = System.currentTimeMillis();
				long endtime = now + keeptime;

				cds.setStarttime(now);
				cds.setEndtime(endtime);
				cds.setDataSet(ds);
				cds.setSql(sql);


				try
				{
					FileOutputStream fo = new FileOutputStream(path);
					try
					{
						FileLock fl = fo.getChannel().tryLock();
						if (fl != null)
						{
							JFile.writeGZipObject(fo, cds);
						}
					}
					catch (OverlappingFileLockException e)
					{
						//Log.OutException(e);
					}

					fo.close();
				}
				catch (FileNotFoundException e)
				{
					Log.OutException(e);
				}
			}
		}


		return ds;
	}

	public void close()
	{
//		try
//		{
//			if (rs != null)
//			{
//				rs.close();
//			}
//		}
//		catch (SQLException ex)
//		{
//			Log.OutException(ex);
//		}

		try
		{
			//&& stmt.isClosed()==false
			if (stmt != null )
			{
				stmt.close();
			}
			if (stmtwrite != null && stmtwrite != stmt)
			{
				stmtwrite.close();
			}

			stmt = null;
			stmtwrite = null;
		}
		catch (SQLException ex)
		{
			Log.OutException(ex);
		}

		try
		{
			if (conn != null && !conn.isClosed())
			{
				conn.close();
			}
			if (connwrite != null && connwrite != conn && !connwrite.isClosed())
			{
				connwrite.close();
			}
			conn = null;
			connwrite = null;
		}
		catch (SQLException ex)
		{
			Log.OutException(ex, jdbcurl);
		}
	}

	public boolean isClosed()
	{
		boolean re;
		try
		{
			if (conn !=null)
			{
				re = conn.isClosed();
			}
			else
			{
				return true;
			}
		}
		catch (Exception ex)
		{
			Log.OutException(ex);
			return true;
		}

		return re;
	}

	public int executeUpdate(String formate,Object... strs)
	{
		String sql = String.format(formate,strs);
		return executeUpdate(sql);
	}

	public int executeUpdate(String sql)
	{
		try
		{
			String sqlstr = checksql(sql);
			Log.OutSql(sql);
			return getStmtWrite().executeUpdate(sqlstr);
		}
		catch (SQLException ex)
		{
			if (Format.isEmpty(jdbcurlwrite ))
			{
				jdbcurlwrite = jdbcurl;
			}
			Log.OutException(ex, jdbcurlwrite + "\r\n" + sql);

			return -1;
		}
	}

	private Statement getStmt()
	{
		if (!isinit)
		{
			instance();
		}
//		if (stmt==null)
//		{
//			initdb();
//		}
		return stmt;
	}

	private Statement getStmtWrite()
	{
		if (!isinit)
		{
			instance();
		}
		if (stmtwrite == null)
		{
			stmtwrite = stmt;
		}

		//		if (stmt==null)
		//		{
		//			initdb();
		//		}
		return stmtwrite;
	}

	public int executeUpdateEx(String sql) throws SQLException
	{
		String sqlstr = checksql(sql);
		Log.OutSql(sql);
		return getStmtWrite().executeUpdate(sqlstr);
	}

//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		if (isClosed() == false)
//		{
//			close();
//		}
//		conn = null;
//		stmt = null;
//		user = null;
//		password = null;
//		db = null;
//		host = null;
//		jdbcurl = null;
//		ps = null;
//		info = null;
//	}

	protected String checksql(String sql)
	{
		final char[] zero = {0};
		return Format.replaceAll(sql,new String(zero), "");
	}

	public DataSet getResultSet() throws SQLException
	{
		return new DataSet(getStmt().getResultSet());
	}


	public void addBatch(String formate,Object... strs)
	{
		String sql = String.format(formate,strs);
		addBatch(sql);
	}

	public void addBatch(String sql)
	{
		try
		{
			getStmtWrite().addBatch(sql);
			Log.OutSql(sql);
		}
		catch (Exception ex)
		{
			if (Format.isEmpty(jdbcurlwrite ))
			{
				jdbcurlwrite = jdbcurl;
			}
			Log.OutException(ex, jdbcurlwrite + "\r\n" + sql);
		}
	}

	public int[] executeBatchEx() throws SQLException
	{
		return getStmtWrite().executeBatch();
	}

	public int[] executeBatch()
	{
		try
		{
			return executeBatchEx();
		}
		catch (Exception ex)
		{
			if (Format.isEmpty(jdbcurlwrite ))
			{
				jdbcurlwrite = jdbcurl;
			}
			Log.OutException(ex, jdbcurlwrite);
			return null;
		}
	}

	public Connection getConnection()
	{
		if (!isinit)
		{
			instance();
		}
		return conn;
	}

	public Connection getWriteConnection()
	{
		if (!isinit)
		{
			instance();
		}

		if (connwrite == null)
		{
			connwrite = conn;
		}
		return connwrite;
	}

	public String getClobString(Clob clob)
	{
		try
		{
			return clob.getSubString(1L, (int) clob.length());
		}
		catch (Exception ex)
		{
			Log.OutException(ex);
			return null;
		}
	}


	public PreparedStatement createPstmt(String sql)
	{
		PreparedStatement pstmt = null;
		try
		{
			pstmt = getWriteConnection().prepareStatement(sql);
		}
		catch (Exception ex)
		{
			Log.OutException(ex);
		}
		return pstmt;
	}

	public void prepareStatement(String sql) throws SQLException
	{
		ps = getWriteConnection().prepareStatement(sql);
	}

	public Statement getStatement()
	{
		return getStmt();
	}


	/**
	 * 同步表 list 内容到  dblist同步
	 * @param tablename
	 * @param keyfields
	 * @param valuesfields
	 * @param dblist 数据库里的数据
	 * @param list
	 */
	public void syncTable(String tablename, String[] keyfields, String[] valuesfields, List<Row> dblist, List<Row> list)
	{
//		System.out.println("list.size()"+list.size());
		Map<String,Row> map = new HashMap<>();
		for (Row r : list)
		{
			StringBuilder kb = new StringBuilder();
			for (String k : keyfields)
			{
				kb.append(r.getString(k));
			}
			String key = Format.getKey(kb.toString());
			map.put(key,r);
		}

		final int DELSIZE = 1000;

		int delnum = 0;

		StringBuilder delwhere = new StringBuilder();

		for (Row r : dblist)
		{
			StringBuilder kb = new StringBuilder();
			for (String k : keyfields)
			{
				kb.append(r.getString(k));
			}
			String key = Format.getKey(kb.toString());

			Row nr = map.get(key);

			if (nr == null)
			{
				//如果没有就生成删除
				delwhere.append(String.format(" OR (%s)",r.getWhereString(keyfields)));
				delnum++;

				if (delnum == DELSIZE)
				{
					addBatch("delete from %s where 0 %s",tablename,delwhere.toString());
					delwhere = new StringBuilder();
					delnum = 0 ;
				}
//				addBatch("delete from %s where %s",tablename,r.getWhereString(keyfields));
			}
			else
			{
				//如果有就检查是否一样 不一样更新
				boolean needupdate = false;
				BaseTable bt = new BaseTable();
				for (String k : valuesfields)
				{
					if (r.getString(k).equals(nr.getString(k)) == false)
					{
						needupdate = true;
						bt.Add(k,nr.getString(k));
					}
				}

/*				System.out.println(r);
				System.out.println(nr);
				System.out.println("----------------------------");*/

				if (needupdate)
				{
					bt.setTablename(tablename);
					addBatch(bt.getUpdateString(r.getWhereString(keyfields)));
				}
				//删除map里的row
				map.remove(key);
			}
		}
		if (delnum > 0)
		{
			addBatch("delete from %s where 0 %s",tablename,delwhere.toString());
		}
//		System.out.println("delnum.size()"+delnum);

		//		剩下的map是新的直接插入

		executeBatch();

		if (map.isEmpty() == false)
		{
			BatchInsert bi = null;
			final int INSERTSIZE = 1000;
			int insertnum = 0;
			for (String key : map.keySet())
			{
				Row r = map.get(key);
				if (bi == null)
				{
					bi= new BatchInsert(tablename,Format.toListString(r.getColsNameList()));
				}

				BaseTable bt = new BaseTable();
				bt.setTablename(tablename);
				bt.AddRow(r);

				bi.Add(bt);
				insertnum++;
				if (insertnum == INSERTSIZE)
				{
					bi.executeUpdate(this);
					bi = null;
					insertnum = 0;
				}
			}

			if (insertnum > 0)
			{
				bi.executeUpdate(this);
			}

//			System.out.println("insertnum.size()"+insertnum);

		}
	}



	public int[] preparedStatementExe(String sql, List<Row> list, String[] fiedls) throws SQLException
	{
		Connection con = getWriteConnection();
		int[] re;
		PreparedStatement ps = con.prepareStatement(sql);
		for (Row r : list)
		{
			for (int i=0,len=fiedls.length;i<len;i++)
			{
				ps.setString(i+1,r.getString(fiedls[i]));
			}
			ps.addBatch();
		}
		re = ps.executeBatch();

		return re;
	}
}
