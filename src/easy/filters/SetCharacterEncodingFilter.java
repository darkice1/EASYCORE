package easy.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import easy.config.Config;


/**
 * 
 * <p><i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 * 设置字符过滤器
 *
 * @version 1.0 (<i>2007-1-23 neo</i>)
 */
public class SetCharacterEncodingFilter implements Filter
{
	private boolean isshowurl = false;
	/**
	 * Take this filter out of service.
	 */
	public void destroy()
	{
	}

	/**
	 * Select and set (if specified) the character encoding to be used to interpret request parameters for this request.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param result
	 *            The servlet response we are creating
	 * @param chain
	 *            The filter chain we are processing
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
//		if ("TRUE".equals(request.getAttribute("EC_SETCHARACTERENCODING_ISSET")))
//		{
//			chain.doFilter(request, response);
//			return;	
//		}
//		else
//		{
//			request.setAttribute("EC_SETCHARACTERENCODING_ISSET","TRUE");
//		}
		
		String request_charactencoding = Config.getProperty("REQUEST_CHARACTERENCODING");
		String response_characterencoding = Config.getProperty("RESPONSE_CHARACTERENCODING");
		
		if (request_charactencoding != null)
		{
			request.setCharacterEncoding(request_charactencoding);			
		}
		
		if (response_characterencoding != null)
		{
			response.setCharacterEncoding(response_characterencoding);
		}

		
		if (isshowurl)
		{
			System.out.println(((HttpServletRequest)request).getRequestURL()+"#"+request_charactencoding+"#"+response_characterencoding);
		}
		// Pass control on to the next filter
		chain.doFilter(request, response);

	}

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig  The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException
	{
		String tmp = filterConfig.getInitParameter("isshowurl");
		if (tmp != null && "TRUE".equals(tmp.toUpperCase()))
		{
			isshowurl = true;
		}
	}
}
