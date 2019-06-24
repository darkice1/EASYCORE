package demo.action.commit;

import easy.action.CommitAction;
import easy.sql.BaseTable;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 * CommitActoin Demo class说明
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
		add("democommit1");
		
		
		BaseTable b = new BaseTable();
		b.setTablename("democommit2");
		b.AddPro("cr_date", "NOW()");
		
		add("democommit2",b);
		url = "/demo/commit/index.jsp";
	}

}
