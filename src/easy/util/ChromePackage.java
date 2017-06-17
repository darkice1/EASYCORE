/**
 * 
 */
package easy.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import easy.io.JFile;

/**
 * @author starneo@gmail.com 2017年6月17日
 */
public class ChromePackage
{
	/**
	 * 寻找混浊js文件
	 * 
	 * @param sourcepath
	 * @param targetpath
	 * @throws IOException
	 */
	public static void release(String sourcepath, String targetpath,List<String> jslist)
			throws IOException
	{
		JFile.copyDir(sourcepath, targetpath);

		if (jslist != null && jslist.isEmpty() == false)
		{
			Files.walkFileTree(Paths.get(targetpath), new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file,BasicFileAttributes attrs) throws IOException
				{
					String sp = file.toAbsolutePath().toString();
//					String ext = Format.getFileExtName(file.getFileName().toString()).toLowerCase();
					
//					if ("js".equals(ext))
					boolean isok = false;
					for(String f :jslist)
					{
						if (sp.indexOf(f) >= 0)
						{
							isok = true;
							break;
						}
					}
					
					if (isok)
					{
						Log.OutLog("混浊[%s]",file);
						JFile f = new JFile(sp);
						String con = f.readAllText();
						String enjs = JsEncode.encode(con);
						if (enjs != null)
						{
							f.WriteText(enjs);
						}
						f.close();
					}
					
					return FileVisitResult.CONTINUE;
				}

			});			
		}
	}

//	/**
//	 * @param args
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException
//	{
//		List<String> list = new ArrayList<String>();
//		list.add("util.js");
//		release("/Users/Neo/Documents/svn/renwozhe/publish/renwozhe2017-05-07",
//				"/Users/Neo/Documents/svn/renwozhe/publish/renwozhe2017-05-07_pub",list);
//
//	}

}
