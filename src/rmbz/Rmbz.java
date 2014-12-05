package rmbz;

import java.io.IOException;
import java.net.ConnectException;

import net.sf.json.JSONObject;
import easy.io.JFile;
import easy.robot.chat.ChatRobot;
import easy.util.Log;

public class Rmbz extends ChatRobot
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
		Rmbz c = new Rmbz();
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
