package easy.servlet;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: ��������Ƽ���չ���޹�˾</i></p>
 *
 * �ļ��ϴ�
 *
 * @version 1.0 (<i>2006-5-11 Neo</i>)
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;


public class FileUpload
{
	private HashMap collection;

	private String err;

	private HttpServletRequest request;

	private String saveDir;
	
	//16k������100Mͬʱ����3000��ʹ��
	private static final int BUFSIZE = 16384;

	public FileUpload(HttpServletRequest request)
	{
		err = "";
		this.request = request;
		collection = new HashMap<String,Object>();
	}

	private void getBoundary()
	{
		String contentType = request.getContentType();
		String boundary = "";
		int BOUNDARY_WORD_SIZE = "boundary=".length();
		if (contentType == null	|| !contentType.startsWith("multipart/form-data"))
		{
			err = "Ilegal ENCTYPE : must be multipart/form-data\n";
			err = String.valueOf(err)+ String.valueOf("ENCTYPE set = ".concat(String.valueOf(String.valueOf(contentType))));
		}
		else
		{
			boundary = contentType.substring(contentType.indexOf("boundary=") + BOUNDARY_WORD_SIZE);
			boundary = "--".concat(String.valueOf(String.valueOf(boundary)));
			parseBody(boundary);
		}
	}

	public String getErrString()
	{
		return err;
	}

	public HashMap getMimeParts()
	{
		return (HashMap) collection.clone();
	}

	public String getSaveDir()
	{
		return saveDir;
	}

	/**
	 * ��������
	 * @param boundary
	 */
	private void parseBody(String boundary)
	{
		//ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		StringBuffer buf = new StringBuffer();
		try
		{
			ServletInputStream sis = request.getInputStream();
			byte b[] = new byte[BUFSIZE];
			int x = 0;
			int state = 0;
			String name = null;
			String fileName = null;
			String contentType = null;
			String tmpfilename = null;
			OutputStream tmpout = null;

			do
			{
				if ((x = sis.readLine(b, 0, BUFSIZE)) <= -1)
				{
					break;
				}

				String s = new String(b, 0, x);
				if (s.startsWith(boundary))
				{
					state = 0;
					if (name != null)
					{
						//�ϴ��ļ�
						//byte data[] = buffer.toByteArray();
						
						if (fileName == null)
						{
							//byte[] tmp = buffer.toByteArray();
							//System.out.println("************************"+new String(buf));
							tmpfilename = new String(buf);
						}
//						System.out.println(name+"--------------"+fileName+"------"+contentType+"-----------"+tmpfilename+"--------"+(fileName == null));
						
						MimeBodyPart part = new MimeBodyPart(name, tmpfilename, fileName, contentType);
						setupHashMap(part);
						//buffer = new ByteArrayOutputStream();
						buf = new StringBuffer();
						name = null;
						contentType = null;
						fileName = null;
						//data = null;
						part = null;
					}
				}
				else if (s.startsWith("Content-Disposition") && state == 0)
				{
					//�����ļ���
					state = 1;
					if (s.indexOf("filename=") == -1)
					{
						name = s.substring(s.indexOf("name=") + "name=".length(), s.length() - 2);
					}
					else
					{
						name = s.substring(s.indexOf("name=") + "name=".length(), s.lastIndexOf(";"));
						fileName = s.substring(s.indexOf("filename=")+ "filename=".length(), s.length() - 2);
						
						if (fileName.equals("\"\""))
						{
							fileName = null;
						}
						else
						{
				            // Windows���������������ļ�·��������
				            // ��Linux/Unix��Mac�����ֻ�����ļ�����
							String userAgent = request.getHeader("User-Agent");
							String userSeparator = "/";
							if (userAgent.indexOf("Windows") != -1)
								userSeparator = "\\";
							if (userAgent.indexOf("Linux") != -1)
								userSeparator = "/";
							fileName = fileName.substring(fileName.lastIndexOf(userSeparator) + 1, fileName.length() - 1);
							if (fileName.startsWith("\""))
								fileName = fileName.substring(1);
							
							//tmpfilename = TMPDIR + userSeparator + EDate.getLogDate(new Date()) + "_R"+ Integer.toString((int)(Math.random()*10000))+"_" + fileName;
							//System.out.println("---------------"+tmpfilename);
							if (tmpout != null)
							{
								tmpout.close();
							}
							//tmpout = new FileOutputStream(new File(tmpfilename))
							File f = File.createTempFile("ETUPLOAD_TEMP","");
							tmpfilename = f.getAbsolutePath();
							tmpout = new FileOutputStream(f);
						}
					}
					name = name.substring(1, name.length() - 1);
				}
				else if (s.startsWith("Content-Type") && state == 1)
				{
					//ȡ���ļ�����
					state = 2;
					contentType = s.substring(s.indexOf(":") + 2,s.length() - 2);
				}
				else if (s.equals("\r\n") && state != 3)
				{
					state = 3;
				}
				else
				{
					if (fileName == null)
					{
						buf.append(new String(b,0,x));
					}
					else
					{
						tmpout.write(b, 0, x);
						tmpout.flush();
					}
					
				}
			}
			while (true);
			sis.close();
			//buffer.close();
		}
		catch (IOException e)
		{
			err = e.toString();
		}
	}

	public void setSaveDir(String saveDir)
	{
		Collection ks = collection.values();
		Iterator i = ks.iterator();
		this.saveDir = saveDir;
		File f = new File(saveDir);
		if (!f.exists())
			f.mkdirs();
		do
		{
			if (!i.hasNext())
				break;
			Object o = i.next();
			if (o instanceof MimeBodyPart)
			{
				MimeBodyPart mbp = (MimeBodyPart) o;
				if (mbp != null && mbp.isFile())
					mbp.setSaveDir(saveDir);
			}
			else
			{
				Vector v = (Vector) o;
				int j = 0;
				while (j < v.size())
				{
					MimeBodyPart mbp = (MimeBodyPart) v.get(j);
					if (mbp != null && mbp.isFile())
						mbp.setSaveDir(saveDir);
					j++;
				}
			}
		}
		while (true);
	}

	private void setupHashMap(MimeBodyPart m)
	{
		if (m != null)
		{
			String nome = m.getName();
			Object c;
			if ((c = collection.get(nome)) == null)
				collection.put(nome, m);
			else if (c instanceof Vector)
			{
				Vector temp = (Vector) c;
				temp.add(m);
				collection.put(nome, temp);
			}
			else
			{
				Vector temp = new Vector();
				temp.add(c);
				temp.add(m);
				collection.put(nome, temp);
			}
		}
	}

	public boolean upload()
	{
		getBoundary();
		return err.equals("");
	}
}
