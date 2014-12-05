package easy.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;

import easy.io.JFile;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2011</i></p>
 *
 * GOOGLE翻译
 *
 * @version 1.0 (<i>2011-8-15 neo(starneo@gmail.com)</i>)
 */

public class TransLate
{
	//http://translate.google.cn/translate_a/t?client=t&text=abc&hl=zh-CN&sl=en&tl=zh-CN&multires=1&prev=btn&ssel=0&tsel=0&sc=1
	private final static String TURL = "http://translate.google.cn/translate_a/t?client=t&text=%s&hl=zh-CN&sl=%s&tl=%s&multires=1&prev=btn&ssel=0&tsel=0&sc=1";
	private final static String TTSURL = "http://translate.google.cn/translate_tts?ie=UTF-8&q=%s&tl=%s&prev=input";

	
	public static void saveTTS(String str,String path)
	{
		saveTTS(str,path,null); 
	}
	
	/**
	 * 保存tts数据
	 * @param str
	 * @param path
	 * @param langue
	 */
	public static void saveTTS(String str,String path,String langue)
	{
		try
		{
			str = URLEncoder.encode(str,"utf-8");
		}
		catch(UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}
		
		if (langue==null)
		{
			langue="zh-CN";
		}
		
		try
		{
			JFile.saveHttpFile(String.format(TTSURL,str,langue), path);
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}
	
	/**
	 * 
	 * @param str
	 * @param src
	 * @param tar
	 * @return
	 */
	public static String translate(String str,String src,String tar)
	{
		try
		{
			str = URLEncoder.encode(str,"utf-8");
		}
		catch(UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}
		String restr=null;
		try
		{
			restr = JFile.loadHttpFile(String.format(TURL, str,src,tar));
			restr = Format.getContent(restr, "\"", "\"");
		}
		catch(ConnectException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return restr;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// en zh-CN fr
		//System.out.println(translate("I have an apple。","en","zh-CN"));
	}

}