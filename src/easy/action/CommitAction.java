package easy.action;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import easy.sql.BaseTable;
import easy.util.Log;

/**
 * <p>
 * <i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: 九州易软科技发展有限公司</i>
 * </p>
 * 
 * 自动将提交信息插入/更新表中. cmt开头为一般传入参数,格式cmt_表明_字段名。 cmtw开头为条件,格式cmtw_表明_字段名。update时使用,传入多个条件时为AND. cmts多个参数,格式cmts_表明_字段名。使用,分割. cmt_isupdate为标记，cmt_isupdate=true为更新。其他为新增，没有也为新增。
 * 
 * @version 1.0 (<i>2006-8-23 Neo</i>)
 */

public abstract class CommitAction extends Action
{
	protected Map<String, BaseTable> tablemap = new HashMap<String, BaseTable>();

	protected final static String CMT_HEAD = "cmt";

	protected final static String CMTW_HEAD = "cmtw";

	protected final static String CMTS_HEAD = "cmts";

	protected final static String ISUPDATE = "cmt_isupdate";

	protected final static String TRUE = "true";

	protected boolean isupdate = false;
	

	/**
	 * 初始化commit表,把需要commit数据的表名add进来。
	 */
	abstract protected void inittable();

	protected void add(String tablename)
	{
		BaseTable b = new BaseTable();
		b.setTablename(tablename);
		add(tablename, b);
	}

	/**
	 * 初始化commit表,把需要commit数据的表名与BaseTable一起add进来。 主要作用特殊对象。如每次需要更新时间。 就要先new 一个BaseTable,再AddPro("time","NOW()")之后在add这个BaseTable。
	 */
	protected void add(String tablename, BaseTable table)
	{
		String where = table.getWhere();
		if (where == null)
		{
			table.setWhere("1=1");
		}
		else
		{
			table.setWhere(String.format("(%s)", where));
		}
		tablemap.put(tablename, table);
	}

	protected void commitdate() throws SQLException
	{
		if (tablemap.size() > 0)
		{
			Enumeration<?> e = request.getParameterNames();
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();

				if (name.equals(ISUPDATE) && request.getParameter(name).toString().equals("true"))
				{
					isupdate = true;
				}
				else
				{
					String[] s = name.split("_", 3);
					if (s.length == 3)
					{
						if (s[0].equals(CMT_HEAD))
						{
							BaseTable b = tablemap.get(s[1]);
							if (b != null)
							{
								b.Add(s[2], request.getParameter(name));
							}
						}
						else if (s[0].equals(CMTW_HEAD))
						{
							BaseTable b = tablemap.get(s[1]);
							b.setWhere(b.getWhere() + String.format(" AND %s='%s'", s[2], request.getParameter(name)));
						}
						else if (s[0].equals(CMTS_HEAD))
						{
							BaseTable b = tablemap.get(s[1]);
							if (b != null)
							{
								String[] params = request.getParameterValues(name);
								StringBuffer buf = new StringBuffer();
								for (String p : params)
								{
									buf.append(p);
									buf.append(",");
								}
								buf.setLength(buf.length() - 1);
								b.Add(s[2], buf.toString());
							}
						}
					}
				}
			}
			String[] tns = tablemap.keySet().toArray(new String[0]);
			for (String t : tns)
			{
				BaseTable b = tablemap.get(t);
				if (isupdate)
				{
					b.update(b.getWhere());
				}
				else
				{
					b.Insert();
				}
			}
		}
	}

	/**
	 * @see easy.action.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		inittable();
		Exception commitex = null;
		try
		{
			commitdate();
		}
		catch (Exception e)
		{
			Log.OutException(e);
			commitex = e;
		}
		if (commitex != null)
		{
			error = "数据库错误："+commitex.toString();
			message = error;
		}
	}
}