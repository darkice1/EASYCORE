package easy.sql;

import java.io.Serializable;
import java.sql.Types;

public class Col implements Comparable<Col>,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int type;
	private String fieldname;
	private String value;
	
	public Col (String fieldname,String value,int type)
	{
		this.fieldname = fieldname;
		this.value = value;
		this.type = type;
	}
	
	public Col (String fieldname,int value,int type)
	{
		this.fieldname = fieldname;
		this.value = Integer.toString(value);
		this.type = type;
	}	
	
	public int compareTo(Col o)
	{
		Col c = (Col)o;

		if (type == Types.INTEGER)
		{
			return Integer.parseInt(value) - Integer.parseInt(c.value);
		}
		else if (type == Types.BIGINT)
		{
			return (int)(Long.parseLong(value) - (Long.parseLong(c.value)));
		}
		else if (type == Types.FLOAT)
		{
			return Float.compare(Float.parseFloat(value), Float.parseFloat(c.value));
		}
		else if (type == Types.DECIMAL || type == Types.DOUBLE)
		{
			return Double.compare(Double.parseDouble(value), Double.parseDouble(c.value));
		}
		else
		{
			return value.compareTo(c.value);
		}
	}


	public String getFieldname()
	{
		return fieldname;
	}


	public int getType()
	{
		return type;
	}


	/**
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}
}