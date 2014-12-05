/**
 * 
 */
package demo.action.ajaxaction;

import easy.action.Action;
import easy.action.tools.ProgressBarAction;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2011</i></p>
 *
 * ProgressBarDemoAction class说明
 *
 * @version 1.0 (<i>2011-1-21 neo(starneo@gmail.com)</i>)
 */

public class ProgressBarDemoAction extends Action
{

	/* (non-Javadoc)
	 * @see easy.action.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		ProgressBarAction pb = (ProgressBarAction) session.getAttribute("pb");
		
		if (pb == null)
		{
			 pb = new ProgressBarAction();
			 pb.setCurpg(0);
			 pb.setMaxpg(100);
		}
		else
		{
			pb.setCurpg(pb.getCurpg()+10);
		}
		
		session.setAttribute("pb", pb);
	}

}
