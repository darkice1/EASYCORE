package easy.sql;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>
 * <i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i>
 * </p>
 * 
 * 表基础类
 * 
 * @version 1.0 (<i>2005-7-5 neo</i>)
 */

public class BaseTable
{
	protected String tablename;

	protected String viewname;

	protected String order = "id DESC";

	protected String fieldlist = "*";

	protected String where;

	protected Map<String, String> params = new HashMap<String, String>();

	protected Map<String, String> proparams = new HashMap<String, String>();

	protected final static String LASTSQL = "SELECT LAST_INSERT_ID() id";

	/**
	 * 初始化数据
	 * 
	 * @param ds
	 * @return
	 */
	protected DataSet initdate(DataSet ds)
	{
		return ds;
	}

	/**
	 * 添加一般项
	 * 
	 * @param field
	 * @param value
	 */
	public void Add(String field, String value)
	{
		params.put(field, value);
	}
	
	public void Add(String field, int value)
	{
		params.put(field, ""+value);
	}
	
	public void Add(String field, long value)
	{
		params.put(field, ""+value);
	}

	public void Add(String field, double value)
	{
		params.put(field, ""+value);
	}
	
	public void Add(String field, float value)
	{
		params.put(field, ""+value);
	}
	/**
	 * 添加存储过程或运算
	 * 
	 * @param field
	 * @param value
	 */
	public void AddPro(String field, String value)
	{
		proparams.put(field, value);
	}

	public void clear()
	{
		params.clear();
		proparams.clear();
	}

	/**
	 * 插入数据
	 * 
	 * @return -1
	 */
	public int Insert() throws SQLException
	{
		return Insert(false);
	}

	public int InsertDelayed() throws SQLException
	{
		CPSql sql = new CPSql();
		int re = sql.executeUpdateEx(getInsertString(true,false));
		sql.close();
		sql = null;
		return re;
	}

	/**
	 * 
	 * @param isreturn是否返回id
	 * @return isreturn=true返回id,isreturn=false返回-1
	 * @throws SQLException
	 */
	public int Insert(boolean isreturn) throws SQLException
	{
		CPSql sql = new CPSql();
		sql.executeUpdateEx(getInsertString());
		if (isreturn)
		{
			DataSet ds = sql.executeQuery(LASTSQL);
			int id = -1;
			if (ds.next())
			{
				id = ds.getInt("id");
			}
			sql.close();
			sql = null;
			ds = null;
			return id;
		}
		else
		{
			sql.close();
			sql = null;
			return -1;
		}
	}
	
	public String getReplaceString()
	{
		Iterator<Entry<String, String>> paramsfields = params.entrySet().iterator();
		Iterator<Entry<String, String>> profields = proparams.entrySet().iterator();

		
		StringBuffer sqlbuf;
		
		sqlbuf = new StringBuffer("REPLACE INTO ");

		sqlbuf.append(tablename);

		StringBuffer fieldbuf = new StringBuffer("(");
		StringBuffer valuebuf = new StringBuffer("(");

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();

			String field = entry.getKey(); 

			fieldbuf.append(field);
			fieldbuf.append(',');

			valuebuf.append(doValue(params.get(field)));
			valuebuf.append(',');
			
			field = null;
			entry = null;
		}
		// 存储过程等
		while (profields.hasNext())
		{
			Entry<String,String> entry =  profields.next();
			String field = entry.getKey(); 

			fieldbuf.append(field);
			fieldbuf.append(',');

			valuebuf.append(proparams.get(field));
			valuebuf.append(',');
			
			field = null;
			entry = null;
		}
		fieldbuf.setCharAt(fieldbuf.length() - 1, ')');
		valuebuf.setCharAt(valuebuf.length() - 1, ')');

		sqlbuf.append(fieldbuf);
		sqlbuf.append(" VALUES ");
		sqlbuf.append(valuebuf);
		
		paramsfields = null;
		profields = null;
		
		//String sql = sqlbuf.toString();
		
		//sqlbuf = null;
		fieldbuf = null;
		valuebuf = null;

		return sqlbuf.toString();
	}

	/**
	 * 
	 * @param isdelayed 是否延迟
	 * @param isignore 是否忽略错误
	 * @return
	 */
	public String getInsertString(boolean isdelayed,boolean isignore)
	{
		Iterator<Entry<String, String>> paramsfields = params.entrySet().iterator();
		Iterator<Entry<String, String>> profields = proparams.entrySet().iterator();

		String delayed ="", ignore = "";
		
		StringBuffer sqlbuf;
		if (isdelayed)
		{
			delayed = "DELAYED ";
		}

		if (isignore)
		{
			ignore = "IGNORE ";
		}
		
		sqlbuf = new StringBuffer(String.format("INSERT %s%sINTO ", delayed,ignore));

		sqlbuf.append(tablename);

		StringBuffer fieldbuf = new StringBuffer("(");
		StringBuffer valuebuf = new StringBuffer("(");

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();

			String field = entry.getKey(); 

			fieldbuf.append(field);
			fieldbuf.append(',');

			valuebuf.append(doValue(params.get(field)));
			valuebuf.append(',');
			
			field = null;
			entry = null;
		}
		// 存储过程等
		while (profields.hasNext())
		{
			Entry<String,String> entry =  profields.next();
			String field = entry.getKey(); 

			fieldbuf.append(field);
			fieldbuf.append(',');

			valuebuf.append(proparams.get(field));
			valuebuf.append(',');
			
			field = null;
			entry = null;
		}
		fieldbuf.setCharAt(fieldbuf.length() - 1, ')');
		valuebuf.setCharAt(valuebuf.length() - 1, ')');

		sqlbuf.append(fieldbuf);
		sqlbuf.append(" VALUES ");
		sqlbuf.append(valuebuf);
		
		paramsfields = null;
		profields = null;
		
		//String sql = sqlbuf.toString();
		
		//sqlbuf = null;
		fieldbuf = null;
		valuebuf = null;
		delayed = null;
		ignore = null;

		return sqlbuf.toString();
	}

	public String getInsertString()
	{
		return getInsertString(false,false);
	}

	/**
	 * 删除数据
	 * 
	 * @param where
	 *            删除条件
	 * @throws SQLException
	 */
	public void delete(String where) throws SQLException
	{
		CPSql sql = new CPSql();
		sql.executeUpdateEx(getdeleteString(where));
		sql.close();
		sql = null;
	}

	public String getdeleteString(String where) throws SQLException
	{
		StringBuffer sqlbuf = new StringBuffer("delete from ");
		sqlbuf.append(tablename);
		sqlbuf.append(" where ");
		sqlbuf.append(where);
		
		//String sql = sqlbuf.toString();
		//sqlbuf = null;

		return sqlbuf.toString();
	}

	/**
	 * 更新数据
	 * 
	 * @param where
	 * @return
	 * @throws SQLException
	 */
	public int update(String where) throws SQLException
	{
		CPSql sql = new CPSql();
		int re = sql.executeUpdateEx(getUpdateString(where));
		sql.close();
		sql = null;
		
		return re;
	}
	
	public int update(String format,Object... strs) throws SQLException
	{
		return update(String.format(format, strs));
	}
	
	public String getUpdateString(String format,Object... strs)
	{
		return getUpdateString(String.format(format, strs));
	}

	public String getUpdateString(String where)
	{
		Iterator<Entry<String, String>> paramsfields = params.entrySet().iterator();
		Iterator<Entry<String, String>> profields = proparams.entrySet().iterator();

		StringBuffer sqlbuf = new StringBuffer("UPDATE ");
		sqlbuf.append(tablename);
		sqlbuf.append(" SET ");

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();
			String field = entry.getKey(); 

			sqlbuf.append(field);
			sqlbuf.append('=');
			sqlbuf.append(doValue(params.get(field)));
			sqlbuf.append(',');
			
			field = null;
			entry = null;
		}
		// 存储过程等
		while (profields.hasNext())
		{
			Entry<String,String> entry =  profields.next();
			String field = entry.getKey(); 

			sqlbuf.append(field);
			sqlbuf.append('=');
			sqlbuf.append(proparams.get(field));
			sqlbuf.append(',');
			
			field = null;
			entry = null;
		}

		sqlbuf.setCharAt(sqlbuf.length() - 1, ' ');

		sqlbuf.append("WHERE ");
		sqlbuf.append(where);
		
		//String sql = sqlbuf.toString();
		//sqlbuf = null;
		paramsfields = null;
		profields = null;

		return sqlbuf.toString();
	}
	
	/**
	 * 插入/更新所有字段
	 * @return
	 */
	public String getInsertUpdateOnDuplAll()
	{
		Iterator<Entry<String, String>> paramsfields = params.entrySet().iterator();
		Iterator<Entry<String, String>> profields = proparams.entrySet().iterator();

		StringBuffer sqlbuf = new StringBuffer();

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();
			String field = entry.getKey(); 

			sqlbuf.append(field);
			sqlbuf.append('=');
			sqlbuf.append(doValue(params.get(field)));
			sqlbuf.append(',');
			
			field = null;
			entry = null;
		}
		// 存储过程等
		while (profields.hasNext())
		{
			Entry<String,String> entry =  profields.next();
			String field = entry.getKey(); 

			sqlbuf.append(field);
			sqlbuf.append('=');
			sqlbuf.append(proparams.get(field));
			sqlbuf.append(',');
			
			field = null;
			entry = null;
		}

		sqlbuf.setCharAt(sqlbuf.length() - 1, ' ');

		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(),sqlbuf.toString());
	}
	
	/**
	 * 
	 * @param fields 后面需要操作字段
	 * @param ops操作符 如果少写以最后一个为准
	 * @return
	 */
	public String getInsertUpdateOnDuplPro(String fields,String ops)
	{
		String[] fs = fields.split(",");
		String[] os = ops.split(",");
		StringBuffer buf = new StringBuffer();
		
		int olen = os.length-1;
		for (int i=0,len=fs.length; i<len; i++)
		{
			String f = fs[i];
			String fv = params.get(f);
			
			if (fv == null)
			{
				fv = proparams.get(f);
			}
			
			if (fv != null)
			{
				String o;
				if (i<=olen)
				{
					o = os[i];
				}
				else
				{
					o = os[olen];
				}
				buf.append(f);
				buf.append("=");
				buf.append(f);
				buf.append(o);
				buf.append(fv);
				buf.append(",");
			}
		}
		
		buf.setLength(buf.length()-1);
		
		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(),buf.toString());
	}
	
	public String getInsertUpdateOnDupl(String updateString)
	{
		//sql.executeUpdate(String.format("%s ON DUPLICATE KEY UPDATE %s", r.toString(),updateString));	
		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(),updateString);
	}
	
	public String getInsertUpdateOnDupl(String formate,Object... strs) 
	{
		String sql = String.format(formate,strs);
		return getInsertUpdateOnDupl(sql);
	}

	public void InsertUpdateOnDupl(String formate,Object... strs) throws SQLException
	{
		String sql = String.format(formate,strs);
		InsertUpdateOnDupl(sql);
	}
	
	public void InsertUpdateOnDupl(String updateString) throws SQLException
	{
		CPSql sql = new CPSql();
		sql.executeUpdateEx(getInsertUpdateOnDupl(updateString));
		sql.close();
		sql = null;
	}

	public DataSet select(String where, int startidx, int count)
			throws SQLException
	{
		StringBuffer sqlbuf = new StringBuffer("SELECT ");
		StringBuffer cbuf = new StringBuffer("SELECT COUNT(*) C FROM ");

		sqlbuf.append(fieldlist);
		sqlbuf.append(" FROM ");
		if (viewname == null)
		{
			sqlbuf.append(tablename);
			cbuf.append(tablename);
		}
		else
		{
			if (viewname.indexOf(" ") == -1)
			{
				sqlbuf.append(String.format("%s", viewname));
				cbuf.append(String.format("%s", viewname));
			}
			else
			{
				sqlbuf.append(String.format("(%s) t", viewname));
				cbuf.append(String.format("(%s) t", viewname));
			}
		}

		if (where != null && where.equals("") == false)
		{
			sqlbuf.append(" WHERE ");
			sqlbuf.append(where);

			cbuf.append(" WHERE ");
			cbuf.append(where);
		}

		if (order != null && order.equals("") == false)
		{
			sqlbuf.append(" ORDER BY ");
			sqlbuf.append(order);
		}

		if (startidx >= 0)
		{
			sqlbuf.append(" LIMIT ");
			sqlbuf.append(startidx);

			if (count >= 0)
			{
				sqlbuf.append(",");
				sqlbuf.append(count);
			}
		}
		// System.out.println(sqlbuf);
		/*
		 * ORACLE SELECT * FROM ( SELECT A.*, rownum r FROM ( SELECT * FROM
		 * Articlesn ORDER BY PubTime DESC ) A WHERE rownum <= PageUpperBound )
		 * B WHERE r > PageLowerBound;
		 */
		CPSql sql = new CPSql();

		DataSet cds = sql.executeQuery(cbuf.toString());
		DataSet ds = sql.executeQuery(sqlbuf.toString());

		if (cds.next())
		{
			ds.setCount(cds.getInt("C"));
		}
		sql.close();
		sql = null;
		cbuf = null;
		sqlbuf = null;
		
		initdate(ds);
		return ds;
	}

	public static String doValue(String value)
	{
		if (value == null || value.length() == 0)
		{
			return "''";
		}

		value = value.replaceAll("\\\\", "\\\\\\\\");
		value = value.replaceAll("'", "\\\\'");
		value = "'" + value + "'";

		return value;
	}

	/**
	 * @return Returns the tablename.
	 */
	public String getTablename()
	{
		return tablename;
	}

	/**
	 * @param tablename
	 *            The tablename to set.
	 */
	public void setTablename(String tablename)
	{
		this.tablename = tablename;
	}

	/**
	 * @return Returns the order.
	 */
	public String getOrder()
	{
		return order;
	}

	/**
	 * @param order
	 *            The order to set.
	 */
	public void setOrder(String order)
	{
		this.order = order;
	}

	public void setFieldlist(String fieldlist)
	{
		this.fieldlist = fieldlist;
	}

	public String getFieldlist()
	{
		return this.fieldlist;
	}

	public String getWhere()
	{
		return where;
	}

	public void setWhere(String where)
	{
		this.where = where;
	}
/*
	public static void main(String[] args)
	{
		BaseTable bt = new BaseTable();
		bt.setTablename("test");
		bt.Add("aa", "1");
		bt.Add("bb", "2");
		
		System.out.println(bt.getInsertUpdateOnDuplPro("aa,bb", "+,-"));
		
	}
*/
//	@Override
//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		//clear();
//		
//		tablename = null;
//		viewname = null;
//		order = null;
//		fieldlist = null;
//		where = null;
//		params = null;
//		proparams  = null;
//	}
}