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
	public static void JUniqueOne(final String pid)
	{
		try
		{
			JUnique.acquireLock(pid);
		}
		catch (AlreadyLockedException e1)
		{
			Log.OutLog("%s已开启，无需再次启动。",pid);
			System.exit(0);
		}

	}
}
