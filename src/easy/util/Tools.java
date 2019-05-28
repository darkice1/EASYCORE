package easy.util;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

/**
 * @program: EASYCORE
 * @description:
 * @author: Neo
 * @create: 2019-05-28 21:32
 **/

public class Tools
{
	public void JUniqueOne(final String pid)
	{
		boolean isexist;
		try
		{
			JUnique.acquireLock(pid);
			isexist = false;
		}
		catch (AlreadyLockedException e1)
		{
			isexist = true;
		}
		if (isexist)
		{
			Log.OutLog("%s已开启，无需再次启动。",pid);
			System.exit(0);
		}
	}
}
