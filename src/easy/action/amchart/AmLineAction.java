/**
 * 
 */
package easy.action.amchart;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import easy.action.Action;
import easy.sql.DataSet;
import easy.sql.Row;
import easy.util.Format;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2011</i></p>
 *
 * AmLineAction class说明
 *
 * @version 1.0 (<i>2011-9-8 neo(starneo@gmail.com)</i>)
 */

public class AmLineAction extends Action
{
	protected DataSet dataset;
	protected String seriesname;
	protected List <String[]> list = new ArrayList<String[]>();

	/**
	 * @param dataset the dataset to set
	 */
	public void setDataset(DataSet dataset)
	{
		this.dataset = dataset;
	}

	public void addValuename(String valuename)
	{
		addValuename(valuename,valuename);
	}

	public void addValuename(String valuename,String valuetitle)
	{
		String[] t = {valuename,valuetitle};
		list.add(t);
	}

	/* (non-Javadoc)
	 * @see easy.action.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		response.setDateHeader("Expires", 0); // prevents caching at the proxy server
		response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
		response.setHeader("Content-Type", "application/xml");

		PrintWriter out = response.getWriter();
		
		out.print(getXMLString());

		//out.print(Format.toXMLString(dataset, part(dataset, pagesize, pageNum), System.currentTimeMillis() - starttime));
		out.close();
	}
	
	public String getXMLString()
	{
		StringBuffer buf = new StringBuffer();
		
		int listlen = list.size();
		if (dataset!=null && seriesname!=null && listlen > 0)
		{
			buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><chart>");
			
			StringBuffer sbuf = new StringBuffer("<series>");
			StringBuffer[] vbuf = new StringBuffer[listlen];
			
			for (int j=0,jlen=list.size();j<jlen;j++)
			{
				//System.out.println(j+" "+list.get(j)[0]+" "+list.get(j)[1]);
				vbuf[j] = new StringBuffer();
				vbuf[j].append(String.format("<graph gid=\"%d\" title=\"%s\">", j,Format.toHTMLString(list.get(j)[1])));
			}
			
			for (int i=0,len=dataset.getCount();i<len;i++)
			{
				Row r = dataset.getRow(i);
				sbuf.append(String.format("<value xid=\"%d\">%s</value>", i,Format.toHTMLString(r.getString(seriesname))));
				
				for (int j=0,jlen=list.size();j<jlen;j++)
				{
					vbuf[j].append(String.format("<value xid=\"%d\">%s</value>", i,r.getString(list.get(j)[0])));
				}
			}
			
			buf.append(sbuf);
			buf.append("</series><graphs>");
			
			
			for (int j=0,jlen=list.size();j<jlen;j++)
			{
				vbuf[j].append("</graph>");
				buf.append(vbuf[j]);
			}			
			
						
			buf.append("</graphs></chart>");
		}
		return buf.toString();
	}

	/**
	 * @param seriesname the seriesname to set
	 */
	public void setSeriesname(String seriesname)
	{
		this.seriesname = seriesname;
	}
	
	public static void main(String[] args)
	{
		AmLineAction a = new AmLineAction();
		DataSet d = new DataSet();
		
		for (int i=0;i<10;i++)
		{
			Row r = new Row();
			r.putString("s", i+"月>");
			r.putInteger("a", (int)(Math.random()*6));
			r.putInteger("b", (int)(Math.random()*12));
			r.putInteger("c", (int)(Math.random()*8));
			d.AddRow(r);
		}
		
		a.setDataset(d);
		a.setSeriesname("s");
		a.addValuename("a","测试>");
		a.addValuename("b");
		a.addValuename("c");
		
		System.out.println(a.getXMLString());
		
	}
}
