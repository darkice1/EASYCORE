/**
 * 
 */
package easy.action.tools;

import easy.action.ListXmlAction;
import easy.sql.DataSet;
import easy.sql.Row;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2011</i></p>
 *
 * 进度条
 *
 * @version 1.0 (<i>2011-1-21 neo(starneo@gmail.com)</i>)
 */

public class ProgressBarAction extends ListXmlAction
{
	private String text;
	private int curpg,maxpg;
	
	
	/* (non-Javadoc)
	 * @see easy.action.ListXmlAction#initListXml()
	 */
	@Override
	protected void initList()
	{
		String n = request.getParameter("n");
		
		if (n!=null)
		{
			String a = request.getParameter("a");
			dataset = new DataSet();
			if ("c".equals(a))
			{
				session.removeAttribute(n);
			}
			else
			{
				ProgressBarAction pb = (ProgressBarAction)session.getAttribute(n);
				if (pb==null)
				{
					Row r = new Row();
					r.putString("text", "loading");
					r.putInteger("curpg", 0);
					r.putInteger("maxpg", 100);
					
					dataset.AddRow(r);
				}
				else
				{
					//System.out.println(pb.text);
					Row r = new Row();
					r.putString("text", pb.text);
					r.putInteger("curpg", pb.curpg);
					r.putInteger("maxpg", pb.maxpg);
					
					dataset.AddRow(r);
				}
			}
		}
	}


	/**
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}


	/**
	 * @return the curpg
	 */
	public int getCurpg()
	{
		return curpg;
	}


	/**
	 * @return the maxpg
	 */
	public int getMaxpg()
	{
		return maxpg;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}


	/**
	 * @param curpg the curpg to set
	 */
	public void setCurpg(int curpg)
	{
		this.curpg = curpg;
	}


	/**
	 * @param maxpg the maxpg to set
	 */
	public void setMaxpg(int maxpg)
	{
		this.maxpg = maxpg;
	}
}
