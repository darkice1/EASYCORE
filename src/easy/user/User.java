package easy.user;


/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * 用户类
 *
 * @version 1.0 (<i>2005-7-28 Gawen</i>)
 */

public class User
{
	/**
	 * @return Returns the gruops_id.
	 */
	public String getGroups_id()
	{
		return groups_id;
	}
	/**
	 * @param groups_id The gruops_id to set.
	 */
	public void setGroups_id(String groups_id)
	{
		this.groups_id = groups_id;
	}
	protected String id = "";
	protected String username = "";
	protected String name = "";
	protected String groups_id = "";
	protected boolean ischecked = false;
	
	public User()
	{
	}
	
	public User(String id)
	{
		this(id,"");
	}
	
	public User(String id,String username)
	{
		this.id = id;
		this.username = username;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
}