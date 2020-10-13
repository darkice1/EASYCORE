package easy.util;

import java.util.List;

/**
 * <p><i>Copyright: youhow.net(c) 2005-2011</i></p>
 *
 * �����㷨
 *
 * @version 1.0 (<i>2011-5-27 neo(starneo@gmail.com)</i>)
 */

public class Algorithms
{
	/**
	 * ���ַ���������Ҫ���������
	 * @param <T>
	 * @param list
	 * @param o
	 * @return
	 */
	public static <T extends Comparable<T>> T dichotomy(List<T> list,T o)
	{
//		List<T> list = new ArrayList<T>();
//		list.addAll(l);
//
//		Collections.sort(list);
		
		int len = list.size()-1;
		
		int idx = len / 2;
		int high = len;
		int low = 0;

		while (low <= high)
		{
			//System.out.print(idx+ " ");
			T t = list.get(idx);
			int re = t.compareTo(o);
			if (re < 0)
			{
				low = idx+1;
			}
			else if (re > 0)
			{
				high = idx-1;
			}
			else 
			{
				return t;
			}
			idx =(low+high)/2;
			//System.out.println(low+" "+high+" "+idx);
//			try
//			{
//				Thread.sleep(100);
//			}
//			catch(InterruptedException e)
//			{
//				e.printStackTrace();
//			}
		}
		
		
		return null;
	}
}