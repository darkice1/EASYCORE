package easy.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * TODO Zip class说明
 *
 * @version 1.0 (<i>2005-7-27 Gawen</i>)
 */

public class Zip
{
	public static final int BUFFER_SIZE = 10240;
	
	public static void zip(String srcFile,String objFile) throws FileNotFoundException
	{
		File src = new File(srcFile);
		File obj = new File(objFile);
		if (!src.exists())
		{
			throw new FileNotFoundException(srcFile);
		}
		else if (src.isFile())
		{
			zipFile(src,obj);
		}
		else if (src.isDirectory())
		{
			zipDir(src,obj);
		}
	}
	
	private static void zipFile(File src,File obj)
	{
		
	}
	
	private static void zipDir(File src,File obj)
	{
		
	}
	
	public static List<File> unzip(String srcFile,String objFile) throws FileNotFoundException,IOException
	{
	    List<File> fileList = new ArrayList<File>();
	    
//		ZipInputStream zin = new ZipInputStream(new FileInputStream(srcFile));	
//		ZipEntry entry = null;
//		while ( (entry = zin.getNextEntry()) != null )
//		{
//			if (entry.isDirectory())
//			{
//				String name = entry.getName();
//				name = name.substring(0,name.length()-1);
//				new File(objFile + File.separator + name).mkdir();
//			}
//			else
//			{
//				File file = new File(objFile + File.separator + entry.getName());
//				file.createNewFile();
//				FileOutputStream fos = new FileOutputStream(file);
//				int b = -1;
//				while ( (b = zin.read()) != -1 )
//				{
//					fos.write(b);
//				}
//				fos.close();
//				
//				fileList.add(file);
//			}
//		}
//		zin.close();
//		return fileList;
		
		ZipInputStream in = new ZipInputStream(new FileInputStream(srcFile));
		ZipEntry entry = null;
		
		while ((entry = in.getNextEntry()) != null)
		{
			//System.out.println(entry.getName());
			if (entry.isDirectory())
			{
				new File(objFile + File.separator + entry.getName()).mkdir();
			}
			else
			{
				File file = new File(objFile + File.separator + entry.getName());
				//处理目录文件	
		        if (!file.getParentFile().exists())
		        {
		            file.getParentFile().mkdirs();
		        }

				FileOutputStream fos = new FileOutputStream(file);
				
				int b = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				
				while ((b = in.read(buffer)) != -1)
				{
					fos.write(buffer,0,b);
				}
				
				fos.close();
				fileList.add(file);
			}
		}
		
		in.close();
		return fileList;
	}
}
