package easy.sql;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import easy.config.Config;
import easy.util.Log;


/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * ��ݿ��ￄ1�7
 *
 * @version 1.0 (<i>2005-7-7 neo</i>)
 */

public class DataSet implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient int cursor = -1; 
	//private List<Row> rowList = Collections.synchronizedList(new LinkedList<Row>());
	private List<Row> rowList = new ArrayList<Row>();
	protected final static String DBENCODEING = Config.getProperty("DBENCODEING");
	protected final static String DBENSTRINGCODEING = Config.getProperty("DBENSTRINGCODEING");
	
	public DataSet()
	{
	}
	
//	@Override
//	protected void finalize() throws Throwable
//	{
//		super.finalize();
//		//rowList.clear();
//		rowList = null;
//	}
	
	public DataSet(ResultSet rs) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		
		int cols = rsmd.getColumnCount();
		String[] colsName = new String[cols+1];
		for (int i=1;i<=cols;i++)
		{
			colsName[i] = rsmd.getColumnLabel(i);
		}
		
		while (rs.next())
		{
			Row row = new Row();
			for (int i=1;i<=cols;i++)
			{
				try
				{
					String rowstr;
					//Log.OutLog(String.format("%s %d %d %d",rsmd.getColumnName(i) ,rsmd.getColumnType(i),Types.CLOB,Types.TIMESTAMP));
					int type = rsmd.getColumnType(i);
					if (type == -4)
					{
						//-4Ϊmy sql��blob
						Blob blob = rs.getBlob(i);
						if (blob != null)
						{
							if (DBENCODEING == null  || DBENCODEING.equals(""))
							{
								rowstr = new String(blob.getBytes(1l,(int)blob.length()));
							}
							else
							{
								rowstr = new String(blob.getBytes(1l,(int)blob.length()),DBENCODEING);
							}
						}
						else
						{
							rowstr = null;
						}
						blob = null;
					}
					else if (type == Types.DATE)
					{
						try
						{
							rowstr = rs.getDate(i).toString();
						}
						catch(Exception e)
						{
							rowstr = "";
						}
					}
					else if (type == Types.TIMESTAMP)
					{
						try
						{
							if (rs.getInt(i) != 0)
							{
								rowstr = rs.getString(i).substring(0,rs.getString(i).length()-2);
							}
							else
							{
								rowstr = "";
							}
						}
						catch(Exception e)
						{
							Log.OutException(e);
							rowstr = rs.getString(i).substring(0,rs.getString(i).length()-2);
						}
					}
					else if (type == Types.CLOB)
					{
						Clob clob = rs.getClob(i);
						if (clob != null)
						{
							rowstr = clob.getSubString(1l, (int)clob.length());
						}
						else
						{
							rowstr = "";
						}
						clob = null;
					}
					else
					{
						byte[] buf = rs.getBytes(i);
						if (buf != null)
						{
							if (DBENSTRINGCODEING == null || DBENSTRINGCODEING.equals(""))
							{
								rowstr = rs.getString(i);
							}
							else
							{
								rowstr = new String(rs.getBytes(i),DBENSTRINGCODEING);
							}
						}
						else
						{
							rowstr = null;
						}
						buf = null;
					}

					row.put(colsName[i],new Col(colsName[i],rowstr == null?"":rowstr,type));
					rowstr = null;
				}
				catch (Exception e)
				{
					row.put(colsName[i],new Col(colsName[i],"",rsmd.getColumnType(i)));
					Log.OutException(e);
				}		
			}
			rowList.add(row);
			row = null; 
		}
		colsName = null;
		rsmd = null;
	}
	
	/**
	 * ���ض�Ӧ��
	 * @param idx ������
	 * @return
	 */
	public Row getRow(int idx)
	{
		return (Row)rowList.get(idx);
	}
	
	/**
	 * ���ص�ǰ��
	 * @return
	 */
	public Row getRow()
	{
		return getRow(cursor);
	}
	
	public Integer getInt(String col)
	{
		return rowList.get(cursor).getInt(col);
	}
	
	public Long getLong(String col)
	{
		return rowList.get(cursor).getLong(col);
	}
	
	public Float getFloat(String col)
	{
		return rowList.get(cursor).getFloat(col);
	}
	
	public Double getDouble(String col)
	{
		return rowList.get(cursor).getDouble(col);
	}	
	
	public String getString(String col)
	{
		return rowList.get(cursor).getString(col);
	}
	
	public boolean next()
	{
		return (++cursor) >= rowList.size()?false:true;
	}
	
	public void previous()
	{
		if (cursor>-1)
		{
			cursor--;
		}
	}
	
	public void beforeFirst()
	{
		cursor = -1;
	}
	
	/**
	 * �滻ָ���ֶ�����
	 * @param colname
	 * @param srcstr
	 * @param relpacestr
	 */
	public void replaceAll(String colname,String regex,String replacement) throws Exception
	{
		for (Row r : rowList)
		{
			r.put(colname,new Col(colname,r.getString(colname).replaceAll(regex,replacement),r.get(colname).getType()));
			r = null;
		}
	}

	public void put(String colname,Col value)
	{
		put (cursor,colname,value);
	}
	
	
	public void put(int idx,String colname,Col value)
	{
		Row r = rowList.get(idx);
		r.put(colname,value);
		r = null;
	}
	
	public void putString (String colname,String value)
	{
		put (colname,new Col(colname,value,Types.VARCHAR));
	}
	
	public void putString (int idx,String colname,String value)
	{
		put (idx,colname,new Col(colname,value,Types.VARCHAR));
	}
	
	public void putDouble(String colname,double value)
	{
		put (colname,new Col(colname,Double.toString(value),Types.DOUBLE));
	}
	
	public void putDouble (int idx,String colname,double value)
	{
		put (idx,colname,new Col(colname,Double.toString(value),Types.DOUBLE));
	}
	
	public void putInteger(String colname,int value)
	{
		put (colname,new Col(colname,Integer.toString(value),Types.INTEGER));
	}
	
	public void putInteger (int idx,String colname,int value)
	{
		put (idx,colname,new Col(colname,Integer.toString(value),Types.INTEGER));
	}	
	
	/**
	 * @return Returns the count.
	 */
	public int getCount()
	{
		return rowList.size();
	}

	/**
	 * @param count The count to set.
	 */
	public void setCount(int count)
	{
	}
	
	/**
	 * @return Returns the rowList.
	 */
	public List<Row> getRowList()
	{
		return rowList;
	}
	
	/**
	 * ��rowlist������row
	 * @param row
	 */
	public void AddRow(Row row)
	{
		rowList.add(row);
	}
	
	public void moveCursor(int position)
	{
	    if (position < 0)
	    {
	        cursor = 0;
	    }
	    else if (position >= rowList.size())
	    {
	        cursor = rowList.size() - 1;
	    }
	    else
	    {
	        cursor = position;
	    }
	}

	public void sort(String fieldname)
	{
		setSortFiled(fieldname);
		Collections.sort(rowList);
	}

	public void sort(String[] fieldnames,String[] fieldTypes,Boolean[] isDESC)
	{
		//setSortFiled(fieldnames[0]);
		//Collections.sort(rowList);
		
		String preField="",currField="";
		currField = fieldnames[0];
		boolean flag = true;
		while(flag)
		{
			flag = false;
			for(int j=1,len=rowList.size();j<len;j++)
			{
				if(fieldTypes[0].toLowerCase().equals("int") || fieldTypes[0].toLowerCase().equals("long"))
				{
					if(rowList.get(j-1).getLong(currField).longValue() == rowList.get(j).getLong(currField).longValue())
						continue;
					if((rowList.get(j-1).getLong(currField).longValue() < rowList.get(j).getLong(currField).longValue()) == isDESC[0])
					{
						Collections.swap(rowList,j-1,j);
						flag = true;
					}
				}
				else if(fieldTypes[0].toLowerCase().equals("float") || fieldTypes[0].toLowerCase().equals("double"))
				{
					if(rowList.get(j-1).getDouble(currField).doubleValue() == rowList.get(j).getDouble(currField).doubleValue()) 
						continue;
					if((rowList.get(j-1).getDouble(currField).doubleValue() < rowList.get(j).getDouble(currField).doubleValue()) == isDESC[0])
					{
						Collections.swap(rowList,j-1,j);
						flag = true;
					}
				}
				else
				{
					if(rowList.get(j-1).getString(currField).compareTo(rowList.get(j).getString(currField)) == 0) 
						continue;
					if((rowList.get(j-1).getString(currField).compareTo(rowList.get(j).getString(currField))<0) == isDESC[0])
					{
						Collections.swap(rowList,j-1,j);
						flag = true;
					}
				}
			}
		}
		//////
		for(int i=1,len=fieldnames.length;i<len;i++)
		{
			preField = fieldnames[i-1];
			currField = fieldnames[i];
			flag = true;
			while(flag)
			{
				flag = false;
				for(int j=1,jlen=rowList.size();j<jlen;j++)
				{
					if(rowList.get(j-1).getString(preField).equals(rowList.get(j).getString(preField)))
					{
						if(fieldTypes[i].toLowerCase().equals("int") || fieldTypes[i].toLowerCase().equals("long"))
						{
							if(rowList.get(j-1).getLong(currField).longValue() == rowList.get(j).getLong(currField).longValue()) continue;
							if((rowList.get(j-1).getLong(currField).longValue() < rowList.get(j).getLong(currField).longValue()) == isDESC[i])
							{
								Collections.swap(rowList,j-1,j);
								flag = true;
							}
						}
						else if(fieldTypes[i].toLowerCase().equals("float") || fieldTypes[i].toLowerCase().equals("double"))
						{
							if(rowList.get(j-1).getDouble(currField).doubleValue() == (rowList.get(j).getDouble(currField)).doubleValue()) continue;
							if((rowList.get(j-1).getDouble(currField).doubleValue() < (rowList.get(j).getDouble(currField)).doubleValue()) == isDESC[i])
							{
								Collections.swap(rowList,j-1,j);
								flag = true;
							}
						}
						else
						{
							if(rowList.get(j-1).getString(currField).compareTo(rowList.get(j).getString(currField)) == 0) continue;
							if((rowList.get(j-1).getString(currField).compareTo(rowList.get(j).getString(currField))<0) == isDESC[i])
							{
								Collections.swap(rowList,j-1,j);
								flag = true;
							}
						}
					}
				}
			}
		}
		preField= null;
		currField=null;
	}

	public void reverse(String fieldname)
	{
		sort(fieldname);
		Collections.reverse(rowList);
	}

	/**
	 * ���������ք1�7
	 * @param sortfield
	 */
	public void setSortFiled(String sortfield)
	{
		for (Row r : rowList)
		{
			r.setSortfield(sortfield);
			r = null;
		}
	}
	
	public void removeField(String field)
	{
		for (Row r : rowList)
		{
			r.remove(field);
			r = null;
		}
	}
	
	public DataSet getDataSet(int start,int count)
	{
		DataSet ds = new DataSet();
		
		int len = rowList.size() - start;
		if (len >= count)
		{
			len = start+count;
		}
		else
		{
			len = rowList.size();
		}
		//System.out.println(start+" "+len);
		
		for (int i=start; i<len; i++)
		{
			ds.AddRow(rowList.get(i));
		}
				
		return ds;
	}

	/**
	 * @return Returns the cursor.
	 */
	public int getCursor()
	{
		return cursor;
	}

	/**
	 * @param cursor The cursor to set.
	 */
	public void setCursor(int cursor)
	{
		moveCursor(cursor);
	}
	
	/**
	 * 返回csv格式tab为分隔符
	 * @param fields csv字段顺序以,分割
	 * @return
	 */
	public String toCvsString(final String fields)
	{		
		return toCsvString(fields,"\t");
	}

	/**
	 * 返回csv格式
	 * @param fields csv字段顺序以,分割
	 * @param split
	 * @return
	 */
	public String toCsvString(final String fields,final String split)
	{
		StringBuffer buf = new StringBuffer();
		String[] f = fields.split(",");
		for (int i=0,len=f.length; i<len; i++)
		{
			f[i] = new String(f[i].trim());
		}
		
		for (Row r : rowList)
		{
			int len=f.length,last=len-1;
			for (int i=0; i<len; i++)
			{
				String fieldname = f[i];
				buf.append(r.getString(fieldname));
				if (i < last)
				{
					buf.append(split);
				}
				fieldname = null;
			}
			buf.append("\n");
			r = null;
		}
		
		String sql =  buf.toString();
		buf = null;
		f = null;
		return sql;
	}
	
	/**
	 * 
	 * @param srclist 源数据
	 * @param list join数据
	 * @param srckeys 源关联key, 分割
	 * @param keys 源关联key, 分割
	 */
	public static void join(List<Row> srclist,List<Row> list,String srckeys,String keys)
	{
		join(srclist,list, srckeys, keys, null);
	}
	/**
	 * 
	 * @param srclist 源数据
	 * @param list join数据
	 * @param srckeys 源关联key, 分割
	 * @param keys 源关联key, 分割
	 * @param 向srclist增加字段, 分割
	 */
	public static void join(List<Row> srclist,List<Row> list,String srckeys,String keys,String addfields)
	{
		String[] sks = srckeys.split(",");
		String[] ks = keys.split(",");
		String[] fs = null;
		if (addfields != null)
		{
			fs = addfields.split(",");
		}

		boolean isok;
		for (Row sr : srclist)
		{
			for (Row r : list)
			{
				isok = true;

				for (int i=0,len=sks.length; i<len; i++)
				{
					if (sr.getString(sks[i]).equals(r.getString(ks[i])) == false)
					{
						isok = false;
						break;
					}
				}
				
				if (isok)
				{
					if (fs != null)
					{
						for (String f : fs)
						{
							sr.putString(f, r.getString(f));
						}
					}
					else
					{
						String tfs[] = r.getColsNameList();
						for (String f : tfs)
						{
							sr.putString(f, r.getString(f));
						}
					}

					break;
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		DataSet ds = new DataSet();
		List<Row>  srclist = ds.getRowList();
		List<Row>  list = new ArrayList<Row>();

		for (int i=0;i<=10;i++)
		{
			Row sr = new Row();
			Row r = new Row();
			
			sr.putDouble("a", Math.random());
			r.putString("b", ""+i);
			r.putString("c", "ccc"+i);

			srclist.add(sr);
			list.add(r);
		}
		
		//System.out.println(srclist);
		//System.out.println(list);

		//join(srclist,list,"a","b");
		String []fieldnames = {"a"};
		String []fieldTypes = {"double"};
		Boolean []isDESC = {false};
		ds.sort(fieldnames, fieldTypes, isDESC);

		Boolean []isDESC2 = {true};
		System.out.println(ds.getRowList());

		ds.sort(fieldnames, fieldTypes, isDESC2);

		//ds.reverse("a");
		//ds.sort("a");
		System.out.println(ds.getRowList());
	}
}
