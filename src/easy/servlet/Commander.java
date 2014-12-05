package easy.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import easy.action.Action;
import easy.config.Config;
import easy.util.Log;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * 控制中心
 * 传入连接 /command?action=abc&url=xxxxxx
 * 1、具体实例化Action
 * 2、Action.init(HttpServletRequest request, HttpServletResponse response)
 * 3、Action.Perform()
 * 4、Action.forward ()
 * 
 * @version 1.0 (<i>2005-7-8 neo</i>)
 */

public class Commander extends HttpServlet
{	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	public java.lang.String getServletInfo()
	{
		return super.getServletInfo();
	}

	public void destroy()
	{
		super.destroy();
	}

	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
		//request.setCharacterEncoding(Config.getProperty("REQUEST_CHARACTERENCODING","GBK"));
		String packageName	= Config.getProperty("ACTION_PACKAGE");
		String actionName 	= request.getParameter("action");
		
		try
		{
			Action actionInstance;
			try
			{
				actionInstance= (Action)Class.forName("easy.action." + actionName + "Action").newInstance();
			}
			catch (ClassNotFoundException cnfe)
			{
				actionInstance = (Action)Class.forName(packageName + actionName + "Action").newInstance();		
			}			
			actionInstance.init(new Request(request),new Response(response));
			actionInstance.Perform();
			actionInstance.afterPerform();
			actionInstance.send();
		}
		catch (ClassNotFoundException cnfe)
		{
			request.setAttribute("error",Log.OutException(cnfe).replaceAll("\r","<br>"));
			request.getRequestDispatcher("/common/error.jsp").forward(request,response);			
		}
		catch (Exception e)
		{
			request.setAttribute("error",Log.OutException(e).replaceAll("\r","<br>"));
			request.getRequestDispatcher("/common/error.jsp").forward(request,response);
		}
	}

	protected void doDelete(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException
	{
	}

	protected void doHead(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
	}

	protected void doOptions(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
	}

	protected void doTrace(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
	{
	}
}
