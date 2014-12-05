/**
 * 
 */
package easy.robot.chat;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URLEncoder;

import easy.io.JFile;
import easy.util.Log;

/**
 * @author Neo(starneo@gmail.com)2014-7-5
 *
 */
public class Douqq extends ChatRobot
{
	/* (non-Javadoc)
	 * @see easy.robot.chat.ChatRobot#chat(java.lang.String)
	 */
	@Override
	public String chat(String msg) throws ConnectException, IOException
	{
		//http://xiao.douqq.com/api.php?msg=
		msg = msg.replace("#", "");
		msg = msg.replace(" ", ".");
		msg = URLEncoder.encode(msg,"utf-8");

		//java.net.URLEncoder.encode(msg,"utf-8")
		String url = String.format("http://xiao.douqq.com/api.php?type=txt&msg=%s",msg);
		//System.out.println(url);
		String html = JFile.loadHttpFile(url);
		
		return html;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Douqq c = new Douqq();
		try
		{
			System.out.println(c.chat("[泪流满面]原來巧克力裡面還有放了告白信 過了8天我才知道 靠杯我還想說他幹嘛每天告訴我他行程 這下只能繼續裝死了"));
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}
}
