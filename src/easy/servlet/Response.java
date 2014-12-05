package easy.servlet;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * TODO Response class˵��
 *
 * @version 1.0 (<i>2005-7-19 Gawen</i>)
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class Response
{
    private HttpServletResponse httpServletResponse;

    public Response( HttpServletResponse hsResponse)
    {
        this.httpServletResponse = hsResponse;
    }

    public void sendRedirect(String location) throws IOException
    {
    	httpServletResponse.sendRedirect(location);
    }
    
    public PrintWriter getWriter() throws IOException
    {
    	return httpServletResponse.getWriter();
    }
    
    public String getCharacterEncoding()
    {
    	return httpServletResponse.getCharacterEncoding();
    }
    
    public String getContentType()
    {
    	return httpServletResponse.getContentType();
    }
    
    public HttpServletResponse getHttpServletResponse()
    {
    	return this.httpServletResponse;
    }
    
    public void setDateHeader(String arg0,long arg1)
    {
    	httpServletResponse.setDateHeader(arg0, arg1);
    }
    
    public void setHeader(String arg0,String arg1)
    {
    	httpServletResponse.setHeader(arg0, arg1);
    }
    
    public void setCharacterEncoding(String encoding)
    {
        httpServletResponse.setCharacterEncoding(encoding);
    }
    
    public void setContentType(String type)
    {
    	httpServletResponse.setContentType(type);
    }
    
	public void setCookie(String name,String value,int expiry,String uri)
	{
		Cookie c = new Cookie(name,value);
		c.setMaxAge(expiry);
		c.setPath(uri);
		httpServletResponse.addCookie(c); 
	}
}