package demo.action.upload;

import easy.action.Action;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: ��������Ƽ���չ���޹�˾</i></p>
 *
 * TODO TestUploadAction class˵��
 *
 * @version 1.0 (<i>2007-1-22 neo</i>)
 */

public class TestUploadAction extends Action
{
	/**
	 * @see easy.action.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		//设置编码
		//System.out.println ("");
		/*
		String b = new BASE64Encoder().encode(request.getParameter("name").getBytes());	
		//System.out.println (b);
		b = new String(new BASE64Decoder().decodeBuffer(b),"utf-8");
		//System.out.println (b);
		request.setAttribute("name", b);
		*/
		
		if (request.getFileName("f1") != null && request.getSize("f1") > 0)
		{
			request.saveAs("f1", "/upload/"+request.getFileName("f1"));
		}
		if (request.getFileName("f2") != null  && request.getSize("f2") > 0)
		{
			request.saveAs("f2", "/upload/"+request.getFileName("f2"));
		}

		request.setAttribute("name", request.getParameter("name"));

		url = "/demo/upload/index.jsp";

	}

}
