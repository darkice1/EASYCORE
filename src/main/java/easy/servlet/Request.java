package easy.servlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

public class Request
{
	private final HttpServletRequest httpServletRequest;
	
//	private static final String SYS_TEMP_DIR = System.getProperty("java.io.tmpdir");
	
//	private final Map<String,EFileItem> dataMap	= new HashMap<>();
	
//	private boolean isMultipartContent = false;
	private static final String UPLOAD = "multipart/form-data"; 
	
	public Request(HttpServletRequest hsRequest)
	{		
		this.httpServletRequest = hsRequest;
//		isMultipartContent = isMultipartContent();
	}
	
	public void setAttribute(String name,Object value)
	{
		httpServletRequest.setAttribute(name,value);
	}
	public String getParameter(String name)
	{
		return getParameterForGeneral(name);
	}
	
	private String getParameterForGeneral(String name)
	{
		return httpServletRequest.getParameter(name);
	}

	
	public HttpSession getSession()
	{
		return httpServletRequest.getSession();
	}
	
	public HttpSession getSession(boolean b)
	{
		return httpServletRequest.getSession(b);
	}
	
	public boolean isMultipartContent()
	{
		String contentType = httpServletRequest.getContentType();
		return contentType != null && contentType.startsWith(UPLOAD);
	}


	
	public HttpServletRequest getHttpServletRequest()
	{
		return httpServletRequest;
	}

	public Enumeration getAttributeNames()
	{
		return httpServletRequest.getAttributeNames();	
	}

	public Enumeration getParameterNames()
	{
		return httpServletRequest.getParameterNames();	
	}
	
	public Cookie[] getCookies()
	{
		return httpServletRequest.getCookies();
	}
	
	public String getCookieValue(String name)
	{
		Cookie[] c = getCookies();
		
		if (c != null)
		{
			for (Cookie t:c)
			{
				if (name.equals(t.getName()))
				{
					return t.getValue();
				}
			}
		}
		
		return null;
	}	

	public String[] getParameterValues(String param)
	{
		return httpServletRequest.getParameterValues(param);
	}
	
	public void setCharacterEncoding(String encode) throws UnsupportedEncodingException
	{
		httpServletRequest.setCharacterEncoding(encode);
	}
	
	public String getRemoteHost()
	{
		return httpServletRequest.getRemoteHost();
	}
	
	public String getContextPath()
	{
		return httpServletRequest.getContextPath();
	}
	
	public String getHeader(String head)
	{
		return httpServletRequest.getHeader(head);
	}
}
