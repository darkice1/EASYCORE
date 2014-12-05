package easy.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import easy.servlet.Request;
import easy.servlet.Response;

/**
 * <p>
 * <i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i>
 * </p>
 * 
 * 通用action
 * 
 * @version 1.0 (<i>2005-7-8 neo</i>)
 */

public abstract class Action
{
	/**
	 * 跳转地址
	 */
	protected String url = null;

	/**
	 * 翻页页号
	 */
	protected int page = 1;

	/**
	 * request
	 */
	protected Request request;

	/**
	 * response
	 */
	protected Response response;

	/**
	 * session
	 */
	protected HttpSession session;

	/**
	 * 提示信息
	 */
	protected String message = "";

	/**
	 * 错误信息
	 */
	protected String error = "";

	/**
	 * 提供给标签的action信息
	 */
	protected String action = "";

	protected boolean isForward = true;

	protected boolean issubmit = false;

	protected int timeout = 2000;

	protected boolean iserror = false;

	protected static String SUBMITURL = "/common/prompt.jsp";

	protected static String ERRORURL = "/common/error.jsp";
	
	protected long starttime = System.currentTimeMillis();
	
	//private static final String RESPONSE_CHARACTERENCODING = Config.getProperty("RESPONSE_CHARACTERENCODING");
	//private static final String REQUEST_CHARACTERENCODING  = Config.getProperty("REQUEST_CHARACTERENCODING");

	public void init(Request request, Response response)throws Exception
	{
		this.request = request;
		this.response = response;
/*
		if (RESPONSE_CHARACTERENCODING != null)
		{
        response.setCharacterEncoding(RESPONSE_CHARACTERENCODING);
		}
		
		if (REQUEST_CHARACTERENCODING != null)
		{
			request.getHttpServletRequest().setCharacterEncoding(REQUEST_CHARACTERENCODING);
		}
*/
		String p_page = request.getParameter("page");
		page = (p_page == null) ? 1 : Integer.parseInt(p_page);
		session = request.getSession(true);
		action = request.getParameter("action");

		try
		{
			session.removeAttribute("where");
			session.removeAttribute("error");
			session.removeAttribute("message");
			session.removeAttribute("url");
		}
		catch(Exception e)
		{
		}
	}

	public abstract void Perform() throws Exception;
	
	public  void afterPerform() throws Exception
	{
	}
	
	public void send() throws IOException, ServletException
	{		
		long usertime = System.currentTimeMillis() - starttime;

		if (url != null)
		{
			if (url.indexOf("http") != 0)
			{
				url = request.getContextPath()+url;
			}
			
			if (isForward)
			{
				request.setAttribute("error", error);
				request.setAttribute("message", message);
				request.setAttribute("url", url);
				request.setAttribute("timeout", timeout);
				request.setAttribute("USE_TIME", usertime);
				forward();
			}
			else
			{
				session.setAttribute("error", error);
				session.setAttribute("message", message);
				session.setAttribute("url", url);
				session.setAttribute("timeout", timeout);
				session.setAttribute("USE_TIME", usertime);
				redirect();
			}
		}
	}

	public void redirect() throws IOException
	{
		if (iserror)
		{
			response.sendRedirect(ERRORURL);
		}
		else
		{
			if (issubmit)
			{
				response.sendRedirect(SUBMITURL);
			}
			else
			{
				response.sendRedirect(url);
			}
		}
	}

	public void forward() throws IOException, ServletException
	{
		if (iserror)
		{
			request.getHttpServletRequest().getRequestDispatcher(ERRORURL).forward(request.getHttpServletRequest(), response.getHttpServletResponse());
		}
		else
		{
			if (issubmit)
			{
				request.getHttpServletRequest().getRequestDispatcher(SUBMITURL).forward(request.getHttpServletRequest(), response.getHttpServletResponse());
			}
			else
			{
				request.getHttpServletRequest().getRequestDispatcher(url).forward(request.getHttpServletRequest(), response.getHttpServletResponse());
			}
		}
	}

	public void setIssubmit(boolean issubmit)
	{
		setIssubmit(issubmit, timeout);
	}

	public void setIssubmit(boolean issubmit, int timeout)
	{
		this.issubmit = issubmit;
		this.timeout = timeout;
	}
}