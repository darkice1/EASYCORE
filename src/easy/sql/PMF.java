package easy.sql;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
/**
 * <p><i>Copyright: youhow.net(c) 2005-2010</i></p>
 *
 * PMF class说明
 *
 * @version 1.0 (<i>2010-5-23 neo(starneo@gmail.com)</i>)
 */

public final class PMF
{
	private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private PMF()
	{
	}

	public static PersistenceManagerFactory get()
	{
		return pmfInstance;
	}
}