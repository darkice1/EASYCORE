package easy.robot.chat;

import easy.io.JFile;
import easy.util.Log;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;


public class Onexin extends ChatRobot
{
	@Override
	public String chat(String msg) throws ConnectException, IOException
	{
		msg = msg.replace("#", "");

		String url = String.format("http://we.onexin.com/?mod=dialog&do=talk&title=%s", msg);
		
		String html = JFile.loadHttpFile(url);
		//System.out.println(html);
		JSONObject json = JSONObject.fromObject(html);
		String recontent = json.getString("message");
		//System.out.println(json.get("message"));
		//System.out.println(json.get("msgbox"));

		return recontent;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Onexin c = new Onexin();
		try
		{
			System.out.println(c.chat("上传了20张照片到专辑“闪过心头、那一霎那的风景” "));
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}

}
