package easy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shell
{
	public Shell()
	{
	}

	public static String run(String command) throws IOException
	{
		return run(command, 0);
	}

	/**
	 * @param command
	 * @param timeout
	 *            超时时间(ms)
	 * @return
	 * @throws IOException
	 */
	public static String run(String command, long timeout) throws IOException
	{
		final int bufsize = 512;

		long st = System.currentTimeMillis();

		Process process = Runtime.getRuntime().exec(command);
		BufferedReader in = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
		BufferedReader ein = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
		process.getOutputStream().close();

		char charbuf[] = new char[bufsize];
		boolean isbreak = false;

		StringBuffer buf = new StringBuffer();
		if (in != null)
		{
			int byteread = 0;

			while ((byteread = in.read(charbuf)) != -1)
			{
				buf.append(charbuf, 0, byteread);
				charbuf = new char[bufsize];

				if (timeout > 0)
				{
					long et = System.currentTimeMillis();
					if (et - st > timeout)
					{
						isbreak = true;
						break;
					}
				}
			}
		}
		//System.out.println(buf.toString());
		if (isbreak == false)
		{
			if (ein != null)
			{
				int byteread = 0;

				while ((byteread = ein.read(charbuf)) != -1)
				{
					buf.append(charbuf, 0, byteread);
					charbuf = new char[bufsize];

					if (timeout > 0)
					{
						long et = System.currentTimeMillis();
						if (et - st > timeout)
						{
							break;
						}
					}
				}
			}
		}

		process.destroy();

		return buf.toString();
	}

	//
	// /**
	// * @param args
	// */
	/*
	public static void main(String[] args)
	{
		//System.out.println(aaa("ping 127.0.0.1", 5000));
		try
		{
			System.out.println(run("ping 127.0.0.1",5000));
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}
	}
	*/
}
