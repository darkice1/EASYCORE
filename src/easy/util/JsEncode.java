/**
 * 
 */
package easy.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import easy.io.EHttpClient;

/**
 * @author starneo@gmail.com 2017年6月17日
 */
public class JsEncode
{
	public static String encode(String js) throws ClientProtocolException, IOException
	{
		String ec = null;
		
		EHttpClient client = new EHttpClient();
		Map<String,String> post = new HashMap<String,String>();
		
		post.put("code", js);
		
		ec = client.postToString("http://tool.css-js.com/actions/jspacker.php?type=encode", post);
		
		return ec;
	}
//
//	/**
//	 * @param args
//	 * @throws IOException 
//	 * @throws ClientProtocolException 
//	 */
//	public static void main(String[] args) throws ClientProtocolException, IOException
//	{
//		System.out.println(encode("abc"));
//
//	}
}
