package easy.servlet;

import easy.config.Config;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 * TODO PageInfo class说明
 *
 * @version 1.0 (<i>2006-7-24 Gawen</i>)
 */

public class PageInfo
{
	protected int recordCount;
	protected int pageNumber;
	protected int pageSize;
	protected int totalPage;
	protected int startIndex;
	
	public PageInfo(){}
	
	public PageInfo(int recordCount,int pageSize,int pageNumber)
	{
		this.recordCount = recordCount;
		try
		{
			this.pageSize = pageSize < 1?Integer.parseInt(Config.getProperty("DBDEFPAGESIZE","20")):pageSize;
		}
		catch (NumberFormatException nfe)
		{
			this.pageSize = 20;
		}
		this.pageNumber	= pageNumber;
		this.totalPage	= (recordCount + pageSize - 1) / pageSize;
	}
	
	/**
	 * @return Returns the pageNumber.
	 */
	public int getPageNumber()
	{
		return pageNumber;
	}
	/**
	 * @param pageNumber The pageNumber to set.
	 */
	public void setPageNumber(int pageNumber)
	{
		this.pageNumber = pageNumber;
	}
	/**
	 * @return Returns the pageSize.
	 */
	public int getPageSize()
	{
		return pageSize;
	}
	/**
	 * @param pageSize The pageSize to set.
	 */
	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}
	/**
	 * @return Returns the recordCount.
	 */
	public int getRecordCount()
	{
		return recordCount;
	}
	/**
	 * @param recordCount The recordCount to set.
	 */
	public void setRecordCount(int recordCount)
	{
		this.recordCount = recordCount;
	}
	/**
	 * @return Returns the totalPage.
	 */
	public int getTotalPage()
	{
		return totalPage;
	}
	/**
	 * @param totalPage The totalPage to set.
	 */
	public void setTotalPage(int totalPage)
	{
		this.totalPage = totalPage;
	}

	/**
	 * @return Returns the startIndex.
	 */
	public int getStartIndex()
	{
		return startIndex;
	}
	/**
	 * @param startIndex The startIndex to set.
	 */
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}
}