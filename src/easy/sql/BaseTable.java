package easy.sql;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

	protected Map<String, String> params = new LinkedHashMap<>();

	protected Map<String, String> proparams = new LinkedHashMap<>();

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
	
	public void AddRow(Row r)
	{
		for (String k : r.getColsNameList())
		{
			params.put(k, r.getString(k));
		}
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
		return re;
	}

	/**
	 * 
	 * @param isreturn 是否返回id
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
			return id;
		}
		else
		{
			sql.close();
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

		StringBuilder fieldbuf = new StringBuilder("(");
		StringBuilder valuebuf = new StringBuilder("(");

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();

			String field = entry.getKey(); 

			fieldbuf.append(field);
			fieldbuf.append(',');

			valuebuf.append(doValue(params.get(field)));
			valuebuf.append(',');

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

		}
		fieldbuf.setCharAt(fieldbuf.length() - 1, ')');
		valuebuf.setCharAt(valuebuf.length() - 1, ')');

		sqlbuf.append(fieldbuf);
		sqlbuf.append(" VALUES ");
		sqlbuf.append(valuebuf);


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

		StringBuilder fieldbuf = new StringBuilder("(");
		StringBuilder valuebuf = new StringBuilder("(");

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();

			String field = entry.getKey(); 

			fieldbuf.append(field);
			fieldbuf.append(',');

			valuebuf.append(doValue(params.get(field)));
			valuebuf.append(',');

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

		}
		fieldbuf.setCharAt(fieldbuf.length() - 1, ')');
		valuebuf.setCharAt(valuebuf.length() - 1, ')');

		sqlbuf.append(fieldbuf);
		sqlbuf.append(" VALUES ");
		sqlbuf.append(valuebuf);

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
	}

	public String getdeleteString(String where)
	{

		//String sql = sqlbuf.toString();
		//sqlbuf = null;

		return "delete from " + tablename + " where " + where;
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

		StringBuilder sqlbuf = new StringBuilder("UPDATE ");
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

		}

		sqlbuf.setCharAt(sqlbuf.length() - 1, ' ');

		sqlbuf.append("WHERE ");
		sqlbuf.append(where);
		
		//String sql = sqlbuf.toString();
		//sqlbuf = null;

		return sqlbuf.toString();
	}

	/**
	 * 更新字段按逗号分割
	 * @param fields
	 * @return
	 */
	public String getInsertUpdateOnDuplFields(String fields)
	{
		String[] fs = fields.split(",");
		return getInsertUpdateOnDuplFields(fs);
	}
	
	/**
	 * 需要更新字段
	 * @param fields
	 * @return
	 */
	public String getInsertUpdateOnDuplFields(String[] fields)
	{
		StringBuilder sqlbuf = new StringBuilder();
		for (String f : fields)
		{
			String v = params.get(f);
			if (v != null)
			{
				sqlbuf.append(f);
				sqlbuf.append('=');
				sqlbuf.append(doValue(params.get(f)));
				sqlbuf.append(',');
			}
			else
			{
				v = proparams.get(f);
				if (v != null)
				{
					sqlbuf.append(f);
					sqlbuf.append('=');
					sqlbuf.append(proparams.get(f));
					sqlbuf.append(',');
				}
			}
		}
		
		sqlbuf.setCharAt(sqlbuf.length() - 1, ' ');

		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(),sqlbuf.toString());
	}
	
	/**
	 * 插入/更新所有字段
	 * @return
	 */
	public String getInsertUpdateOnDuplAll()
	{
		return getInsertUpdateOnDuplAll(false);
	}
	
	public String getInsertUpdateOnDuplAll(boolean isdelayed)
	{
		Iterator<Entry<String, String>> paramsfields = params.entrySet().iterator();
		Iterator<Entry<String, String>> profields = proparams.entrySet().iterator();

		StringBuilder sqlbuf = new StringBuilder();

		// 一般属性
		while (paramsfields.hasNext())
		{
			Entry<String,String> entry =  paramsfields.next();
			String field = entry.getKey(); 

			sqlbuf.append(field);
			sqlbuf.append('=');
			sqlbuf.append(doValue(params.get(field)));
			sqlbuf.append(',');

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

		}

		sqlbuf.setCharAt(sqlbuf.length() - 1, ' ');

		return String.format("%s ON DUPLICATE KEY UPDATE %s", getInsertString(isdelayed,false),sqlbuf.toString());
	}
	
	/**
	 * 
	 * @param fields 后面需要操作字段
	 * @param ops 如果少写以最后一个为准
	 * @return
	 */
	public String getInsertUpdateOnDuplPro(String fields,String ops)
	{
		return getInsertUpdateOnDuplPro(fields,ops,false);
	}
	public String getInsertUpdateOnDuplPro(String[] fs,String[] os,boolean isdelayed)
	{
		StringBuilder buf = new StringBuilder();
		
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
		return String.format("%s ON DUPLICATE KEY UPDATE %s",getInsertString(isdelayed, false),buf.toString());
	}
	
	public String getInsertUpdateOnDuplPro(String fields,String ops,boolean isdelayed)
	{
		String[] fs = fields.split(",");
		String[] os = ops.split(",");
		
		return getInsertUpdateOnDuplPro(fs,os, isdelayed);
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
	}

	public DataSet select(String where, int startidx, int count)
			throws SQLException
	{
		StringBuilder sqlbuf = new StringBuilder("SELECT ");
		StringBuilder cbuf = new StringBuilder("SELECT COUNT(*) C FROM ");

		sqlbuf.append(fieldlist);
		sqlbuf.append(" FROM ");
		if (viewname == null)
		{
			sqlbuf.append(tablename);
			cbuf.append(tablename);
		}
		else
		{
			if (!viewname.contains(" "))
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

		if (where != null && !where.equals(""))
		{
			sqlbuf.append(" WHERE ");
			sqlbuf.append(where);

			cbuf.append(" WHERE ");
			cbuf.append(where);
		}

		if (order != null && !order.equals(""))
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
	
	/**
	 * 是否有信息
	 */
	public boolean hasinfo()
	{
		return !params.isEmpty() || !proparams.isEmpty();
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