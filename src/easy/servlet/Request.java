package easy.servlet;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * @version 1.0 (<i>2005-7-18 Gawen</i>)
 */

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import easy.config.Config;
import easy.io.EFileItem;
import easy.util.Log;

public class Request
{
	private HttpServletRequest httpServletRequest = null;
	
	private static final String SYS_TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	private Map<String,EFileItem> dataMap	= new HashMap<String,EFileItem>();
	
	private boolean isMultipartContent = false;
	private static final String UPLOAD = "multipart/form-data"; 
	
	public Request(HttpServletRequest hsRequest) throws IOException
	{		
		this.httpServletRequest = hsRequest;
		isMultipartContent = isMultipartContent();

		if (isMultipartContent)
		{
//			Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			
			//设置系统使用内存
			factory.setSizeThreshold((int)(1024*Float.parseFloat(Config.getProperty("UPLOAD_MAX_MEMORY","512"))));
			//设置使用使用临时文件夹
			factory.setRepository(new File(Config.getProperty("UPLOAD_TEMP_DIR",SYS_TEMP_DIR)));
			

			String request_charactencoding = Config.getProperty("REQUEST_CHARACTERENCODING");
//			Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			upload.setHeaderEncoding(request_charactencoding);
			
			upload.setSizeMax((long)(1024*1024*Float.parseFloat(Config.getProperty("REQUEST_MAXSIZE","100"))));
//			Parse the request
			List<FileItem> items;
			try
			{
				items = upload.parseRequest(httpServletRequest);
				
				for (int i=0; i < items.size(); i++)
				{
					EFileItem t= new EFileItem((FileItem) items.get(i));
					dataMap.put(t.getFieldName(),t);
				}
			}
			catch (FileUploadException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	public void setAttribute(String name,Object value)
	{
		httpServletRequest.setAttribute(name,value);
	}
	public String getParameter(String name)
	{
		return isMultipartContent?getParameterForUpload(name):getParameterForGeneral(name);
	}
	
	private String getParameterForGeneral(String name)
	{
		return httpServletRequest.getParameter(name);
	}
	
	private String getParameterForUpload(String name)
	{
		EFileItem t = dataMap.get(name);
		if (t!= null && t.isFormField())
		{
			return t.getString();
		}
		return null;
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
	
	public String saveAs(String name,String path,boolean isover) throws NoSuchElementException,IllegalAccessException
    {
		EFileItem t = dataMap.get(name);
		if (t == null)
		{
			throw new NoSuchElementException("no such element[" + name + "]");
		}
		else
		{
			int limited_size = Integer.parseInt(Config.getProperty("UPLOAD_FILE_MAX_SIZE"))*1024*1024;
			
			if (t.isFormField())
			{
				throw new IllegalAccessException("element[" + name + "]'s value is not a file!");
			}
			else
			{
				if (t.getSize() > limited_size)
				{
					return null;
				}
				else
				{
					File f = new File (path);
					try
					{
						if (isover == false)
						{
							while (f.exists())
							{
								//System.out.println( f.getName());
								String filename = "{"+new Random(System.currentTimeMillis()).nextLong()+"}" + f.getName();
								path = f.getParent()+"/"+filename;
								//System.out.println(path);
								f = new File (path);
							}
						}
						t.write(f);
					}
					catch (Exception e)
					{
						Log.OutException(e);
						return null;
					}
					return f.getName();
				}
			}
		}
    }
	
	public String saveAs(String name,String path) throws NoSuchElementException,IllegalAccessException
    {
		return saveAs(name,path,false);
    }
	
	/*
	public String getSavedFileName()
	{
		if (REQUEST_CHARACTERENCODING == null)
		{
			return savedFileName;
		}
		else
		{
			try
			{
				return new String(savedFileName.getBytes(),REQUEST_CHARACTERENCODING);
			}
			catch (UnsupportedEncodingException e)
			{
				Log.OutException(e);
				return null;
			}
		}
	}
	*/
	/**
	 *  取得表单文件名称，如果没有或者不是上传是null
	 * @param name
	 * @return
	 */
	public String getFileName(String name)
	{
		EFileItem t = dataMap.get(name);
		if (t == null || t.isFormField())
		{
			return null;
		}
		else
		{
			return t.getFileName();
		}
	}
	
	/**
	 *  取得表单文件类型，如果没有或者不是上传是null
	 * @param name
	 * @return
	 */
	public String getContentType(String name)
	{
		EFileItem t = dataMap.get(name);
		if (t == null || t.isFormField())
		{
			return null;
		}
		else
		{
			return t.getContentType();
		}
	}
	
	/**
	 * 取得表单文件尺寸，如果没有或者不是上传是-1
	 * @param name
	 * @return
	 */
	public long getSize(String name)
	{
		EFileItem t = dataMap.get(name);
		if (t == null || t.isFormField())
		{
			return -1;
		}
		else
		{
			return t.getSize();
		}
	}	
	
	public HttpServletRequest getHttpServletRequest()
	{
		return httpServletRequest;
	}

	
	/**
	 *��httpServletRequest��Ч
	 * @return
	 */
	public Enumeration getAttributeNames()
	{
		return httpServletRequest.getAttributeNames();	
	}
	
	/**
	 *��httpServletRequest��Ч
	 * @return
	 */
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
	
	/**
	 * �httpServletRequest��Ч
	 * @return
	 */
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
