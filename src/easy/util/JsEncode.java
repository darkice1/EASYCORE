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
	public static String uglify(String js) throws ClientProtocolException, IOException
	{
		String ec = null;
		
		EHttpClient client = new EHttpClient();
		Map<String,String> post = new HashMap<String,String>();
		
		post.put("code", js);
		
		ec = client.postToString("https://tool.css-js.com/!nodejs3/uglify.do?action=compressor&loops=true&sequences=true&if_return=true&unused=true&evaluate=true&hoist_funs=true&comparisons=true&hoist_vars=true&conditionals=true&dead_code=true&booleans=true&properties=false&unsafe=false&join_vars=true", post);
		
		return ec;
	}
	
	public static String jsPacker(String js) throws ClientProtocolException, IOException
	{
		String ec = null;
		
		EHttpClient client = new EHttpClient();
		Map<String,String> post = new HashMap<String,String>();
		
		post.put("code", js);
		
		ec = client.postToString("https://tool.css-js.com/actions/jspacker.php?type=encode", post);
		
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
//		System.out.println(uglify("var a=1;"));
//
//	}
}
