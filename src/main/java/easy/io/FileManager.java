package easy.io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * TODO FolderManager class˵��
 *
 * @version 1.0 (<i>2005-7-26 Gawen</i>)
 */

public class FileManager
{
    public static boolean mkdir(String folderName)
    {
    	return new File(folderName).mkdir();
    }
    
    public static boolean delete(String folderName)
    {
    	File file = new File(folderName);
    	return delete(file);	
    }
    
    public static boolean delete(File file)
    {
    	if (!file.exists())	return false;
    	if ( file.isFile()) return file.delete();
    
    	File[] files = file.listFiles();
		for (File value : files)
		{
			delete(value);
		}
    	return file.delete();
    }
    
    public static String[] list(String path) throws FileNotFoundException
    {
    	return list(new File(path));
    }
    
    public static String[] list(File file) throws FileNotFoundException
    {
    	if (!file.exists())
    	{
    		throw new FileNotFoundException("not found the path/file[" + file.getPath() + "]");
    	}
    	else
    	{
    		return file.list();
    	}
    }
    
    public static File[] listFiles(String path) throws FileNotFoundException
    {
    	return listFiles(new File(path));
    }
    
    public static File[] listFiles(File file) throws FileNotFoundException
    {
    	if (!file.exists())
    	{
    		throw new FileNotFoundException("not found the path/file[" + file.getPath() + "]");
    	}
    	else
    	{
    		return file.listFiles();
    	}
    }
    
    public static List<File> getFiles(String path) throws FileNotFoundException
    {
    	return getFiles(new File(path));
    }
    
    public static List<File> getFiles(File file) throws FileNotFoundException
    {
    	File[] files = listFiles(file);
    	List<File> list = new ArrayList<>();
		for (File value : files)
		{
			if (value.isFile())
			{
				list.add(value);
			}
		}
    	
    	return list;
    }
    
    public static List<File> getDirs(String path) throws FileNotFoundException
    {
    	return getDirs(new File(path));
    }
    
    public static List<File> getDirs(File file) throws FileNotFoundException
    {
    	File[] files = listFiles(file);
    	List<File> list = new ArrayList<>();
		for (File value : files)
		{
			if (value.isDirectory())
			{
				list.add(value);
			}
		}
    	
    	return list;
    }
    
    public static List<File> getAllDirs(String path) throws FileNotFoundException
    {
    	return getAllDirs(new File(path));
    }
    
    public static List<File> getAllDirs(File file) throws FileNotFoundException
    {
    	List<File> list = new ArrayList<>();
    	if (!file.exists())
    	{
    		throw new FileNotFoundException("not found the path/file[" + file.getPath() + "]");
    	}
    	else
    	{
    		list.add(file);
    		return getAllDirs(list,file);
    	}
    }
    
    private static List<File> getAllDirs(List<File> list,File file) throws FileNotFoundException
    {
    	List<File> dirs = getDirs(file);
		for (File dir : dirs)
		{
			list.add(dir);
			getAllDirs(list, dir);
		}
    	return list;
    }
    /**
     * 
     * @param srcFile
     * @param objFile obj path
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean move(String srcFile,String objFile) throws FileNotFoundException,IOException
    {
    	BufferedReader bufReader = new BufferedReader(new FileReader(srcFile));
    	PrintWriter printWriter	= new PrintWriter(new FileOutputStream(objFile + "/" + new File(srcFile).getName()));
    	String line;
    	while ( (line=bufReader.readLine()) != null)
    	{
    		printWriter.println(line);
    	}
    	bufReader.close();
    	printWriter.close();
    	return true;
    }
    
    public static boolean moveAsStream(String srcFile,String objFile) throws IOException
    {
    	FileInputStream		fis = new FileInputStream(srcFile);
    	FileOutputStream 	fos = new FileOutputStream(objFile);
    	byte[] b = new byte[1024];
    	while (fis.read(b,0,1024) != -1)
    	{
    		fos.write(b);
    	}
    	fis.close();
    	fos.close();
    	return true;
    }
}
