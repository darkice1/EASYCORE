package easy.filters.compression;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * <i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i>
 * </p>
 * 
 * CompressionResponseStream class说明
 * 
 * @version 1.0 (<i>2007-1-24 neo</i>)
 */

public class CompressionResponseStream extends ServletOutputStream
{
	/**
	 * Construct a servlet output stream associated with the specified Response.
	 * 
	 * @param response
	 *            The associated response
	 */
	public CompressionResponseStream(HttpServletResponse response) throws IOException
	{

		super();
		closed = false;
		this.response = response;
		this.output = response.getOutputStream();

	}

	/**
	 * The threshold number which decides to compress or not. Users can configure in web.xml to set it to fit their needs.
	 */
	protected int compressionThreshold = 0;

	/**
	 * The buffer through which all of our output bytes are passed.
	 */
	protected byte[] buffer = null;

	/**
	 * The number of data bytes currently in the buffer.
	 */
	protected int bufferCount = 0;

	/**
	 * The underlying gzip output stream to which we should write data.
	 */
	protected GZIPOutputStream gzipstream = null;

	/**
	 * Has this stream been closed?
	 */
	protected boolean closed = false;

	/**
	 * The content length past which we will not write, or -1 if there is no defined content length.
	 */
	protected int length = -1;

	/**
	 * The response with which this servlet output stream is associated.
	 */
	protected HttpServletResponse response = null;

	/**
	 * The underlying servket output stream to which we should write data.
	 */
	protected ServletOutputStream output = null;

	/**
	 * Set the compressionThreshold number and create buffer for this size
	 */
	protected void setBuffer(int threshold)
	{
		compressionThreshold = threshold;
		buffer = new byte[compressionThreshold];
	}

	/**
	 * Close this output stream, causing any buffered data to be flushed and any further output data to throw an IOException.
	 */
	public void close() throws IOException
	{
		if (closed)
			throw new IOException("This output stream has already been closed");

		if (gzipstream != null)
		{
			flushToGZip();
			gzipstream.close();
			gzipstream = null;
		}
		else
		{
			if (bufferCount > 0)
			{
				output.write(buffer, 0, bufferCount);
				bufferCount = 0;
			}
		}

		output.close();
		closed = true;

	}

	/**
	 * Flush any buffered data for this output stream, which also causes the response to be committed.
	 */
	public void flush() throws IOException
	{
		if (closed)
		{
			throw new IOException("Cannot flush a closed output stream");
		}

		if (gzipstream != null)
		{
			gzipstream.flush();
		}

	}

	public void flushToGZip() throws IOException
	{
		if (bufferCount > 0)
		{
			writeToGZip(buffer, 0, bufferCount);
			bufferCount = 0;
		}

	}

	/**
	 * Write the specified byte to our output stream.
	 * 
	 * @param b
	 *            The byte to be written
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public void write(int b) throws IOException
	{
		if (closed)
			throw new IOException("Cannot write to a closed output stream");

		if (bufferCount >= buffer.length)
		{
			flushToGZip();
		}

		buffer[bufferCount++] = (byte) b;

	}

	/**
	 * Write <code>b.length</code> bytes from the specified byte array to our output stream.
	 * 
	 * @param b
	 *            The byte array to be written
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public void write(byte b[]) throws IOException
	{

		write(b, 0, b.length);

	}

	/**
	 * Write <code>len</code> bytes from the specified byte array, starting at the specified offset, to our output stream.
	 * 
	 * @param b
	 *            The byte array containing the bytes to be written
	 * @param off
	 *            Zero-relative starting offset of the bytes to be written
	 * @param len
	 *            The number of bytes to be written
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	public void write(byte b[], int off, int len) throws IOException
	{
		if (closed)
			throw new IOException("Cannot write to a closed output stream");

		if (len == 0)
			return;

		// Can we write into buffer ?
		if (len <= (buffer.length - bufferCount))
		{
			System.arraycopy(b, off, buffer, bufferCount, len);
			bufferCount += len;
			return;
		}

		// There is not enough space in buffer. Flush it ...
		flushToGZip();

		// ... and try again. Note, that bufferCount = 0 here !
		if (len <= (buffer.length - bufferCount))
		{
			System.arraycopy(b, off, buffer, bufferCount, len);
			bufferCount += len;
			return;
		}

		// write direct to gzip
		writeToGZip(b, off, len);
	}

	public void writeToGZip(byte b[], int off, int len) throws IOException
	{
		if (gzipstream == null)
		{
			response.addHeader("Content-Encoding", "gzip");
			gzipstream = new GZIPOutputStream(output);
		}
		gzipstream.write(b, off, len);

	}


	/**
	 * Has this response stream been closed?
	 */
	public boolean closed()
	{

		return (this.closed);

	}
}
