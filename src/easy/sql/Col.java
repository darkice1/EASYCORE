package easy.sql;

import java.io.Serializable;

public class Col implements Comparable<Col>,Serializable
{
	public void setFieldname(String fieldname)
	{
		this.fieldname = fieldname;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fieldname;
	private Object value;

	public void setValue(Object value)
	{
		this.value = value;
	}

	public Col()
	{
	}

	public Col (String fieldname,Object value)
	{
		if (value == null)
		{
			this.value = "";
		}
		else
		{
			this.value = value;
		}
		this.fieldname = fieldname;
	}
	
	public Col (String fieldname,String value)
	{
		if (value == null)
		{
			this.value = "";
		}
		else
		{
			this.value = value;
		}
		this.fieldname = fieldname;
	}
	
	public Col (String fieldname,int value)
	{
		this.fieldname = fieldname;
		this.value = value;
	}
	
	public Col (String fieldname,float value)
	{
		this.fieldname = fieldname;
		this.value = value;
	}
	
	public Col (String fieldname,long value)
	{
		this.fieldname = fieldname;
		this.value = value;
	}
	
	public Col (String fieldname,double value)
	{
		this.fieldname = fieldname;
		this.value = value;
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
		else if (value instanceof byte[])
		{
			return new String((byte[])value).compareTo(new String((byte[])o.value));
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