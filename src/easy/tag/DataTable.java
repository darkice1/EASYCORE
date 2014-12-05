package easy.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import easy.sql.BaseTable;
import easy.sql.DataSet;
import easy.util.Log;

/**
 * <p>
 * <i>Copyright: Easy (c) 2005-2005 <br>
 * Company: Easy </i>
 * </p>
 * 
 * DataTable tag
 * 
 * @version 1.0 ( <i>2005-7-6 neo </i>)
 */

public class DataTable extends SimpleTagSupport
{
	private final static Pattern FIELDPATTERN = Pattern.compile("\\{\\{[a-zA-Z0-9_]*\\}\\}");

	private Integer pagesize = new Integer(-1);

	/**
	 * 当前页，页号从1开始
	 */
	private Integer page = new Integer(1);

	private JspFragment row;
	private JspFragment row1;
	private JspFragment row2;

	private BaseTable table;

	private DataSet dataset;

	protected String pagerstyle = "DEF";

	protected String action = "";

	protected String where;
	
	protected String order = "id DESC";

	/**
	 * @return Returns the pagerstyle.
	 */
	public String getPagerstyle()
	{
		return pagerstyle;
	}

	/**
	 * @param pagerstyle
	 *            The pagerstyle to set.
	 */
	public void setPagerstyle(String pagerstyle)
	{
		this.pagerstyle = pagerstyle;
	}

	/**
	 * @return Returns the table.
	 */
	public BaseTable getTable()
	{
		return table;
	}

	/**
	 * @param table
	 *            The table to set.
	 */
	public void setTable(BaseTable table)
	{
		this.table = table;
	}

	/**
	 * @return Returns the row.
	 */
	public JspFragment getRow()
	{
		return row;
	}

	/**
	 * @param row
	 *            The row to set.
	 */
	public void setRow(JspFragment row)
	{
		this.row = row;
	}
	
	public void setRow1(JspFragment row1)
	{
		this.row1 = row1;
	}
	
	public void setRow2(JspFragment row2)
	{
		this.row2 = row2;
	}
	

	public void doTag() throws JspException,IOException
	{
		JspContext ctx = getJspContext();
		JspWriter out = ctx.getOut();

		StringWriter writer = new StringWriter();

		String str = "",str1 = "",str2 = "";
		
		if (row == null)
		{
		    if (row1 == null || row2 == null)
		    {
		    	throw new JspException("Datatable 标签需要一个名为：row/row1/row2的<jsp:attribute>子标签！");
		    }
		    
		    row1.invoke(writer);
		    str1 = writer.toString();
		    
		    writer = new StringWriter();
		    
		    row2.invoke(writer);
		    str2 = writer.toString();
		    
		}
		else
		{
		    row.invoke(writer);
		    str = writer.toString();
		}
		
		StringBuffer buf = new StringBuffer();

		int size = pagesize.intValue();
		int pnum = page.intValue();
		
		//:~para:BaseTable
		if (dataset == null)
		{
			if (table != null)
			{
			    table.setOrder(order);
			    try
			    {
			    	dataset = table.select(where, (pnum - 1) * size, size);
			    }
				catch (SQLException ex)
				{
					Log.OutException(ex);
				}
				
				if (dataset != null)
			    {
				    size = size < 0?dataset.getCount():size;
					if (row == null)
					{
					    for (int i = 0; i < size && dataset.next(); i++)
						{
						    if (i % 2 == 0)
						    {
						    	buf.append(outrow(str1));
						    }
						    else
						    {
						        buf.append(outrow(str2));
						    }					
						}
				    }//:~row == null
					else
					{
					    for (int i = 0; i < size && dataset.next(); i++)
						{
							buf.append(outrow(str));
						}    
					}
				}
			}
			else
			{
				//FIELDPATTERN
				out.println(writer.toString().replaceAll(FIELDPATTERN.pattern(), ""));
				return;
			}
			//:~dataset != null
		}//:~dataset == null
		else
		{
		    size = size < 0?dataset.getCount():size;
		    
			for (int i = (pnum - 1) * size; i < pnum * size && i < dataset.getCount(); i++)
			{
		        dataset.moveCursor(i);
		        if (row == null)
				{       
		            if (i % 2 == 0)
				    {
				    	buf.append(outrow(str1));
				    }
				    else
				    {
				        buf.append(outrow(str2));
				    }
				}
		        else
		        {
		        	buf.append(outrow(str));
		        }
			}
		}

		out.println(buf);
		ctx.setAttribute("pagestyle", outpagerstyle());
	}

	private String outpagerstyle()
	{
		if ("othersStyle".equals(pagerstyle))
		{
			return "";
		}
		else
		{
			return outDefaultStyle();
		}
	}

	private String outDefaultStyle()
	{
		StringBuffer styleBuffer = new StringBuffer();
		styleBuffer.append("<table width='100%' cellpadding='0' cellspacing='0'><tr class='PageToolbar'><td align='center' nowrap>");

		styleBuffer.append(String.format("<font style='font-size:10pt;'>共%s条记录",dataset.getCount()));
		
		if (pagesize <= 0)
		{
		    pagesize = Integer.MAX_VALUE;
		    styleBuffer.append(" 分1页显示 ");
		}
		else
		{
		    int pages = ((dataset.getCount() + pagesize.intValue() - 1) / pagesize.intValue());
			styleBuffer.append(String.format(" 分%s页显示 ",pages < 1?1:pages));
		}
		
		styleBuffer.append("当前第" + page + "页 ");
		if (page.intValue() <= 1)
		{
			styleBuffer.append(" <input type='button' class='button' value='首页' disabled>");
			styleBuffer.append(" <input type='button' class='button' value='上一页' disabled>");
		}
		else
		{
			styleBuffer.append(String.format(" <input type='button' class='button' value='首页' onclick=location.href('command?action=%s&page=%s')>",action,1));
			styleBuffer.append(String.format(" <input type='button' class='button' value='上一页' onclick=location.href('command?action=%s&page=%s')>",action,page.intValue() - 1));
		}

		if (page.intValue() >= ((dataset.getCount() + pagesize.intValue() - 1) / pagesize.intValue()))
		{
			styleBuffer.append(" <input type='button' class='button' value='下一页' disabled>");
			styleBuffer.append(" <input type='button' class='button' value='末页' disabled>");
		}
		else
		{
			styleBuffer.append(String.format(" <input type='button' class='button' value='下一页' onclick=location.href('command?action=%s&page=%s')> ",action,page.intValue() + 1));
			styleBuffer.append(String.format(" <input type='button' class='button' value='末页' onclick=location.href('command?action=%s&page=%s')> ",action,(dataset.getCount() + pagesize.intValue() - 1) / pagesize.intValue()));
		}
		styleBuffer.append(" 跳转至<input type='text' name='pageToTurn' id='pageToTurn' size=4 value='");
		styleBuffer.append(page);
		styleBuffer.append("'><input type=button value='Go' onclick='turnPage(");
		styleBuffer.append((dataset.getCount() + pagesize.intValue() - 1) / pagesize.intValue());
		styleBuffer.append(")'>");

		styleBuffer.append("<script>function turnPage(totalPage){");
		styleBuffer.append("var o = document.getElementById('pageToTurn');");
		styleBuffer.append("if (!checkObjNumber(o,'页码不能为字符和负数')) return;");
		styleBuffer.append("if (o.value < 1 || o.value > totalPage) return;");
		styleBuffer.append("location.href('command?action=");
		styleBuffer.append(action);
		styleBuffer.append("&page='+o.value);");
		styleBuffer.append("}</script>");

		styleBuffer.append("</td></tr></table>");
		return styleBuffer.toString();
	}

	private StringBuffer outrow(String str)
	{
		StringBuffer buf = new StringBuffer();

		Matcher m = FIELDPATTERN.matcher(str);

		while (m.find())
		{
			try
			{
				String field = m.group();
				String tmp = dataset.getString(field.substring(2, field.length() - 2));
				tmp = tmp.replaceAll("\\\\","\\\\\\\\");
				tmp = tmp.replaceAll("\\$","\\\\\\$");
				m.appendReplacement(buf, tmp);
			}
			catch (Exception e)
			{
				Log.OutException(e);
			}
		}
		m.appendTail(buf);
		return buf;
	}

	/**
	 * @return Returns the pagesize.
	 */
	public Integer getPagesize()
	{
		return pagesize;
	}

	/**
	 * @param pagesize
	 *            The pagesize to set.
	 */
	public void setPagesize(Integer pagesize)
	{
		this.pagesize = pagesize;
	}

	/**
	 * @return Returns the page.
	 */
	public Integer getPage()
	{
		return page;
	}

	/**
	 * @param page
	 *            The page to set.
	 */
	public void setPage(Integer page)
	{
		this.page = page;
	}

	/**
	 * @param page
	 *            The page to set.
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * @return Returns the where.
	 */
	public String getWhere()
	{
		return where;
	}

	/**
	 * @param where
	 *            The where to set.
	 */
	public void setWhere(String where)
	{
		this.where = where;
	}

	/**
	 * @return Returns the dataset.
	 */
	public DataSet getDataset()
	{
		return dataset;
	}

	/**
	 * @param dataset
	 *            The dataset to set.
	 */
	public void setDataset(DataSet dataset)
	{
		this.dataset = dataset;
	}
	
	public void setOrder(String order)
	{
	    this.order = order;
	}
	
	public String getOrder()
	{
	    return this.order;
	}
}