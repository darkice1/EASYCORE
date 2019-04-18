package easy.sql;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import easy.util.Log;
import net.sf.json.JSONObject;

/**
 * <p>
 * <i>Copyright: Easy (c) 2005-2005 <br>
 * Company: Easy </i>
 * </p>
 * 
 * 锟斤拷菁锟斤拷锟斤拷锟�
 * 
 * @version 1.0 ( <i>2005-7-7 neo </i>)
 */

public class Row implements Comparable<Row>,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, Col> row = new HashMap<>();
	protected String sortfield;
	
	
//	@Override
//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		//row.clear();
//		row = null;
//		sortfield = null;
//	}
	
	public boolean containsKey(String key)
	{
		return row.containsKey(key);
	}
	
	public int compareTo(Row o)
	{
		Row r = o;
		return get(sortfield).compareTo(r.get(sortfield));
	}
	
	public Col get(String key)
	{
		return row.get(key);
	}

	public String[] getColsNameList ()
	{
		String[] cols = row.keySet().toArray(new String[0]);
		return cols;
	}

	public Integer getInt(String key)
	{
		Object value = row.get(key).getValue();
		if (value == null)
		{
			return null;
		}
		try
		{
			if (value instanceof Integer)
			{
				return (Integer)value;				
			}
			else
			{
				return Integer.parseInt(value.toString());
			}
		}
		catch (NumberFormatException ex)
		{
			//Log.OutException(ex,String.format("%s field value %s can't cover number.",key,value));
			return null;
		}
		catch (Exception ex)
		{
			Log.OutException(ex,String.format("%s field not find",key));
			return null;
		}
	}
	
	public Long getLong(String key)
	{
		Object value = row.get(key).getValue();
		if (value == null)
		{
			return null;
		}

		try
		{
			if (value instanceof Long)
			{
				return (Long)value;				
			}
			else
			{
				return Long.parseLong(value.toString());
			}
		}
		catch (NumberFormatException ex)
		{
			//Log.OutException(ex,String.format("%s field value %s can't cover number.",key,value));
			return null;
		}
		catch (Exception ex)
		{
			Log.OutException(ex,String.format("%s field not find",key));
			return null;
		}
	}	
	
	public Float getFloat(String key)
	{
		Object value = row.get(key).getValue();
		if (value == null)
		{
			return null;
		}
		try
		{
			if (value instanceof Float)
			{
				return (Float)value;				
			}
			else
			{
				return Float.parseFloat(value.toString());
			}
		}
		catch (NumberFormatException ex)
		{
			//Log.OutException(ex,String.format("%s field value %s can't cover number.",key,value));
			return null;
		}
		catch (Exception ex)
		{
			Log.OutException(ex,String.format("%s field not find",key));
			return null;
		}		
	}
	public Double getDouble(String key)
	{
		try
		{
			Object value = row.get(key).getValue();
			if (value == null)
			{
				return null;
			}
						
			if (value instanceof Double)
			{
				return (Double)value;				
			}
			else
			{
				return Double.parseDouble(value.toString());
			}
		}
		catch (NumberFormatException ex)
		{
			//Log.OutException(ex,String.format("%s field value %s can't cover number.",key,value));
			return null;
		}
		catch (Exception ex)
		{
			Log.OutException(ex,String.format("%s field not find",key));
			return null;
		}		
	}
	public String getString(String key)
	{
		try
		{
			Object value = row.get(key).getValue();
//			if (value == null)
//			{
//				return "";
//			}
//			else
//			{
//				return value.toString();
//			}
			return value.toString();
		}
		catch (Exception ex)
		{
			Log.OutException(ex,String.format("%s field not find",key));
			return null;
		}
	}

	public void put(String key, Col value)
	{
		row.put(key, value);
	}
	
	public void putString(String key, String value)
	{
		put(key,new Col(key,value));
	}
	
	public void putInteger(String key, int value)
	{
		put(key,new Col(key,value));
	}
	
	public void putLong(String key, long value)
	{
		put(key,new Col(key,value));
	}
	
	
	public void putFloat(String key, float value)
	{
		put(key,new Col(key,value));
	}
	
	public void putDouble(String key, double value)
	{
		put(key,new Col(key,value));
	}
	
	public void putJSONObjet(JSONObject json)
	{
		putJSONObjet(json,null);
	}
	
	public void putJSONObjet(JSONObject json,String[] addkeys)
	{
		Iterator<?> keys = json.keys();
		while (keys.hasNext())
		{
			String key = (String) keys.next();

			if (addkeys !=null)
			{
				boolean canadd = false;
				for (String k : addkeys)
				{
					if (k.equals(key))
					{
						canadd = true;
						break;
					}
				}
				if (canadd == false)
				{
					continue;
				}
			}
			putString(key,json.getString(key));
		}
	}
	
	/**
	 * @return Returns the sortfield.
	 */
	public String getSortfield()
	{
		return sortfield;
	}

	/**
	 * @param sortfield The sortfield to set.
	 */
	public void setSortfield(String sortfield)
	{
		this.sortfield = sortfield;
	}
	
	/**
	 * 锟斤拷锟斤拷sql
	 * @param tablename
	 * @return
	 */
	public String toBackSqlString(String tablename)
	{	
		StringBuffer fieldbuf = new StringBuffer();
		StringBuffer valuebuf = new StringBuffer();
		for (String str : getColsNameList())
		{
			fieldbuf.append(String.format("%s,",str));
			valuebuf.append(String.format("'%s',", getString(str).replaceAll("'","''")));
		}
		fieldbuf.setLength(fieldbuf.length()-1);
		valuebuf.setLength(valuebuf.length()-1);
		
		return String.format("INSERT INTO %s (%s) VALUES (%s);\r\n", tablename,fieldbuf,valuebuf);
	}
	
	public void remove (String field)
	{
		row.remove(field);
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		Iterator<Entry<String, Col>> rs = row.entrySet().iterator();
		Entry<String,Col> m;
		Col c;
		while (rs.hasNext())
		{
			m =  rs.next();
			c = m.getValue();
			buf.append(String.format("[%s]:[%s]\n",m.getKey(),c.getValue() ));
		}
		
		return buf.toString();
	}
}