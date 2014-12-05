/**
 * 
 */
package easy.gae;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import easy.io.JFile;
import easy.util.Format;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2011</i></p>
 *
 * GAE批量更新
 *
 * @version 1.0 (<i>2011-8-4 neo(starneo@gmail.com)</i>)
 */

public class GaeUpdate
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//更新列表
		int bufsize = 512;
		
		if (args.length < 2)
		{
			System.out.println("GaeUpdate GAE路径 指定更新列表文件位置");
		}
		else
		{
			String shell = args[0];
			JFile file = new JFile(args[1]);
			List<String> list = file.getLineList();
			
			for (String r : list)
			{
				String[] t = r.split("\t");

				if (t.length >= 5)
				{
					Process process;
					
					try
					{
						String gaesource=t[0];
						String appid=t[1];
						String ver=t[2];
						String user=t[3];
						String pass=t[4];
						
						String pshell = String.format("%s -e %s update %s ", shell,user,gaesource);
						
						JFile cf  = new JFile (String.format("%s/WEB-INF/appengine-web.xml", gaesource));
						String c = cf.readAllText();
						c= Format.replaceContent(c, "<application>", "</application>",appid);
						c= Format.replaceContent(c, "<version>", "</version>",ver);
						//System.out.println(c);
						cf.WriteText(c);
						
						cf.close();

						System.out.println(pshell);
						process = Runtime.getRuntime ().exec(pshell);
						BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));  
						BufferedWriter out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream())); 

						char[] cbuf=new char[bufsize];
						while(in.read(cbuf)>=0)
						{
							String buf = new String(cbuf);
							System.out.print(buf);
							
							if (buf.indexOf("Email: ") >= 0)
							{
								System.out.println(user);
								out.write(String.format("%s\n", user));
								out.flush();
							}					
							if (buf.indexOf("Password for ") >= 0)
							{
								//System.out.println(pass);
								System.out.println("*******");
								out.write(String.format("%s\n", pass));
								out.flush();
							}								
							
							cbuf=new char[bufsize];
						}
						
						// while((s=bufferedReader.readLine()) != null){  
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				
			}
		}
	}

}
