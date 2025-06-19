package easy.filters;

import easy.config.Config;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;


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


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
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
		if ("TRUE".equalsIgnoreCase(tmp))
		{
			isshowurl = true;
		}
	}
}
