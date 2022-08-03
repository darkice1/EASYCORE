package easy;

import easy.sql.CPSql;
import easy.sql.DataSet;

import java.sql.SQLException;

/**
 * @author starneo@gmail.com Mar 22, 2019
 */
public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws SQLException
	{
		CPSql sql = new CPSql();
		DataSet ds = sql.executeQuery("select 1");
		sql.close();
	}

}
