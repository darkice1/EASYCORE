package easy.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

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

public abstract class Sql
{
	//System.getProperty("java.io.tmpdir"))
	protected final String CACHEPATH = Config.getProperty("DBCACHE",System.getProperty("java.io.tmpdir"));
	protected final long CACHEKEEPTIME = Long.parseLong(Config.getProperty("CACHEKEEPTIME","600000"));

	protected Connection conn;

	protected Statement stmt;

	//protected ResultSet rs;

	protected String user = "";

	protected String password = "";

	protected String db = "";

	protected String host;

	protected int port;

	protected String jdbcurl = "";

	protected PreparedStatement ps;

	protected int resultSetType = ResultSet.TYPE_FORWARD_ONLY;

	protected int resultSetConncurrency = ResultSet.CONCUR_READ_ONLY;

	protected Properties info = new Properties();
	
	protected long upcount=0;
	protected long insertcount=0;
	protected long deltcount=0;

	/**
	 * ������ݿ⣬�û�������,ip��
	 */
	protected abstract void init();
	protected boolean isinit = false;

	/**
	 * ��ʼ����ݿ�
	 */
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
		DataSet ds =  executeQuery(sql);
		sql = null;
		return ds;
	}

	/**
	 * 数据库查询
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public DataSet executeQuery(String sql) throws SQLException
	{
		try
		{
			Log.OutSql(sql);
			ResultSet rs = getStmt().executeQuery(sql);
			DataSet ds = new DataSet(rs);
			rs.close();
			rs=null;
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
		DataSet ds =  executeQueryCache(sql);
		sql = null;
		return ds;
	}
	
	public DataSet executeQueryCache(long keeptime,String formate,Object... strs) throws SQLException, IOException
	{
		String sql = String.format(formate,strs);
		DataSet ds = executeQueryCache(sql,keeptime);
		sql = null;
		return ds;
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
			File file = new File(CACHEPATH);
			boolean pathok =false;
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
				throw new IOException(String.format("%s not find.", CACHEPATH));
			}
			
			sql = sql.trim();
			/*
			user = Config.getProperty("DBUSER");
			password = Config.getProperty("DBPASSWORD");
			jdbcurl = Config.getProperty("DBURL");
			dbclass = Config.getProperty("DBCLASS");
			*/		

			String md5 = Format.Md5(String.format("%s %s %s %s %s",user,password,jdbcurl,jdbcurl, sql));
			String path = String.format("%s/%s.db", CACHEPATH,md5);
			//System.out.println(path);

			boolean neednew = false;
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
						ds = new DataSet();
						
						for (Row r: cds.getDataSet().getRowList())
						{
							ds.AddRow(r);
							r = null;
						}
						EDate d = new EDate();
						d.setTime(end);
						Log.OutSql(String.format("[CACHE/%s]%s",d,sql));
						
						d = null;
						cds = null;
						return ds;
					}
					
					cds = null;
				}
				catch (ClassNotFoundException e)
				{
					neednew = true;
				}
				catch (Exception e)
				{
					//Log.OutException(e);
					neednew = true;
				}
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
				FileLock fl = fo.getChannel().tryLock();
				if (fl != null)
				{
					JFile.writeGZipObject(fo, cds);
				}
				fo.close();
				
				cds = null;
			}
			file  = null;
			md5 = null;
			path = null;
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
				stmt = null;
			}
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
				conn = null;
			}
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
		int re =  executeUpdate(sql);
		sql = null;
		return re;
	}

	/**
	 * 数据库更新
	 * @param sql
	 * @return
	 */
	public int executeUpdate(String sql)
	{
		try
		{
			String sqlstr = checksql(sql);
			Log.OutSql(sql);
			int re = getStmt().executeUpdate(sqlstr);
			sqlstr= null;
			return re;
		}
		catch (SQLException ex)
		{
			if (ex.toString().indexOf("Data truncation: Data truncated for column") <= 0)
			{
				Log.OutException(ex, jdbcurl + "\r\n" + sql);
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
	
	/**
	 * 数据库更新（返回异常）
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int executeUpdateEx(String sql) throws SQLException
	{
		String sqlstr = checksql(sql);
		Log.OutSql(sql);
		int re = getStmt().executeUpdate(sqlstr);
		sqlstr = null;
		return re;
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
		char zero[] = { 0 };
		sql.replaceAll(new String(zero), "");
		zero = null;
		return sql;
	}

	public DataSet getResultSet() throws SQLException
	{
		return new DataSet(getStmt().getResultSet());
	}

	public void addBatch(String sql)
	{
		try
		{
			if (sql.length()>6)
			{
				String t = sql.substring(0,6).toLowerCase();
				
				if (t.equals("insert"))
				{
					insertcount++;
				}
				else if (t.equals("delete"))
				{
					deltcount++;
				}
				else if (t.equals("update"))
				{
					upcount++;
				}
				t = null;
				
			}
			getStmt().addBatch(sql);
			Log.OutSql(sql);
		}
		catch (Exception ex)
		{
			Log.OutException(ex, jdbcurl + "\r\n" + sql);
		}
	}

	/**
	 * @return the upcount
	 */
	public long getUpcount()
	{
		return upcount;
	}

	/**
	 * @return the insertcount
	 */
	public long getInsertcount()
	{
		return insertcount;
	}

	/**
	 * @return the deltcount
	 */
	public long getDeltcount()
	{
		return deltcount;
	}

	public int[] executeBatch()
	{
		try
		{
			upcount=0;
			insertcount=0;
			deltcount=0;
			return getStmt().executeBatch();
		}
		catch (Exception ex)
		{
			Log.OutException(ex, jdbcurl);
			return null;
		}
	}

	public Connection getConnection()
	{
		return conn;
	}

	public String getClobString(Clob clob)
	{
		try
		{
			return clob.getSubString(1l, (int) clob.length());
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
			pstmt = conn.prepareStatement(sql);
		}
		catch (Exception ex)
		{	
			Log.OutException(ex);
		}
		return pstmt;
	}

	public void prepareStatement(String sql) throws SQLException
	{
		ps = conn.prepareStatement(sql);
	}
	
	public Statement getStatement()
	{
		return getStmt();
	}
}
