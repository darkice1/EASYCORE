package easy.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import easy.config.Config;
import easy.servlet.PageInfo;
import easy.sql.CPSql;
import easy.sql.DataSet;
import easy.util.Format;
import easy.util.Log;

/**
 * �б�Action�� ������Ҫ��ʼ��sql������ҳ����pagesize,pagesizeĬ��Ϊconfig��DBDEFPAGESIZE��DBDEFPAGESIZE��δ����Ϊ20��
 * pagenumΪҳ�� ��requestȡ��������
 * srw_�ֶ���,srw(searchwhere)Ϊ�ƿ�ͷΪ������Ϣ����swr_id=1(Ϊid=1) srw_l_�ֶ���Ϊlikes使用(srw_l_name=abc表示Ϊname LIKE '%abc%')
 * 
 * @version 1.0 (<i>2006-7-23 Neo</i>)
 */


public abstract class ListXmlAction extends Action
{
	/**
	 * 缓存保留时间(ms)
	 */
	protected long cachetime = 0;
	
	protected String sql;

	protected String order;

	protected DataSet dataset;

	protected int pagesize = Integer.parseInt(Config.getProperty("DBDEFPAGESIZE", "20"));

	protected int pageNum = 1;

	protected void setPageNum()
	{
		String t = request.getParameter("pageNum");
		if (t != null)
		{
			try
			{
				pageNum = Integer.parseInt(t);
			}
			catch (NumberFormatException nfe)
			{
			}
		}
	}

	/**
	 * ��ʼ��sql������ҳ����pagesize,pagesizeĬ��Ϊconfig��DBDEFPAGESIZE��DBDEFPAGESIZE��δ����Ϊ20��
	 */
	abstract protected void initList();
	
	/**
	 * 返回组合sql
	 * @param sql
	 * @return
	 */
	public String getInitSqlString(final String sqlstr)
	{
		String newsql = sql;
		
		StringBuffer tmpSql = new StringBuffer();
		boolean isset = false;
		Enumeration<?> e = request.getHttpServletRequest().getParameterNames();
		while (e.hasMoreElements())
		{
			String temp = (String) e.nextElement();
			if (temp.startsWith("srw_"))
			{
				if (isset == false)
				{
					isset = true;
					tmpSql.append("1=1 ");
				}
				if (temp.startsWith("srw_l_"))
				{
					tmpSql.append(String.format(" AND %s LIKE '%%%s%%'", temp.replaceFirst("srw_l_", ""), request.getParameter(temp)));
				}
				else if (temp.startsWith("srw_ne_"))
				{
					tmpSql.append(String.format(" AND %s <> '%s'", temp.replaceFirst("srw_ne_", ""), request.getParameter(temp)));
				}
				else if (temp.startsWith("srw_in_"))
				{
					if (request.getParameter(temp)!=null && request.getParameter(temp).equals("")==false)
					{
						tmpSql.append(String.format(" AND %s IN (%s)", temp.replaceFirst("srw_in_", ""), request.getParameter(temp)));
					}
				}
				else if (temp.startsWith("srw_nin_"))
				{
					if (request.getParameter(temp)!=null && request.getParameter(temp).equals("")==false)
					{
						tmpSql.append(String.format(" AND %s NOT IN (%s)", temp.replaceFirst("srw_nin_", ""), request.getParameter(temp)));
					}
				}
				else if (temp.startsWith("srw_w"))
				{
					if (request.getParameter(temp)!=null && request.getParameter(temp).equals("")==false)
					{
						tmpSql.append(String.format(" AND %s", request.getParameter(temp)));
					}
				}
				else
				{
					tmpSql.append(String.format(" AND %s = '%s'", temp.replaceFirst("srw_", ""), request.getParameter(temp)));
				}
			}
		}

		if (tmpSql.length() > 0 )
		{
			newsql = String.format("SELECT * FROM (%s) t1 WHERE %s", sqlstr,tmpSql);
		}

		
		if (order != null)
		{
			newsql = String.format("SELECT * FROM (%s) t2 ORDER BY %s", sqlstr,order);
		}
		
		newsql = String.format("%s LIMIT %d,%d", newsql,(pageNum-1)*pagesize,pagesize);
		
		return newsql;
	}

	/**
	 * ��ݴ�����Ϣ����sql ��requestȡ��������srw_�ֶ���,
	 * srw(searchwhere)Ϊ�ƿ�ͷΪ������Ϣ
	 * ����swr_id=1(Ϊid=1) 
	 * srw_l_�ֶ���Ϊlike��������(srw_l_name=abc����Ϊname LIKE '%abc%')
	 * srw_ne_�ֶ���Ϊnot��������(srw_ne_name=abc����Ϊname <> 'abc')
	 * srw_in_�ֶ���Ϊin��������(srw_in_name=abc����Ϊname in (abc))
	 * srw_nin_�ֶ���Ϊnot in��������(srw_nin_name=abc����Ϊname not in (abc))
	 * srw_w�ֶ���Ϊ��������(srw_w=a=b����Ϊa=b)
	 * 
	 * @throws SQLException
	 */
	protected void initSql() throws SQLException
	{
		if (dataset == null && sql != null)
		{
	
			//初始化sql
			String newsql = getInitSqlString(sql);
			
			CPSql c = new CPSql();
			try
			{
				dataset = c.executeQueryCache(newsql, cachetime);
			}
			catch (IOException e1)
			{
				Log.OutException(e1);
			}
			c.close();
		}
	}

	protected boolean hasWhere(String sql)
	{
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("'[\\W\\w]+?'");
		Matcher matcher = pattern.matcher(sql);

		while (matcher.find())
		{
			matcher.appendReplacement(sb, "");
		}
		matcher.appendTail(sb);

		String temp = sb.toString().toLowerCase();
		int i = temp.lastIndexOf("where");
		if (i > 0)
		{
			return temp.lastIndexOf(")", i + 5) < 0;
		}

		return false;
	}

	/**
	 * @see easy.servlet.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		setPageNum();
		initList();
		
		if (request.getParameter("pagesize") != null)
		{
			try
			{
				pagesize = Integer.parseInt(request.getParameter("pagesize"));
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		
		if (request.getParameter("order") != null)
		{
			order = request.getParameter("order");
		}
		initSql();
		
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		response.setDateHeader("Expires", 0); // prevents caching at the proxy server
		response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
		response.setHeader("Content-Type", "application/xml");

		PrintWriter out = response.getWriter();
		out.print(Format.toXMLString(dataset, part(dataset, pagesize, pageNum), System.currentTimeMillis() - starttime));
		out.close();
	}

	protected PageInfo part(DataSet d, int sizePerPage, int pageNum)
	{
		int count = d.getCount();
		PageInfo pi = new PageInfo(count, sizePerPage, pageNum);

		int start = sizePerPage * (pageNum - 1);
		if (start >= d.getCount())
		{
			start = sizePerPage * (pi.getTotalPage() - 1);
		}

		if (start < 0)
		{
			start = 0;
		}

		pi.setStartIndex(start);

		return pi;
	}
}