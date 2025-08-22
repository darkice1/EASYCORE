package easy.sql

import easy.config.Config.getProperty
import easy.util.EDate
import easy.util.Log
import java.io.Serializable
import java.sql.ResultSet
import java.sql.Types
import java.util.*

/**
 *
 * *Copyright: Easy (c) 2005-2005<br></br>
 * Company: Easy*
 *
 * ��ݿ��ￄ1�7
 *
 * @version 1.0 (*2005-7-7 neo*)
 */
@Suppress("unused")
class DataSet : Serializable {
	@Transient
	private var cursor = -1

	var rowList = ArrayList<Row>()

	constructor()

	//	@Override
	//	protected void finalize() throws Throwable
	//	{
	//		super.finalize();
	//		//rowList.clear();
	//		rowList = null;
	//	}
	constructor(rs: ResultSet) {
		val rsmd = rs.metaData

		rs.last()
		val count = rs.row
		rs.first()
		rs.beforeFirst()

		rowList = ArrayList<Row>(count)

		//		list.ensureCapacity();
//		System.out.println("count "+ count);
		val cols = rsmd.columnCount
		val colsName = arrayOfNulls<String>(cols + 1)
		for (i in 1..cols) {
			colsName[i] = rsmd.getColumnLabel(i)
		}
		//
		while (rs.next()) {
			val row = Row()
			for (i in 1..cols) {
				try {
					var rowstr: String? = null
					//Log.OutLog(String.format("%s %d %d %d",rsmd.getColumnName(i) ,rsmd.getColumnType(i),Types.CLOB,Types.TIMESTAMP));
					val type = rsmd.getColumnType(i)
					when (type) {
						-4 -> {
							//-4Ϊmy sql��blob
					//						Blob blob = rs.getBlob(i);
					//						if (blob != null)
					//						{
					//							if (DBENCODEING == null  || DBENCODEING.equals(""))
					//							{
					//								rowstr = new String(blob.getBytes(1l,(int)blob.length()));
					//							}
					//							else
					//							{
					//								rowstr = new String(blob.getBytes(1l,(int)blob.length()),DBENCODEING);
					//							}
					//						}
					//						else
					//						{
					//							rowstr = null;
					//						}
							row.put(Col(colsName[i], rs.getBytes(i)))
						}
						Types.DATE -> {
							rowstr = try {
								rs.getDate(i).toString()
							} catch (_: Exception) {
								""
							}
						}
						Types.TIMESTAMP -> {
							try {
								val ts = rs.getTimestamp(i)
								if(ts != null && ts.getTime() != 0L) {
					//								rowstr = rs.getString(i);
									val d = EDate()
									d.setTime(rs.getTimestamp(i).getTime())
									rowstr = d.toString()

									//								System.out.println("DATE   " +    rowstr);
								} else {
									rowstr = ""
								}
							} catch (e: Exception) {
								Log.OutException(e)
								rowstr = rs.getString(i)
							}
						}
						Types.CLOB -> {
							val clob = rs.getClob(i)
							rowstr = if(clob != null) {
								clob.getSubString(1L, clob.length().toInt())
							} else {
								""
							}
						}
						Types.BLOB -> {
							row.put(Col(colsName[i], rs.getBytes(i)))
						}
						Types.INTEGER, Types.SMALLINT, Types.TINYINT -> {
							row.put(Col(colsName[i], rs.getInt(i)))
						}
						Types.FLOAT -> {
							row.put(Col(colsName[i], rs.getFloat(i)))
						}
						Types.DOUBLE -> {
							row.put(Col(colsName[i], rs.getDouble(i)))
						}
						else -> {
							val buf = rs.getBytes(i)
							rowstr = if(buf != null) {
								if(DBENSTRINGCODEING == null || DBENSTRINGCODEING == "") {
									rs.getString(i)
								} else {
									String(rs.getBytes(i), charset(DBENSTRINGCODEING))
								}
							} else {
								""
							}
						}
					}

					if(rowstr != null) {
						row.put(colsName[i], Col(colsName[i], rowstr))
					}
				} catch (e: Exception) {
					row.put(colsName[i], Col(colsName[i], ""))
					Log.OutException(e)
				}
			}
			rowList.add(row)
		}
	}

	/**
	 * ���ض�Ӧ��
	 * @param idx ������
	 * @return
	 */
	fun getRow(idx: Int): Row {
		return rowList[idx]
	}

	val row: Row
		/**
		 * ���ص�ǰ��
		 * @return
		 */
		get() = getRow(cursor)

	fun getInt(col: String?): Int {
		return rowList[cursor].getInt(col)
	}

	fun getLong(col: String?): Long {
		return rowList[cursor].getLong(col)
	}

	fun getFloat(col: String?): Float {
		return rowList[cursor].getFloat(col)
	}

	fun getDouble(col: String?): Double {
		return rowList[cursor].getDouble(col)
	}

	fun getBytes(col: String?): ByteArray {
		return rowList[cursor].getBytes(col)
	}

	fun getString(col: String?): String {
		return rowList[cursor].getString(col)
	}

	fun next(): Boolean {
		return (++cursor) < rowList.size
	}

	fun previous() {
		if(cursor > -1) {
			cursor--
		}
	}

	fun beforeFirst() {
		cursor = -1
	}

	/**
	 * �滻ָ���ֶ�����
	 * @param colname
	 * @param regex
	 */
	fun replaceAll(colname: String?, regex: String, replacement: String) {
		for (r in rowList) {
			r.put(colname, Col(colname, r.getString(colname).replace(regex.toRegex(), replacement)))
		}
	}

	fun put(colname: String?, value: Col?) {
		put(cursor, colname, value)
	}


	fun put(idx: Int, colname: String?, value: Col?) {
		val r = rowList[idx]
		r.put(colname, value)
	}

	fun putString(colname: String?, value: String?) {
		put(colname, Col(colname, value))
	}

	fun putString(idx: Int, colname: String?, value: String?) {
		put(idx, colname, Col(colname, value))
	}

	fun putDouble(colname: String?, value: Double) {
		put(colname, Col(colname, value))
	}

	fun putDouble(idx: Int, colname: String?, value: Double) {
		put(idx, colname, Col(colname, value))
	}

	fun putInteger(colname: String?, value: Int) {
		put(colname, Col(colname, value))
	}

	fun putInteger(idx: Int, colname: String?, value: Int) {
		put(idx, colname, Col(colname, value))
	}

	var count: Int
		/**
		 * @return Returns the count.
		 */
		get() = rowList.size
		/**
		 * @param count The count to set.
		 */
		set(count) {
			this.cursor = count
		}


	@Suppress("FunctionName")
	fun AddRow(row: Row?) {
		rowList.add(row!!)
	}

	fun moveCursor(position: Int) {
		cursor = if(position < 0) {
			0
		} else if(position >= rowList.size) {
			rowList.size - 1
		} else {
			position
		}
	}


	fun sort(fieldname: String?, type: Int) {
		sort(fieldname)
	}

	fun sort(fieldname: String?) {
		setSortFiled(fieldname)
		rowList.sort()
	}

	fun sort(fieldnames: Array<String?>, fieldTypes: Array<String?>, isDESC: Array<Boolean?>) {
		//setSortFiled(fieldnames[0]);
		//Collections.sort(rowList);

		var preField: String?
		var currField: String?
		currField = fieldnames[0]
		var flag = true
		var fieldType = fieldTypes[0]
		if(fieldType != null) {
			fieldType = fieldType.lowercase(Locale.getDefault())
		}
		while (flag) {
			flag = false
			var j = 1
			val len = rowList.size
			while (j < len) {
				if("int" == fieldType || "long" == fieldType) {
					if(rowList[j - 1].getLong(currField) == rowList[j].getLong(currField)) {
						j++
						continue
					}
					if((rowList[j - 1].getLong(currField) < rowList[j].getLong(currField)) == isDESC[0]) {
						Collections.swap(rowList, j - 1, j)
						flag = true
					}
				} else if("float" == fieldType || "double" == fieldType) {
					if(rowList[j - 1].getDouble(currField) == rowList[j].getDouble(currField)) {
						j++
						continue
					}
					if((rowList[j - 1].getDouble(currField) < rowList[j].getDouble(currField)) == isDESC[0]) {
						Collections.swap(rowList, j - 1, j)
						flag = true
					}
				} else {
					if(rowList[j - 1].getString(currField).compareTo(rowList[j].getString(currField)) == 0) {
						j++
						continue
					}
					if((rowList[j - 1].getString(currField) < rowList[j].getString(currField)) == isDESC[0]
					) {
						Collections.swap(rowList, j - 1, j)
						flag = true
					}
				}
				j++
			}
		}/**/// */
		var i = 1
		val len = fieldnames.size
		while (i < len) {
			fieldType = fieldTypes[i]
			if(fieldType != null) {
				fieldType = fieldType.lowercase(Locale.getDefault())
			}

			preField = fieldnames[i - 1]
			currField = fieldnames[i]
			flag = true
			while (flag) {
				flag = false
				var j = 1
				val jlen = rowList.size
				while (j < jlen) {
					if(rowList[j - 1].getString(preField) == rowList[j].getString(preField)) {
						if("int" == fieldType || "long" == fieldType) {
							if(rowList[j - 1].getLong(currField) == rowList[j].getLong(currField)) {
								j++
								continue
							}
							if((rowList[j - 1].getLong(currField) < rowList[j]
									.getLong(currField)) == isDESC[i]
							) {
								Collections.swap(rowList, j - 1, j)
								flag = true
							}
						} else if("float" == fieldType || "double" == fieldType) {
							if(rowList[j - 1].getDouble(currField) == (rowList[j].getDouble(currField))) {
								j++
								continue
							}
							if((rowList[j - 1].getDouble(currField) < rowList[j]
									.getDouble(currField)) == isDESC[i]
							) {
								Collections.swap(rowList, j - 1, j)
								flag = true
							}
						} else {
							if(rowList[j - 1].getString(currField)
									.compareTo(rowList[j].getString(currField)) == 0
							) {
								j++
								continue
							}
							if((rowList[j - 1].getString(currField) < rowList[j].getString(currField)) == isDESC[i]
							) {
								Collections.swap(rowList, j - 1, j)
								flag = true
							}
						}
					}
					j++
				}
			}
			i++
		}
	}

	fun reverse(fieldname: String?, type: Int) {
		reverse(fieldname)
	}


	fun reverse(fieldname: String?) {
		sort(fieldname)
		rowList.reverse()
	}

	/**
	 * ���������ք1�7
	 * @param sortfield
	 */
	fun setSortFiled(sortfield: String?) {
		for (r in rowList) {
			r.setSortfield(sortfield)
		}
	}

	fun removeField(field: String?) {
		for (r in rowList) {
			r.remove(field)
		}
	}

	fun getDataSet(start: Int, count: Int): DataSet {
		val ds = DataSet()

		var len = rowList.size - start
		len = if(len >= count) {
			start + count
		} else {
			rowList.size
		}

		//System.out.println(start+" "+len);
		for (i in start..<len) {
			ds.AddRow(rowList[i])
		}

		return ds
	}

	/**
	 * @return Returns the cursor.
	 */
	fun getCursor(): Int {
		return cursor
	}

	/**
	 * @param cursor The cursor to set.
	 */
	fun setCursor(cursor: Int) {
		moveCursor(cursor)
	}

	/**
	 * 返回csv格式tab为分隔符
	 * @param fields csv字段顺序以,分割
	 * @return
	 */
	fun toCvsString(fields: String): String {
		return toCsvString(fields, "\t")
	}

	/**
	 * 返回csv格式
	 * @param fields csv字段顺序以,分割
	 * @param split
	 * @return
	 */
	fun toCsvString(fields: String, split: String?): String {
		val buf = StringBuilder()
		val f: Array<String?> = fields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		var i = 0
		val len = f.size
		while (i < len) {
			f[i] = f[i]!!.trim { it <= ' ' }
			i++
		}

		for (r in rowList) {
			val len = f.size
			val last = len - 1
			for (i in 0..<len) {
				val fieldname = f[i]
				buf.append(r.getString(fieldname))
				if(i < last) {
					buf.append(split)
				}
			}
			buf.append("\n")
		}

		return buf.toString()
	}

	companion object {
		/**
		 *
		 */
		private const val serialVersionUID = 1L
		private val DBENCODEING: String? = getProperty("DBENCODEING")
		private val DBENSTRINGCODEING: String? = getProperty("DBENSTRINGCODEING")


		/**
		 *
		 * @param srclist 源数据
		 * @param list join数据
		 * @param srckeys 源关联key, 分割
		 * @param keys 源关联key, 分割
		 */
		@JvmOverloads
		fun join(
			srclist: MutableList<Row>, list: MutableList<Row>, srckeys: String, keys: String, addfields: String? = null
		        ) {
			val sks: Array<String?> = srckeys.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			val ks: Array<String?> = keys.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			var fs: Array<String?>? = null
			if(addfields != null) {
				fs = addfields.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			}

			var isok: Boolean
			for (sr in srclist) {
				for (r in list) {
					isok = true

					var i = 0
					val len = sks.size
					while (i < len) {
						if(sr.getString(sks[i]) != r.getString(ks[i])) {
							isok = false
							break
						}
						i++
					}

					if(isok) {
						if(fs != null) {
							for (f in fs) {
								sr.putString(f, r.getString(f))
							}
						} else {
							val tfs = r.colsNameList
							for (f in tfs) {
								sr.putString(f, r.getString(f))
							}
						}

						break
					}
				}
			}
		}
	}
}
