package easy.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;

import easy.config.Config;
import easy.util.Log;

/**
 * <p>
 * <i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i>
 * </p>
 * 
 * TODO EFileItem class说明
 * 
 * @version 1.0 (<i>2007-1-22 neo</i>)
 */

public class EFileItem implements FileItem
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private static final String ISO_8859_1 = "ISO-8859-1";
	

	FileItem fileitem;

	public EFileItem(FileItem fileitem)
	{
		this.fileitem = fileitem;
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#delete()
	 */
	public void delete()
	{
		fileitem.delete();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#get()
	 */
	public byte[] get()
	{
		return fileitem.get();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getContentType()
	 */
	public String getContentType()
	{
		return fileitem.getContentType();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getFieldName()
	 */
	public String getFieldName()
	{
		return fileitem.getFieldName();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return fileitem.getInputStream();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getName()
	 */
	public String getName()
	{
		return fileitem.getName();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException
	{
		return fileitem.getOutputStream();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getSize()
	 */
	public long getSize()
	{
		return fileitem.getSize();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getString()
	 */
	public String getString()
	{
		String value = fileitem.getString();
		String request_charactencoding = Config.getProperty("REQUEST_CHARACTERENCODING");
		
		if (request_charactencoding != null)
		{
			try
			{
				value = new String(value.getBytes(ISO_8859_1),request_charactencoding);
			}
			catch (UnsupportedEncodingException e)
			{
				Log.OutException(e);
			}
		}
		
		return value;
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#getString(java.lang.String)
	 */
	public String getString(String arg0) throws UnsupportedEncodingException
	{
		return fileitem.getString(arg0);
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#isFormField()
	 */
	public boolean isFormField()
	{
		return fileitem.isFormField();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#isInMemory()
	 */
	public boolean isInMemory()
	{
		return fileitem.isInMemory();
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#setFieldName(java.lang.String)
	 */
	public void setFieldName(String arg0)
	{
		fileitem.setFieldName(arg0);
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#setFormField(boolean)
	 */
	public void setFormField(boolean arg0)
	{
		fileitem.setFormField(arg0);
	}

	/**
	 * @see org.apache.commons.fileupload.FileItem#write(java.io.File)
	 */
	public void write(File arg0) throws Exception
	{
		fileitem.write(arg0);
	}

	/**
	 * 取得文件名
	 * @return
	 */
	public String getFileName()
	{
		String fileName = getName();

		if (fileName != null)
		{
			fileName = FilenameUtils.getName(fileName);
		}

		return fileName;
	}
}
