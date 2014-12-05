package easy.tag.html;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * <p><i>Copyright: Easy (c) 2005-2005<br>
 * Company: Easy</i></p>
 *
 * EasyHtml class˵��
 *
 * @version 1.0 (<i>2005-8-28 Gawen</i>)
 */

public class SelectTag extends SimpleTagSupport
{
	protected String name = "SELECT"; 
	protected String[] value = null;
	protected String[] text = null;
	protected String selected = null;
	protected boolean disabled = false;
	
	public void doTag() throws JspException, IOException
	{
		JspContext jspContext = getJspContext();
		JspWriter jspWriter = jspContext.getOut();
		jspWriter.println(doSelect());
	}
	
	protected String doSelect()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(String.format("<SELECT NAME='%s'%s>",name,disabled?" DISABLED":""));
		if (value != null)
		{
			for (int i = 0,len = value.length;i < len;i++)
			{
				buffer.append(String.format("<OPTION VALUE='%s'%s>%s</OPTION>",value[i],(selected!=null&&selected.equals(value[i]))?" SELECTED":"",(text!=null&&text.length>i)?text[i]:""));
			}
		}
		buffer.append("</SELECT>");
		return buffer.toString();
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setValue(String[] value)
	{
		this.value = value;
	}
	
	public String[] getValue()
	{
		return this.value;
	}
	
	public void setText(String[] text)
	{
		this.text = text;
	}
	
	public String[] getText()
	{
		return this.text;
	}
	
	public void setSelected(String selected)
	{
		this.selected = selected;
	}
	
	public String getSelected()
	{
		return this.selected;
	}
	
	public void setDisabled(boolean b)
	{
		this.disabled = b;
	}
	
	public boolean getDisabled()
	{
		return this.disabled;
	}
}
