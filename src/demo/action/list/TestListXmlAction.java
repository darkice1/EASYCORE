package demo.action.list;

import easy.action.ListXmlAction;

public class TestListXmlAction extends ListXmlAction
{

	//@Override
	protected void initList()
	{
		sql = "SELECT * FROM list Where 1 = 1";
		pagesize = 5;
	}
}