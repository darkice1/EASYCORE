package easy.action;

import java.io.PrintWriter;

import net.sf.json.JSONObject;
import easy.util.Format;

/**
 * 
 * @author Neo(starneo@gmail.com)2014-4-1
 *
 */
public abstract class JsonAction extends ListXmlAction
{
	protected JSONObject json = new JSONObject();
	
	/**
	 * @see easy.servlet.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		setPageNum();
		initList();
		
		if (request.getParameter("pagesize") != null)
		{
			try
			{
				pagesize = Integer.parseInt(request.getParameter("pagesize"));
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		
		if (request.getParameter("order") != null)
		{
			order = request.getParameter("order");
		}
		initSql();
		
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		response.setDateHeader("Expires", 0); // prevents caching at the proxy server
		response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
		response.setHeader("Content-Type", "application/json");

		PrintWriter out = response.getWriter();
		json.put("usetime", System.currentTimeMillis() - starttime);
		out.print(Format.listToJsonString(dataset.getRowList(),json));
		out.close();
	}
//	
//	public static void main (String[] args)
//	{
//		JSONObject json = new JSONObject();
//		List<Row> list = new ArrayList<Row>();
//		for (int i=0; i<10 ;i++)
//		{
//			Row r = new Row();
//			r.putInteger("a"+i, i);
//			r.putInteger("b"+i, i);
//
//			list.add(r);
//		}
//	
////		json.p
////		json.put("result", list);
//
//		json.put("aa", 111);
//		json.put("bb", 111);
//
//		System.out.println(Format.listToJsonString(list,json));
//	}
}