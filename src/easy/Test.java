/**
 * 
 */
package easy;

import easy.sql.Row;

/**
 * @author starneo@gmail.com Mar 22, 2019
 */
public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		long st = System.currentTimeMillis();
		for (int i=0;i<10000000;i++)
		{
			Row r = new Row();
//			r.putDouble("ddd", 0d);
//			r.getDouble("ddd");
			
			r.putString("sss", "sss");
			r.getString("sss");
			
//			r.putFloat("fff", 0f);
//			r.putInteger("iii", 0);
			
//			new Col("sss","sss",Types.VARCHAR);
		}
		System.out.println(System.currentTimeMillis()-st);
	}

}
