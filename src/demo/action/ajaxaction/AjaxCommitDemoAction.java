package demo.action.ajaxaction;

import java.io.PrintWriter;

import easy.action.Action;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 * TODO AjaxCommitDemoAction class说明
 *
 * @version 1.0 (<i>2007-3-12 neo</i>)
 */

public class AjaxCommitDemoAction extends Action
{
	/**
	 * @see easy.servlet.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		System.out.println(request.getParameter("test"));
		System.out.println(request.getParameter("test2"));
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		boolean flag = true;
		if (flag)
		{
			buf.append(String.format("<rs message=\"%s\" />","提交成功"));
		}
		else
		{
			buf.append(String.format("<rs message=\"%s\" />","提交失败"));
		}

		
		
		
		
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		response.setDateHeader("Expires", 0); // prevents caching at the proxy server
		response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
		response.setHeader("Content-Type", "application/xml");	
		
		PrintWriter out = response.getWriter();
		out.print(buf);
		out.close();
	}

}
