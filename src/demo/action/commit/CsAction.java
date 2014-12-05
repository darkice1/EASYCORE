package demo.action.commit;

import easy.action.Action;
import easy.sql.CPSql;
import easy.sql.DataSet;
import easy.sql.SelectDataSet;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2007<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 *
 * @version 1.0 (<i>2007-3-12 neo</i>)
 */

public class CsAction extends Action
{
	/**
	 * @see easy.servlet.Action#Perform()
	 */
	@Override
	public void Perform() throws Exception
	{
		
		CPSql sql=new CPSql();
		
		
		DataSet ds=sql.executeQuery("select * from users");
		
		sql.close();
		
		SelectDataSet sd=new SelectDataSet();
		
		
		//sd.AddWhere("username","=","bbbxxdx");
		
		//sd.AddWhere("sex","=","M");
		
		//sd.AddWhere("id","<","420");
		
		//System.out.println("2007-04-17 00:00:00".length());
		
		sd.AddWhere("crtime",">","2007-04-15","and");
		
		sd.AddWhere("groups_id","=","4","or");
		
		sd.AddWhere("groups_id","=","1","or");
		
		
		
	
		
		//sd.AddWhere("id",">","400");
		//sd.AddWhere("crtime",">","2007-04-29 00:00:00");
		
		//sd.AddWhere("crtime","<","2007-05-10 00:00:00");
		
		//sd.AddWhere("id",">","200");
		//sd.AddWhere("id","<","300");
		
		//sd.AddWhere("username","=","wang");
		//sd.AddWhere("crtime",">","2007-02-26 15:44:01");
		
		ds=sd.AccurateSelect(ds);
		 
		//ds=sd.AccurateSelect(ds);
		
		System.out.println("info=="+sd.getInfo());
		  
		
		if(ds!=null)
		{
			while(ds.next())
			{
				System.out.println("===============================");
				
				System.out.println("id=="+ds.getString("id"));
				
				System.out.println("username=="+ds.getString("username"));
				
				System.out.println("password=="+ds.getString("password"));
				
				System.out.println("crtime=="+ds.getString("crtime"));
				
				System.out.println("groups_id=="+ds.getString("groups_id"));
				 
				
			}
		}
		
		
	

		
	
		
	}

}
