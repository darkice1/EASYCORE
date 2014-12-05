package easy.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import easy.config.Config;
import easy.filters.compression.CompressionServletResponseWrapper;

/**
 * <p>
 * <i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i>
 * </p>
 * 
 * CompressionFilter class说明
 * 
 * @version 1.0 (<i>2007-1-24 neo</i>)
 */

public class CompressionFilter implements Filter
{
	/**
	 * Minimal reasonable threshold
	 */
	private final static int MIN_THRESHOLD = 128;
	
	/**
	 * Minimal reasonable threshold
	 */
	private final static String DEF_THRESHOLD = "1024";

	/**
	 * The threshold number to compress
	 */
	protected int compressionThreshold;

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */

	public void init(FilterConfig filterConfig)
	{
	}

	/**
	 * Take this filter out of service.
	 */
	public void destroy()
	{
	}

	/**
	 * The <code>doFilter</code> method of the Filter is called by the container each time a request/response pair is passed through the chain due to a client request for a resource at the end of the chain. The FilterChain passed into this method allows the Filter to pass on the request and response to the next entity in the chain.
	 * <p>
	 * This method first examines the request to check whether the client support compression. <br>
	 * It simply just pass the request and response if there is no support for compression.<br>
	 * If the compression support is available, it creates a CompressionServletResponseWrapper object which compresses the content and modifies the header if the content length is big enough. It then invokes the next entity in the chain using the FilterChain object (<code>chain.doFilter()</code>), <br>
	 */

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{	
		if ("TRUE".equals(request.getAttribute("EC_COMPRESSION_ISSET")))
		{
			chain.doFilter(request, response);
			return;	
		}
		else
		{
			request.setAttribute("EC_COMPRESSION_ISSET","TRUE");
			String str = Config.getProperty("COMPRESSIONTHRESHOLD",DEF_THRESHOLD);
			if (str != null)
			{
				compressionThreshold = Integer.parseInt(str);
				if (compressionThreshold != 0 && compressionThreshold < MIN_THRESHOLD)
				{
					//压缩阀值(wrapper)要求大于MIN_THRESHOLD，如果小于MIN_THRESHOLD那么就设置为MIN_THRESHOLD
					compressionThreshold = MIN_THRESHOLD;
				}
			}
			else
			{
				compressionThreshold = 0;
			}
		}
		

		if (compressionThreshold == 0)
		{
			chain.doFilter(request, response);
			return;
		}

		boolean supportCompression = false;
		if (request instanceof HttpServletRequest)
		{
			// Are we allowed to compress ?
			String s = (String) ((HttpServletRequest) request).getParameter("gzip");
			if ("false".equals(s))
			{
				//不支持gzip就不压缩
				chain.doFilter(request, response);
				return;
			}

			Enumeration e = ((HttpServletRequest) request).getHeaders("Accept-Encoding");
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();
				if (name.indexOf("gzip") != -1)
				{
					supportCompression = true;
				}
			}
		}

		if (supportCompression == false)
		{
			chain.doFilter(request, response);
			return;
		}
		else
		{
			if (response instanceof HttpServletResponse)
			{
				CompressionServletResponseWrapper wrappedResponse = new CompressionServletResponseWrapper((HttpServletResponse) response);

				wrappedResponse.setCompressionThreshold(compressionThreshold);

				try
				{
					chain.doFilter(request, wrappedResponse);
				}
				finally
				{
					wrappedResponse.finishResponse();
				}
				return;
			}
		}
	}
}
