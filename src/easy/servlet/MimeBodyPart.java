package easy.servlet;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: ��������Ƽ���չ���޹�˾</i></p>
 *
 * TODO MimeBodyPart class˵��
 *
 * @version 1.0 (<i>2006-5-11 Neo</i>)
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MimeBodyPart
{

	private String contentType;
	
	private final static int BUFSIZE = 1024*1024;

	// private byte data[];

	private String tmpfilename;

	private String err;

	private String fileName;

	private boolean isFile;

	private long size;

	private String name;

	private boolean overwrite;

	private String saveDir;

	private final String tempDir = System.getProperty("java.io.tmpdir");

	private File file;

	public MimeBodyPart(String name, String tmpfilename, String fileName,String contentType)
	{
		overwrite = false;
		err = "";
		this.name = name;
		this.tmpfilename = tmpfilename;
		this.contentType = contentType;
		this.fileName = fileName;

		isFile = fileName != null;

		if (isFile)
		{
			file = new File(tmpfilename);
			size = file.length();
		}
		else
		{
			size = tmpfilename.length();
		}
	}

	/*
	 * public byte[] getBytes() { return (byte[]) data.clone(); }
	 * 
	 * public String getContentType() { if (isFile) return contentType; else return null; }
	 */
	//*
	public String getDataAsString()
	{
		/*
		if (isFile)
		{
			return ""; 
		} 
		else
		{
			//byte[] data = out.toByteArray(); 
			//return new String(data, 0, data.length);
			return "";
		}
		*/
		//return "OK";
		if (isFile)
		{
			return ""; 
		} 
		else
		{
			return tmpfilename;
		}

		// byte[] data = out.toByteArray();
		// return new String(data, 0, data.length);
		//return "OK";
	}



	public String getErrString()
	{
		return err;
	}

	public String getFileName()
	{
		if (isFile)
			return fileName;
		else
			return "Either this is not a file, but a form text field, or it's empty.";
	}

	public long getFileSize()
	{
		if (isFile)
			return size;
		else
			return 0;
	}

	public String getName()
	{
		return name;
	}

	public boolean getOverwrite()
	{
		return overwrite;
	}

	public String getSaveDir()
	{
		return saveDir;
	}

	public boolean isFile()
	{
		return isFile;
	}

	/*
	 * public boolean saveAsGZipFile() { if (isFile) { if (fileName != null && data != null) { if (saveDir == null || saveDir.equals("")) saveDir = tempDir; try { File f = new File(String.valueOf(String .valueOf((new StringBuffer(String.valueOf(String .valueOf(saveDir)))).append(fileName) .append(".gz")))); if (f.exists() && overwrite || !f.exists()) { GZIPOutputStream zos = new GZIPOutputStream( new FileOutputStream(f)); zos.write(data); zos.close(); fileName = String.valueOf(String.valueOf(fileName)) .concat(".gz"); } else { fileName = GeneralUtilities.generateFileName(saveDir, String.valueOf(String.valueOf(fileName)) .concat(".gz")); f = new File(String.valueOf(saveDir) + String.valueOf(fileName)); GZIPOutputStream zos = new GZIPOutputStream( new FileOutputStream(f)); zos.write(data); zos.close(); } } catch (IOException e) { err = e.toString(); boolean flag = false; return flag; } } } else { err = "This object is not a file and won't be saved."; return false; } return true; }
	 * 
	 * public boolean saveAsZipFile() { if (isFile) { if (fileName != null && data != null) { if (saveDir == null || saveDir.equals("")) saveDir = tempDir; try { File f = new File(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(saveDir)))).append(fileName).append(".zip")))); if (f.exists() && overwrite || !f.exists()) { ZipOutputStream zos = new ZipOutputStream( new FileOutputStream(f)); ZipEntry ze = new ZipEntry(String.valueOf(saveDir) + String.valueOf(fileName)); zos.putNextEntry(ze); zos.write(data); zos.closeEntry(); zos.close(); fileName = String.valueOf(String.valueOf(fileName)) .concat(".zip"); } else { fileName = GeneralUtilities.generateFileName(saveDir,String.valueOf(String.valueOf(fileName)).concat(".zip")); f = new File(String.valueOf(saveDir) + String.valueOf(fileName)); ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(f)); ZipEntry ze = new ZipEntry(String.valueOf(saveDir)+ String.valueOf(fileName)); zos.putNextEntry(ze); zos.write(data); zos.closeEntry(); zos.close(); } } catch (IOException e) { err = e.toString(); boolean flag = false; return flag; } } } else { err = "This object is not a file and won't be saved."; return false; } return true; }
	 */
	public boolean saveFile()
	{
		if (isFile)
		{
			if (fileName != null)
			{
				if (saveDir == null || saveDir.equals(""))
					saveDir = tempDir;
				try
				{
					File f = new File(String.valueOf(saveDir)
							+ String.valueOf(fileName));
					if (f.exists() && overwrite || !f.exists())
					{
						/*
						 * try { //f.renameTo(file); //System.out.println("move:"+file.renameTo(f)); rename(tmpfilename,String.valueOf(saveDir)+ String.valueOf(fileName)); } catch (Exception ex) { ex.printStackTrace(); }
						 */
						rename(tmpfilename, String.valueOf(saveDir)
								+ String.valueOf(fileName));

						/*
						 * OutputStream os = new FileOutputStream(f); out.writeTo(os); out.flush(); out.close();
						 * 
						 * os.write(size); os.flush(); os.close();
						 */
					}
					else
					{
						fileName = GeneralUtilities.generateFileName(saveDir,
								fileName);
						rename(tmpfilename, String.valueOf(saveDir) + String.valueOf(fileName));
						// f = new File(String.valueOf(saveDir)+ String.valueOf(fileName));

						// f = new File("d:\\aaa");
						// f.renameTo(file);
						// file.renameTo(f);
						// System.out.println("move:"+file.renameTo(f));
						/*
						 * OutputStream os = new FileOutputStream(f); out.writeTo(os); out.flush(); out.close();
						 * 
						 * os.write(size); os.flush(); os.close();
						 */
					}
					System.out.println(f.getAbsoluteFile() + "     "+ file.getAbsolutePath());
				}
				catch (Exception e)
				{
					err = e.toString();
					boolean flag = false;
					return flag;
				}
			}
		}
		else
		{
			err = "This object is not a file and won't be saved.";
			return false;
		}
		return true;
	}

	private void rename(String oldname, String newname)
	{
		File inFile = new File(oldname);
		File outFile = new File(newname);

		//System.out.println(inFile.getAbsolutePath()+"********************************************"+outFile.getAbsolutePath());
		try
		{
			FileInputStream fis = new FileInputStream(inFile);
			FileOutputStream fos = new FileOutputStream(outFile);
			
			byte[] buf = new byte[BUFSIZE];
			int c;
			while ((c = fis.read(buf)) > 0)
			{
				fos.write(buf,0,c);
			}
			
			/*
			int c;	
			while ((c = fis.read()) != -1) // �ж�,�����ļ��е�������û�н�����-1��ô�������fos��дͬ������
			{
				fos.write(c);
			}
			*/
			fis.close();// ע�⣬�������д�������ڴ�ռ��
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println(e);
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
		inFile.delete();
	}

	/*
	 * public boolean saveToDatabaseAsBLOB(String driver, String url, String uid, String pwd, String command) { boolean ok = true; try { Class.forName(driver); Connection c = DriverManager.getConnection(url, uid, pwd); if (c != null) { PreparedStatement ps = c.prepareStatement(command); ps.setBytes(1, data); ps.execute(); ps.close(); c.close(); } } catch (SQLException e) { err = e.toString(); ok = false; } catch (ClassNotFoundException e1) { err = e1.toString(); ok = false; } return ok; }
	 */
	public void setOverwrite(boolean overwrite)
	{
		this.overwrite = overwrite;
	}

	public void setSaveDir(String saveDir)
	{
		this.saveDir = saveDir;
		File f = new File(saveDir);
		if (!f.exists())
			f.mkdirs();
	}
}
