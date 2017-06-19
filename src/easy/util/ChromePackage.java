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
import easy.sql.Row;

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
	public static void release(String sourcepath, String targetpath,List<Row> jslist)
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
					for(Row f :jslist)
					{
//						System.out.println(f);
						String name = f.getString("name");
						if (sp.indexOf(name) >= 0)
						{
							String type = f.getString("type");
							Log.OutLog("混浊[%s][%s]",type,file);
							
							JFile jf = new JFile(sp);
							String con = jf.readAllText();
							
							String enjs = null;
							if ("p".equals(f.getString("type")))
							{
								enjs = JsEncode.jsPacker(con);
							}
							else
							{
								enjs = JsEncode.uglify(con);
							}
							
							if (enjs != null)
							{
								jf.WriteText(enjs);
							}
							jf.close();
							
							break;
						}
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
