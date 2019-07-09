package demo.action.commit;

import easy.action.CommitAction;
import easy.sql.BaseTable;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: ��������Ƽ���չ���޹�˾</i></p>
 *
 * CommitActoin Demo class˵��
 *
 * @version 1.0 (<i>2006-8-23 Neo</i>)
 */

public class DemoCommitAction extends CommitAction
{

	/**
	 * @see easy.action.CommitAction#inittable()
	 * @Override
	 */
	
	protected void inittable()
	{
		add();
		
		
		BaseTable b = new BaseTable();
		b.setTablename("democommit2");
		b.AddPro("cr_date", "NOW()");
		
		add("democommit2",b);
		url = "/demo/commit/index.jsp";
	}

}
