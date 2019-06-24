/**
 * 
 */
package easy.sql;

import java.io.Serializable;

/**
 * 存储cache
 * @author Neo(starneo@gmail.com)2013-12-11
 *
 */

public class CDataSet implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DataSet ds;
	private String sql;
	private long starttime;
	private long endtime;

//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		sql = null;
//		ds = null;
//	}
	
	/**
	 * 
	 */
	public CDataSet()
	{
	}

	public DataSet getDataSet()
	{
		return ds;
	}

	public void setDataSet(DataSet ds)
	{
		this.ds = ds;
	}

	public String getSql()
	{
		return sql;
	}

	public void setSql(String sql)
	{
		this.sql = sql;
	}

	public long getStarttime()
	{
		return starttime;
	}

	public void setStarttime(long starttime)
	{
		this.starttime = starttime;
	}

	public long getEndtime()
	{
		return endtime;
	}

	public void setEndtime(long endtime)
	{
		this.endtime = endtime;
	}

//	/**
//	 * @param args
//	 * @throws SQLException 
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) throws SQLException, IOException
//	{
//		CPSql sql = new CPSql();
//		DataSet ds = sql.executeQueryCache(" select now() c ");
//		while (ds.next())
//		{
//			System.out.println(ds.getString("c"));
//		}
//		sql.close();
//
//	}
}
