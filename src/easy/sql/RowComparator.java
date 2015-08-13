/**
 * 
 */
package easy.sql;

import java.util.Comparator;

/**
 * @author Neo(starneo@gmail.com)2015年8月13日
 * @param <T>
 *
 */
public class RowComparator implements Comparator<Row>
{
	private String sortfield;
	
	public RowComparator(String sortfield)
	{
		this.sortfield = sortfield;
	}

	@Override
	public int compare(Row o1, Row o2)
	{
		return o1.get(sortfield).compareTo(o2.get(sortfield));
	}

}
