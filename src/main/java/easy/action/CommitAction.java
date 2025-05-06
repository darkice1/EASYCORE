package easy.action;

import easy.sql.BaseTable;
import easy.util.Log;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: ��������Ƽ���չ���޹�˾</i>
 * </p>
 * 
 * �Զ����ύ��Ϣ����/���±���. cmt��ͷΪһ�㴫�����,��ʽcmt_����_�ֶ����� cmtw��ͷΪ����,��ʽcmtw_����_�ֶ�����updateʱʹ��,����������ʱΪAND. cmts�������,��ʽcmts_����_�ֶ�����ʹ��,�ָ�. cmt_isupdateΪ��ǣ�cmt_isupdate=trueΪ���¡�����Ϊ������û��ҲΪ������
 * 
 * @version 1.0 (<i>2006-8-23 Neo</i>)
 */

public abstract class CommitAction extends Action
{
	protected Map<String, BaseTable> tablemap = new HashMap<>();

	protected final static String CMT_HEAD = "cmt";

	protected final static String CMTW_HEAD = "cmtw";

	protected final static String CMTS_HEAD = "cmts";

	protected final static String ISUPDATE = "cmt_isupdate";

//	protected final static String TRUE = "true";

	protected boolean isupdate = false;
	

	/**
	 * ��ʼ��commit��,����Ҫcommit���ݵı���add������
	 */
	abstract protected void inittable();

	protected void add()
	{
		BaseTable b = new BaseTable();
		b.setTablename("democommit1");
		add("democommit1", b);
	}

	/**
	 * ��ʼ��commit��,����Ҫcommit���ݵı�����BaseTableһ��add������ ��Ҫ�������������ÿ����Ҫ����ʱ�䡣 ��Ҫ��new һ��BaseTable,��AddPro("time","NOW()")֮����add���BaseTable��
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
		if (!tablemap.isEmpty())
		{
			Enumeration<?> e = request.getParameterNames();
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();

				if (name.equals(ISUPDATE) && request.getParameter(name).equals("true"))
				{
					isupdate = true;
				}
				else
				{
					String[] s = name.split("_", 3);
					if (s.length == 3)
					{
						switch (s[0])
						{
							case CMT_HEAD:
							{
								BaseTable b = tablemap.get(s[1]);
								if (b != null)
								{
									b.Add(s[2], request.getParameter(name));
								}
								break;
							}
							case CMTW_HEAD:
							{
								BaseTable b = tablemap.get(s[1]);
								b.setWhere(b.getWhere() + String.format(" AND %s='%s'", s[2], request.getParameter(name)));
								break;
							}
							case CMTS_HEAD:
							{
								BaseTable b = tablemap.get(s[1]);
								if (b != null)
								{
									String[] params = request.getParameterValues(name);
									StringBuilder buf = new StringBuilder();
									for (String p : params)
									{
										buf.append(p);
										buf.append(",");
									}
									buf.setLength(buf.length() - 1);
									b.Add(s[2], buf.toString());
								}
								break;
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
			error = "error:"+ commitex;
			message = error;
		}
	}
}