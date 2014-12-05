package easy.robot.chat;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.HashMap;

import net.sf.json.JSONObject;

import easy.io.JFile;
import easy.util.Log;

public class Xiaoi extends ChatRobot
{
	@Override
	public String chat(String msg) throws ConnectException, IOException
	{
		//{"sessionId":"22678bc1d8e94f148932093f9eb073d6","robotId":"webbot",
		//"userId":"bdd5a9a8117b4ae685c43408c44942f3","body":{"content":"test"},"type":"txt"}
		HashMap<String,String> dmap = new HashMap<String,String>();
		dmap.put("sessionId", "22678bc1d8e94f148932093f9eb073d6");
		dmap.put("robotId", "webbot");
		dmap.put("userId", "bdd5a9a8117b4ae685c43408c44942f3");
		dmap.put("type", "txt");
		dmap.put("sessionId", "22678bc1d8e94f148932093f9eb073d6");
		
		JSONObject json = JSONObject.fromObject(dmap);
		
		HashMap<String,String> cmap = new HashMap<String,String>();
		cmap.put("content", msg);
		json.put("body", cmap);
		
		String data = json.toString();
		data = "{\"sessionId\":\"22678bc1d8e94f148932093f9eb073d6\",\"robotId\":\"webbot\",\"userId\":\"bdd5a9a8117b4ae685c43408c44942f3\",\"body\":{\"content\":\"test\"},\"type\":\"txt\"}";
		data = URLEncoder.encode(data,"utf-8");

		
		//dmap.put("sessionId", "22678bc1d8e94f148932093f9eb073d6");
		//String data = String.format("{\"sessionId\":\"22678bc1d8e94f148932093f9eb073d6\",\"robotId\":\"webbot\",\"userId\":\"bdd5a9a8117b4ae685c43408c44942f3\",\"body\":{\"content\":\"test\"},\"type\":\"txt\"}", args)
		String url = String.format("http://i.xiaoi.com/robot/webrobot?&callback=__webrobot_processMsg&data=%s&ts=%d", data,System.currentTimeMillis());
		System.out.println(url);
		String html = JFile.loadHttpFile(url, 
						"cnonce=496584; sig=7c6ba5921c954627822c03c3991b95abdaa800ec; XISESSIONID=w6qanxyfcin31vf77jsg5e3ag; nonce=736914",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36",
						null, 
						"http://i.xiaoi.com/");
		System.out.println(html);

		
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Xiaoi c = new Xiaoi();
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
