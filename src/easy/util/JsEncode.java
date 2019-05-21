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
	public static String uglify(String js) throws IOException
	{
		String ec;
		
		EHttpClient client = new EHttpClient();
		Map<String,String> post = new HashMap<>();
		
		post.put("code", js);
		
		ec = client.postToString("https://tool.css-js.com/!nodejs3/uglify.do?action=compressor&loops=true&sequences=true&if_return=true&unused=true&evaluate=true&hoist_funs=true&comparisons=true&hoist_vars=true&conditionals=true&dead_code=true&booleans=true&properties=false&unsafe=false&join_vars=true", post);
		
		return ec;
	}
	
	public static String jsPacker(String js) throws IOException
	{
		String ec;
		
		EHttpClient client = new EHttpClient();
		Map<String,String> post = new HashMap<>();
		
		post.put("code", js);
		
		ec = client.postToString("https://tool.css-js.com/actions/jspacker.php?type=encode", post);
		
		return ec;
	}

//	public static void main(String[] args) throws IOException
//	{
//		System.out.println(uglify("//判断是不是天猫、淘宝、飞猪\n" + "function vip_isTaobaoOrTmallOrFliggyUrl(url)\n" + "{\n" + "\tvar _divTb = $('.tb-skin:eq(0)');\n" + "\t\n" + "\tvar istb = false;\n" + "\t\n" + "\tif (_divTb.length >= 1)\n" + "\t{\n" + "\t\tistb = true;\n" + "\t}\n" + "\t\n" + "\tvar matc = null ;\n" + "\tif (istb == false)\n" + "\t{\n" + "\t\tmatc = url.match(/^(http|https):\\/\\/(.*?)\\.(taobao|tmall|fliggy|alitrip|liangxinyao)\\.(com|hk).*$/) ;\n" + "\t\t\n" + "\t\tif (matc != null)\n" + "\t\t{\n" + "\t\t\tistb = true;\n" + "\t\t}\n" + "\t}\n" + "\n" + "\tif (istb == true)\n" + "\t{\n" + "\t\tif (url.indexOf(\"id=\") < 0 || url.indexOf(\"item.htm\")<0)\n" + "\t\t{\n" + "\t\t\tmatc=null;\n" + "\t\t}\n" + "\t\telse\n" + "\t\t{\n" + "\t\t\tmatc = location.origin + location.pathname+\"?id=\"+getQueryString(\"id\");\n" + "\t\t}\n" + "\t}\n" + "\t\n" + "\treturn matc ;\n" + "}\n" + "\n" + "//main 入口\n" + "var tburl = vip_isTaobaoOrTmallOrFliggyUrl(location.href);\n" + "if(tburl != null)\n" + "{\t\n" + "\t//获取优惠券 or 跳转 or null\n" + "\ttburl = \"http://s.renwozhe.com/c?action=GetTaokeInfo&url=\"+encodeURIComponent(tburl);\n" + "\tchrome.runtime.sendMessage({types: \"getJson\",url:tburl}, function(response) \n" + "\t{\n" + "\t\t//{\"taokeurl\":{\"csurl\":,\"surl\":,\"pid\":}}\n" + "\t\tvar _divTb = $('.tb-skin:eq(0)');\n" + "\t\tif(response.taokeurl != null && response.taokeurl.surl != null)\n" + "\t\t{\n" + "\t\t\t//优惠券\n" + "\t\t\tif(response.taokeurl.csurl != null)\n" + "\t\t\t{\n" + "\t\t\t\t//判断是否已激活\n" + "\t\t\t\tif(location.href.indexOf(response.taokeurl.pid) > 0)\n" + "\t\t\t\t{\n" + "\t\t\t\t\t//_divTb.append('<div class=\"rwzRebateBar rwzGreen\"><a class=\"rwzRebateBarRight\" style=\"cursor: default;\" ><div style=\"line-height: 35px; padding-left: 76px; font-size: 18px;\">已激活,购买即减</div></a><div style=\"clear: both;\"></div></div>');\n" + "//\t\t\t\t\tga('send', 'event', '激活优惠券',document.title, location.href);\n" + "\t\t\t\t}\n" + "\t\t\t\telse\n" + "\t\t\t\t{\n" + "\t\t\t\t\t//音乐\n" + "\t\t\t\t\tvar notifyAudio = new Audio(chrome.extension.getURL(\"notify.wav\"));\n" + "\t\t\t\t\tnotifyAudio.play();\n" + "\t\t\t\t\t//_divTb.append('<div class=\"rwzRebateBar rwzRedNo\"><a href=\"'+response.taokeurl.csurl+'\" class=\"rwzRebateBarRight\" style=\"cursor:pointer;\" referrer=\"no-referrer\" rel=\"noreferrer\"><div style=\"line-height: 35px; padding-left: 76px; font-size: 18px;\">发现<span style=\"font-size: 20px; font-weight: bold;\">优惠</span>，点击激活</div></a><div style=\"clear: both;\"></div></div>');\n" + "//\t\t\t\t\tga('send', 'event', '展示优惠券',document.title, location.href);\t\t\t\n" + "\t\t\t\t\ttoUrl(response.taokeurl.surl);\n" + "\t\t\t\t}\n" + "\t\t\t}\n" + "\t\t\telse\n" + "\t\t\t{//跳转\n" + "//\t\t\t\t_divTb.append('<div class=\"rwzRebateBar rwzGreen\"><a class=\"rwzRebateBarRight\" style=\"cursor: default;\" data-spm-anchor-id=\"a220o.1000855.0.0\"><div style=\"line-height: 35px; padding-left: 76px; font-size: 18px;\">已自动跳转优惠页面</div></a><div style=\"clear: both;\"></div></div>');\n" + "\t\t\t\tif(location.href.indexOf(response.taokeurl.pid) < 0)\n" + "\t\t\t\t{\n" + "//\t\t\t\t\tga('send', 'event', '普通淘客',document.title, location.href);\n" + "//\t\t\t\t\tlocation.href = response.taokeurl.surl;\n" + "\t\t\t\t\ttoUrl(response.taokeurl.surl);\n" + "\t\t\t\t}\n" + "\t\t\t\telse\n" + "\t\t\t\t{\n" + "\t\t\t\t\t//_divTb.append('<div class=\"rwzRebateBar rwzRedNo\"><a class=\"rwzRebateBarRight\" style=\"cursor: default;\" ><div style=\"line-height: 35px; padding-left: 76px; font-size: 18px;\">当前宝贝未掉落优惠</div></a><div style=\"clear: both;\"></div></div>');\n" + "//\t\t\t\t\tga('send', 'event', '普通淘客已激活',document.title, location.href);\n" + "\t\t\t\t}\n" + "\t\t\t}\n" + "\t\t}\n" + "\t\telse\n" + "\t\t{//没优惠\n" + "\t\t\t//_divTb.append('<div class=\"rwzRebateBar rwzRedNo\"><a class=\"rwzRebateBarRight\" style=\"cursor: default;\" ><div style=\"line-height: 35px; padding-left: 76px; font-size: 18px;\">当前宝贝未掉落优惠</div></a><div style=\"clear: both;\"></div></div>');\n" + "//\t\t\tga('send', 'event', '没有优惠',document.title, location.href);\n" + "\t\t}\n" + "\t});\n" + "}\n"));
//	}
}
