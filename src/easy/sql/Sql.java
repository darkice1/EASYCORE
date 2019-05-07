package easy.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import easy.config.Config;
import easy.io.JFile;
import easy.util.EDate;
import easy.util.Format;
import easy.util.Log;

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

	protected int resultSetType = ResultSet.TYPE_FORWARD_ONLY;//TYPE_FORWARD_ONLY

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
			if (file.exists() == false)
			{
				pathok = file.mkdir();
			}
			else
			{
				if (file.isDirectory() == false)
				{
					file.delete();
					pathok = file.mkdirs();
				}
				else
				{
					pathok = true;
				}
			}
			
			if (pathok==false)
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
			if (conn != null && conn.isClosed()==false)
			{
				conn.close();
			}
			if (connwrite != null && connwrite != conn && connwrite.isClosed()==false)
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
			if (ex.toString().indexOf("Data truncation: Data truncated for column") <= 0)
			{
				if (Format.isEmpty(jdbcurlwrite ))
				{
					jdbcurlwrite = jdbcurl;
				}
				Log.OutException(ex, jdbcurlwrite + "\r\n" + sql);
			}
			
			return -1;
		}

	}
	
	private Statement getStmt()
	{
		if (isinit == false)
		{
			instance();
			isinit = true;
		}
//		if (stmt==null)
//		{
//			initdb();
//		}
		return stmt;
	}

	private Statement getStmtWrite()
	{
		if (isinit == false)
		{
			instance();
			isinit = true;
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


	public int[] executeBatch()
	{
		try
		{
			return getStmtWrite().executeBatch();
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
		return conn;
	}

	public Connection getWriteConnection()
	{
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
}
