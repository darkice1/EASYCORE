/**
 * 
 */
package easy.sql;

import java.util.ArrayList;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2010</i></p>
 *
 * 批量插入处理
 *
 * @version 1.0 (<i>2010-2-5 neo(starneo@gmail.com)</i>)
 */

public class BatchInsert
{
	private String tablename;
	private String fields;
	
	private int size = Integer.MAX_VALUE;
	
	private ArrayList <StringBuffer> sqllist = new ArrayList <StringBuffer>();
	private ArrayList <String> fieldlist = new ArrayList <String>();
	
	private int idx = 0;
	
	private StringBuffer sqlbuf;
	
	/**
	 * 
	 * @param tablename 表名
	 * @param fields  字段名以,分割
	 */
	public BatchInsert (String tablename,String fields)
	{
		this.tablename = tablename;
		this.fields = fields;
		
		String[] t = fields.split(",");
		for (String f: t)
		{
			f = new String(f.trim());
			fieldlist.add(f);
			f = null;
		}
		t =null;
	}
	
	
	public void Add (BaseTable bt)
	{
		if (idx == 0)
		{
			sqlbuf = new StringBuffer(); 
			sqllist.add(sqlbuf);

			sqlbuf.append(String.format("INSERT INTO %s (%s) VALUES (", tablename,fields));		
		}
		else
		{
			sqlbuf.append(",(");
		}
		
				
		for (int i=0,len=fieldlist.size(); i<len; i++)
		{
			String f = fieldlist.get(i);
			
			String v = bt.params.get(f);
			if (v != null)
			{
				sqlbuf.append(BaseTable.doValue(v));
			}
			else
			{
				v = bt.proparams.get(f);
				if (v != null)
				{
					sqlbuf.append(v);
				}
				else
				{
					sqlbuf.append("''");
				}
			}
			if (i+1<len)
			{
				sqlbuf.append(",");
			}
			v = null;
			f = null;
		}
		sqlbuf.append(")");
		
		if (++idx >= size)
		{
			//System.out.println("###"+sqlbuf);
			idx=0;
		}
		sqllist.set(sqllist.size()-1, sqlbuf);
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size)
	{
		this.size = size;
	}
	
	/**
	 * @return the sqllist
	 */
	public ArrayList<StringBuffer> getSqllist()
	{
		return sqllist;
	}
	
	public int executeUpdate (CPSql sql)
	{
		return executeUpdateOnDupl(sql,null);
	}
	
	public int executeUpdateIgnore (CPSql sql)
	{
		int count=0;
		for (StringBuffer r : sqllist)
		{
			//INSERT INTO
			//INSERT IGNORE INTO
			String sqlstr = r.toString();
			sqlstr = sqlstr.replaceFirst("INSERT INTO", "INSERT IGNORE INTO");
			
			count += sql.executeUpdate(sqlstr);
			r = null;
		}
		return count;
	}

	public int executeUpdateOnDupl (CPSql sql,String updateString)
	{
		int count=0;
		for (StringBuffer r : sqllist)
		{
			if (updateString == null)
			{
				count += sql.executeUpdate(r.toString());
			}
			else
			{
				count += sql.executeUpdate(String.format("%s ON DUPLICATE KEY UPDATE %s", r.toString(),updateString));
			}
			r = null;
		}
		return count;
	}
	
	public void clear()
	{
		idx = 0;
		sqllist.clear();		
	}
	
//	@Override
//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		
//		sqllist = null;
//		fieldlist  = null;
//		tablename = null;
//		fields = null;
//		sqlbuf = null;
//	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		long starttime = System.currentTimeMillis();
		BatchInsert bi = new BatchInsert("test","name,test,test1,test2,INSERT INTO");
		
		BaseTable bt = new BaseTable();
		
		for (int i=0;i<10000;i++)
		{
			bt.Add("name", "name"+i);
			bt.Add("test", "test"+i);
			bt.Add("test1", "test1_"+i);
			bt.Add("test2", "test2_"+i);
			bt.Add("INSERT INTO", "INSERT INTO_"+i);
			
			bi.Add(bt);
			//sqlstr = sqlstr.replace("INSERT INTO", "INSERT IGNORE INTO");
		}
		
		//System.out.println("len:"+bi.getSqllist().get(0).length());
		CPSql sql = new CPSql();
		bi.executeUpdateIgnore(sql);
		sql.close();
		
		long endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);
	}
}