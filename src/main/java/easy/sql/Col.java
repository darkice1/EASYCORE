package easy.sql;



import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

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
		this.value = Objects.requireNonNullElse(value, "");
		this.fieldname = fieldname;
	}

	public Col (String fieldname,String value)
	{
		this.value = Objects.requireNonNullElse(value, "");
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

		return switch (value)
		{
			case Integer i -> Integer.compare(i, (Integer) o.value);
			case Long l -> Long.compare(l, (Long) o.value);
			case Float v -> Float.compare(v, (Float) o.value);
			case Double v -> Double.compare(v, (Double) o.value);
			case byte[] bytes -> new String(bytes).compareTo(new String((byte[]) o.value));
			default -> value.toString().compareTo(o.value.toString());
		};
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

	@Override
	public String toString()
	{
		JSONObject json = new JSONObject();
		json.put(fieldname,value);
		return json.toString();
	}
}