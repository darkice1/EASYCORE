package easy.filters.compression;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 * CompressionServletResponseWrapper class说明
 *
 * @version 1.0 (<i>2007-1-24 neo</i>)
 */

public class CompressionServletResponseWrapper extends HttpServletResponseWrapper
{
	/**
	 * Calls the parent constructor which creates a ServletResponse adaptor
	 * wrapping the given response object.
	 */

	public CompressionServletResponseWrapper(HttpServletResponse response)
	{
		super(response);
		origResponse = response;
	}

	/**
	 * Original response
	 */

	protected HttpServletResponse origResponse = null;

	/**
	 * The ServletOutputStream that has been returned by
	 * <code>getOutputStream()</code>, if any.
	 */

	protected ServletOutputStream stream = null;

	/**
	 * The PrintWriter that has been returned by
	 * <code>getWriter()</code>, if any.
	 */

	protected PrintWriter writer = null;

	/**
	 * The threshold number to compress
	 */
	protected int threshold = 0;

	/**
	 * Content type
	 */
	protected String contentType = null;

	/**
	 * Set content type
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
		origResponse.setContentType(contentType);
	}

	/**
	 * Set threshold number
	 */
	public void setCompressionThreshold(int threshold)
	{
		this.threshold = threshold;
	}

	/**
	 * Create and return a ServletOutputStream to write the content
	 * associated with this Response.
	 *
	 * @exception IOException if an input/output error occurs
	 */
	public ServletOutputStream createOutputStream() throws IOException
	{
		CompressionResponseStream stream = new CompressionResponseStream(origResponse);
		stream.setBuffer(threshold);

		return stream;

	}

	/**
	 * Finish a response.
	 */
	public void finishResponse()
	{
		try
		{
			if (writer != null)
			{
				writer.close();
			}
			else
			{
				if (stream != null)
					stream.close();
			}
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Flush the buffer and commit this response.
	 *
	 * @exception IOException if an input/output error occurs
	 */
	public void flushBuffer() throws IOException
	{
		((CompressionResponseStream) stream).flush();

	}

	/**
	 * Return the servlet output stream associated with this Response.
	 *
	 * @exception IllegalStateException if <code>getWriter</code> has
	 *  already been called for this response
	 * @exception IOException if an input/output error occurs
	 */
	public ServletOutputStream getOutputStream() throws IOException
	{

		if (writer != null)
			throw new IllegalStateException("getWriter() has already been called for this response");

		if (stream == null)
			stream = createOutputStream();

		return (stream);

	}

	/**
	 * Return the writer associated with this Response.
	 *
	 * @exception IllegalStateException if <code>getOutputStream</code> has
	 *  already been called for this response
	 * @exception IOException if an input/output error occurs
	 */
	public PrintWriter getWriter() throws IOException
	{

		if (writer != null)
			return (writer);

		if (stream != null)
			throw new IllegalStateException("getOutputStream() has already been called for this response");

		stream = createOutputStream();

		//String charset = getCharsetFromContentType(contentType);
		String charEnc = origResponse.getCharacterEncoding();

		// HttpServletResponse.getCharacterEncoding() shouldn't return null
		// according the spec, so feel free to remove that "if"
		if (charEnc != null)
		{
			writer = new PrintWriter(new OutputStreamWriter(stream, charEnc));
		}
		else
		{
			writer = new PrintWriter(stream);
		}

		return (writer);

	}

	/*
	public void setContentLength(int length)
	{
	}
	

	
	/**
	 * Returns character from content type. This method was taken from tomcat.
	 * @author rajo
	 */
	/*
	private static String getCharsetFromContentType(String type)
	{

		if (type == null)
		{
			return null;
		}
		int semi = type.indexOf(";");
		if (semi == -1)
		{
			return null;
		}
		String afterSemi = type.substring(semi + 1);
		int charsetLocation = afterSemi.indexOf("charset=");
		if (charsetLocation == -1)
		{
			return null;
		}
		else
		{
			String afterCharset = afterSemi.substring(charsetLocation + 8);
			String encoding = afterCharset.trim();
			return encoding;
		}
	}
	*/
}
