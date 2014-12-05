//DataSet 查询

//精确查询支持 = < >  

package easy.sql;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class SelectDataSet
{

	java.util.regex.Pattern pattern_z = null; // 正则表达式
	java.util.regex.Pattern pattern = null; // 正则表达式

	java.util.regex.Matcher matcher = null; // 操作的字符串
	java.util.regex.Matcher matcher_z = null; // 操作的字符串
	int count = 0;

	String info = "搜索完成";

	ArrayList<String[]> Al = new ArrayList<String[]>();// 条件容器

	DataSet dataset = new DataSet();;// 最终返回的dataset

	// ADD条件
	public void AddWhere(String listname, String mark, String where, String type)
	{
		String temp[] = new String[4];

		temp[0] = listname;

		temp[1] = mark;

		temp[2] = where;

		temp[3] = type;

		Al.add(temp);
	}

	// 精确查询
	public DataSet AccurateSelect(DataSet Ds)
	{

		while (Ds.next())// 总数据
		{
			System.out.println("====" + Al.size());

			for (int i = 0; i < Al.size(); i++)// 条件
			{

				String SeletTemp[] = (String[]) Al.get(i); // 取出条件

				if (SeletTemp[1].equals("=")) // 等于的判断
				{
					System.out.println(SeletTemp[0] + " " + SeletTemp[1] + " "
							+ SeletTemp[2] + " " + SeletTemp[3]);

					if (Ds.getString(SeletTemp[0]).equals(SeletTemp[2]))// 比较数据
					{
						if (SeletTemp[3].equals("or"))
						{
							dataset.AddRow(Ds.getRow());
							count++;// 条件都符合的 放入新的dataset

						}
						else
						{
							if (i == Al.size() - 1)
							{
								dataset.AddRow(Ds.getRow());
								count++;
							}// 条件都符合的 放入新的dataset
						}

					}
					else
					{
						if (!SeletTemp[3].equals("or"))
						{
							break;// 不符合跳出
						}
					}

				}
				else if (SeletTemp[1].equals(">") || SeletTemp[1].equals("<"))// 大于小于判断
				{

					// if(Ds.getString(SeletTemp[0])!=null) //数据库里面的内容不能是空的
					// 不然影响比较
					// {

					try
					{

						pattern = java.util.regex.Pattern.compile("^[0-9]+$");

						matcher = pattern.matcher(SeletTemp[2]);

						if (matcher.find())
						{
							float numeral_A = Integer.parseInt(Ds
									.getString(SeletTemp[0])); // dataset里面的数字

							float numeral_B = Integer.parseInt(SeletTemp[2]); // 比较的数字

							if (SeletTemp[1].equals(">"))
							{
								if (numeral_A > numeral_B)// 比较数据
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}

							}
							if (SeletTemp[1].equals("<"))
							{
								if (numeral_A < numeral_B)// 比较数据
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}
							}
						}

						String str_A = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";

						String str_B = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";

						pattern = java.util.regex.Pattern.compile(str_A);

						pattern_z = java.util.regex.Pattern.compile(str_B);

						matcher = pattern.matcher(SeletTemp[2]);

						matcher_z = pattern_z.matcher(SeletTemp[2]);

						if (matcher.find() || matcher_z.find())
						{

							// SimpleDateFormat sdf = new
							// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

							String TimeTemp = "";

							if (Ds.getString(SeletTemp[0]) == ""
									|| Ds.getString(SeletTemp[0]) == null)
							{
								break;
							}

							if (Ds.getString(SeletTemp[0]).length() < 19) // 判断时间的大小
																			// 如果只精确到日
																			// 就加上00:00:00
							{

								TimeTemp = Ds.getString(SeletTemp[0])
										+ " 00:00:00";
							}
							else
							{
								TimeTemp = Ds.getString(SeletTemp[0]);
							}

							if (SeletTemp[2].length() < 19) // 判断时间的大小
															// 如果只精确到日
															// 就加上00:00:00
							{
								SeletTemp[2] = SeletTemp[2] + " 00:00:00";
							}

							// Date time_A = sdf.parse(TimeTemp);

							// Date time_B = sdf.parse(SeletTemp[2]);

							if (SeletTemp[1].equals("<"))
							{
								if (isDateBefore(TimeTemp, SeletTemp[2]))
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}
							}
							else if (SeletTemp[1].equals(">"))
							{
								if (isDateBefore(SeletTemp[2], TimeTemp))
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}
							}

						}

					}
					catch(Exception e)
					{ // 如果不是数字 那么也许是时间

						e.printStackTrace();

						info = "数据条件错误";

						return null;

					}
					// }

				}
				else
				{
					info = "符号条件错误";

					return null;
				}

			}

		}

		return dataset;// 返回
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// 模糊查询
	public DataSet DimSelect(DataSet Ds)
	{

		pattern = java.util.regex.Pattern.compile("tan");

		matcher = pattern.matcher("tan");

		while (Ds.next())// 总数据
		{

			for (int i = 0; i < Al.size(); i++)// 条件
			{

				String SeletTemp[] = (String[]) Al.get(i); // 取出条件

				if (SeletTemp[1].equals("=")) // 等于的判断
				{

					pattern = java.util.regex.Pattern.compile(SeletTemp[2]); // 把条件赋给正则

					matcher = pattern.matcher(Ds.getString(SeletTemp[0]));// 比较的数据

					if (matcher.find())
					{
						if (i == Al.size() - 1)
						{
							dataset.AddRow(Ds.getRow());
							count++;
						}// 条件都符合的 放入新的dataset
					}
					else
					{
						if (!SeletTemp[3].equals("or"))
						{
							break;// 不符合跳出
						}
					}

				}
				else if (SeletTemp[1].equals(">") || SeletTemp[1].equals("<"))// 大于小于判断
				{

					// if(Ds.getString(SeletTemp[0])!=null) //数据库里面的内容不能是空的
					// 不然影响比较
					// {

					try
					{

						pattern = java.util.regex.Pattern.compile("^[0-9]+$");

						matcher = pattern.matcher(SeletTemp[2]);

						if (matcher.find())
						{
							float numeral_A = Integer.parseInt(Ds
									.getString(SeletTemp[0])); // dataset里面的数字

							float numeral_B = Integer.parseInt(SeletTemp[2]); // 比较的数字

							if (SeletTemp[1].equals(">"))
							{
								if (numeral_A > numeral_B)// 比较数据
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}

							}
							if (SeletTemp[1].equals("<"))
							{
								if (numeral_A < numeral_B)// 比较数据
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}
							}
						}

						String str_A = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";

						String str_B = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";

						pattern = java.util.regex.Pattern.compile(str_A);

						pattern_z = java.util.regex.Pattern.compile(str_B);

						matcher = pattern.matcher(SeletTemp[2]);

						matcher_z = pattern_z.matcher(SeletTemp[2]);

						if (matcher.find() || matcher_z.find())
						{

							String TimeTemp = "";

							if (Ds.getString(SeletTemp[0]) == ""
									|| Ds.getString(SeletTemp[0]) == null)
							{
								break;
							}

							if (Ds.getString(SeletTemp[0]).length() < 19) // 判断时间的大小
																			// 如果只精确到日
																			// 就加上00:00:00
							{

								TimeTemp = Ds.getString(SeletTemp[0])
										+ " 00:00:00";
							}
							else
							{
								TimeTemp = Ds.getString(SeletTemp[0]);
							}

							if (SeletTemp[2].length() < 19) // 判断时间的大小
															// 如果只精确到日
															// 就加上00:00:00
							{
								SeletTemp[2] = SeletTemp[2] + " 00:00:00";
							}

							// Date time_A = sdf.parse(TimeTemp);

							// Date time_B = sdf.parse(SeletTemp[2]);

							if (SeletTemp[1].equals("<"))
							{
								if (isDateBefore(TimeTemp, SeletTemp[2]))
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}
							}
							else if (SeletTemp[1].equals(">"))
							{
								if (isDateBefore(SeletTemp[2], TimeTemp))
								{
									if (i == Al.size() - 1)
									{
										dataset.AddRow(Ds.getRow());
										count++;
									}// 条件都符合的 放入新的dataset
								}
								else
								{
									if (!SeletTemp[3].equals("or"))
									{
										break;// 不符合跳出
									}
								}
							}

						}

					}
					catch(Exception e)
					{ // 如果不是数字 那么也许是时间

						e.printStackTrace();

						info = "数据条件错误";

						return null;

					}
					// }

				}
				else
				{
					info = "查询条件错误";

					return null;
				}

			}
		}

		return dataset;

	}

	public String getInfo()
	{

		return this.info;
	}

	public int getCount()// 查询出多少条数据
	{
		return this.count;
	}

	public static boolean isDateBefore(String dateA, String dateB) // 比较时间

	{
		try
		{

			DateFormat df = DateFormat.getDateTimeInstance();

			return df.parse(dateA).before(df.parse(dateB));

		}
		catch(ParseException e)
		{

			return false;

		}

	}

}