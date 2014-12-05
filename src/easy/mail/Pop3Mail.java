package easy.mail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import com.sun.mail.pop3.POP3Folder;

import easy.sql.DataSet;
import easy.sql.Row;
import easy.util.EDate;
import easy.util.Log;

public class Pop3Mail
{
	private Session session;
	private Store store;

	public Pop3Mail(String host, String username, String passwd) throws MessagingException
	{
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "pop3");
		props.setProperty("mail.pop3.host", host);
		// props.setProperty("mail.pop3.port", "110");
		session = Session.getDefaultInstance(props);

		try
		{
			store = session.getStore("pop3");
			store.connect(host, 110, username, passwd);
		}
		catch (NoSuchProviderException e)
		{
			Log.OutException(e);
		}
	}
	
	public DataSet getMailList(String boxname) throws MessagingException
	{
		return getMailList(boxname,Integer.MAX_VALUE);
	}

	public DataSet getMailList(String boxname,int max) throws MessagingException
	{
		DataSet ds = new DataSet();

		POP3Folder folder = (POP3Folder)store.getFolder(boxname);
		folder.open(Folder.READ_ONLY);
		
		Message[] messages = folder.getMessages();
		int c = 0;
		//System.out.println("邮件总数: " + messages.length);

		for (int i=messages.length-1; i>=0 ; i--)
		{
			//System.out.println(i);
			MimeMessage msg = (MimeMessage) messages[i];
			// System.out.println(msg.getSubject());
			//System.out.println(EDate.toString(msg.getSentDate()));

			Row r = new Row();

			r.putString("id", folder.getUID(msg));
			r.putString("subject", msg.getSubject());
			r.putString("sentDate", EDate.toString(msg.getSentDate()));
			try
			{
				String content = getContent(msg);
				
				r.putString("content", content);
			}
			catch (IOException e)
			{
				Log.OutException(e);
			}

			ds.AddRow(r);
			
			c++;
			if (c >= max)
			{
				break;
			}
		}

		// 释放资源
		folder.close(true);

		return ds;
	}

	/**
	 * 处理邮件正文的工具方法 A
	 */
	private StringBuffer getContent(Part part, StringBuffer result)
					throws MessagingException, IOException
	{
		if (part.isMimeType("multipart/*"))
		{
			Multipart p = (Multipart) part.getContent();
			int count = p.getCount();

			for (int i = 0; i < count; i++)
			{
				BodyPart bp = p.getBodyPart(i);
				getContent(bp, result);
			}
		}
		else if (part.isMimeType("text/*"))
		{
			result.append(part.getContent());
		}
		else
		{
			result.append(part.getContent());
		}
		
		return result;
	}
	
	private String getContent(Message m) throws MessagingException, IOException{
        StringBuffer sb=new StringBuffer("");
        return getContent(m,sb).toString();
    }

	public void close()
	{
		try
		{
			store.close();
		}
		catch (MessagingException e)
		{
			Log.OutException(e);
		}
	}

	/**
	 * @param args
	 * @throws MessagingException 
	 */
	public static void main(String[] args) throws MessagingException
	{
		Pop3Mail pmail = new Pop3Mail("pop.163.com", "dargon_xuan@163.com","1921988122 ");
		//System.out.println(wy.loginPop3("dargon_xuan@163.com", "1921988122"));

		try
		{
			System.out.println(pmail.getMailList("INBOX",10).getRowList());
			// System.out.println(pmail.getMailList("INBOX").getRowList());
		}
		catch (MessagingException e)
		{
			Log.OutException(e);
		}

	}

}
