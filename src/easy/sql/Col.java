package easy.sql;

import java.io.Serializable;

public class Col implements Comparable<Col>,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fieldname;
	private Object value;
	
	public Col()
	{
	}
	
	public Col (String fieldname,String value)
	{
		this.fieldname = fieldname;
		this.value = value;
	}
	
	public Col (String fieldname,int value)
	{
		this.fieldname = fieldname;
		this.value = new Integer(value);
	}
	
	public Col (String fieldname,float value)
	{
		this.fieldname = fieldname;
		this.value = new Float(value);
	}
	
	public Col (String fieldname,double value)
	{
		this.fieldname = fieldname;
		this.value = new Double(value);
	}
	
	public int compareTo(Col o)
	{
//		Col c = (Col)o;

		if (value instanceof Integer)
		{
			return Integer.compare((Integer)value, (Integer)o.value);
		}
		else if (value instanceof Long)
		{
			return Long.compare((Long)value, (Long)o.value);
		}
		else if (value instanceof Float)
		{
			return Float.compare((Float)value, (Float)o.value);
		}
		else if (value instanceof Double)
		{
			return Double.compare((Double)value, (Double)o.value);
		}
		else
		{
			return value.toString().compareTo(o.value.toString());
		}
	}


	public String getFieldname()
	{
		return fieldname;
	}

	/**
	 * @return Returns the value.
	 */
	public Object getValue()
	{
		return value;
	}
}