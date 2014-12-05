/**
 * 
 */
package easy.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Neo(starneo@gmail.com)2013-11-16
 *
 */
public class WebAgent
{
	private final static List<String> LIST = new LinkedList<String>();
	private final static List<String> MOBLIST = new LinkedList<String>();
	
	static
	{
		initlist();
		initmoblist();
	}
	
	private static void initmoblist()
	{
		MOBLIST.add("Mozilla/5.0 (iphone; U; CPU iPhone OS 4_3_5 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
		MOBLIST.add("Mozilla/5.0 (Linux;u;Android 2.3.7;zh-cn;HTC Desire Build) AppleWebKit/533.1 (KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 2.2.1; zh-cn; lepad_001b Build/PQXU100.4.0091.033011) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 4.1.1; zh-cn; N90 DUAL CORE2 CZ Build/JRO03H) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30");
		MOBLIST.add("MQQBrowser/4.3/Mozilla/5.0 (Linux; U; Android 4.2.2; zh-cn; vivo Y11 Build/JDQ39) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
		MOBLIST.add("Mozilla/5.0 (Linux; Android 4.3; Nexus 7 Build/JWR66Y) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.92 Safari/537.36");
		MOBLIST.add("CoolPad8026_CMCC_TD/1.0 Linux/2.6.32 Android/2.3 Release/03.15.2012 Browser/WAP2.0 (AppleWebKit/533.1) Mobile Profile/MIDP-2.1 Configuration/CLDC-1.1");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 4.1.2; zh-cn; GT-N7100 Build/JZO54K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
		MOBLIST.add("Mozilla/5.0 (Android; Mobile; rv:21.0) Gecko/21.0 Firefox/21.0 commoncrawl.org/research//Nutch-1.7-SNAPSHOT");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 4.1.2; zh-CN; Coolpad_7295A Build/JZO54K) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1 UCBrowser/8.8.3.272 Mobile");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 4.1.2; zh-tw; GT-I9300 Build/JZO54K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
		MOBLIST.add("Nokia6120c/3.83; Profile/MIDP-2.0 Configuration/CLDC-1.1");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 4.1.1; zh-CN; MI 2 Build/JRO03L) AppleWebKit/534.31 (KHTML, like Gecko) UCBrowser/9.3.0.321 U3/0.8.0 Mobile Safari/534.31");
		MOBLIST.add("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A501 Safari/9537.53");
		MOBLIST.add("Dalvik/1.6.0 (Linux; U; Android 4.2.1; Lenovo A830 Build/JOP40D)");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; Lenovo A690 Build/GRK39F) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 BaiduBoxApp/3.6_7300091a");
		MOBLIST.add("Mozilla/5.0 (Linux; U; Android 4.0; en-us; Tuna Build/IFK77E) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.67 commoncrawl.org/research//Nutch-1.7-SNAPSHOT");
		MOBLIST.add("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7 commoncrawl.org/research//Nutch-1.7-SNAPSHOT");
		MOBLIST.add("K-Touch_T619+/960226_8514_V0101 Mozilla/5.0 (Linux; U; Android 2.3.5) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		MOBLIST.add("Mozilla/5.0 (iPhone; CPU iPhone OS 6_1_2 like Mac OS X; zh-CN) AppleWebKit/536.26 (KHTML, like Gecko) Mobile/10B146 UCBrowser/9.0.1.284 Mobile");
	}
	
	private static void initlist()
	{
		LIST.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");
		LIST.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		LIST.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
		LIST.add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		LIST.add("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		LIST.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Alexa Toolbar; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; GreenBrowser)");
		LIST.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		LIST.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		LIST.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729)");
		LIST.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
		LIST.add("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
		LIST.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)");
		LIST.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.2; WOW64; Trident/6.0; .NET4.0E; .NET4.0C; .NET CLR 3.5.30729; .NET CLR 2.0.50727; .NET CLR 3.0.30729)");
		LIST.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
		LIST.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Maxthon/3.0 Chrome/22.0.1229.79 Safari/537.1");
		LIST.add("Mozilla/5.0 (Windows NT 5.2; rv:25.0) Gecko/20100101 Firefox/25.0");
		LIST.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		LIST.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C; .NET4.0E; Media Center PC 6.0)");
		LIST.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-CN; rv:1.9.0.8) Gecko/2009032609 Firefox/3.0.8 (.NET CLR 3.5.30729)");
		LIST.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.69 Safari/537.36");
	}
	
	public static String getRandMobAgent()
	{
		return MOBLIST.get((int)(MOBLIST.size()*Math.random()));
	}

	public static String getRandAgent()
	{
		return LIST.get((int)(LIST.size()*Math.random()));
	}
}
