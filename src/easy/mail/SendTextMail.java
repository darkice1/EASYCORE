package easy.mail;

import easy.net.Proxy;
import easy.util.Log;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * <i>Copyright: Esay (c) 2005-2005 <br>
 * Company: Esay </i>
 * </p>
 * 
 * 发送文本邮件类
 * 1.1加入邮件群发功能
 * 
 * @version 1.1 ( <i>2006-1-6 neo </i>)
 * @version 1.0 ( <i>2005-7-4 neo </i>)
 */

public class SendTextMail
{
	protected final List <String> list = new ArrayList<String>();
	
	protected String from;

	protected String to;

	protected String cc;

	protected String subject;

	protected String content;

	protected Properties props = new Properties();

	protected Session sendMailSession;

	protected MimeMessage newMessage;

	protected final static String SMTPHOST = "mail.smtp.host";

	protected final static String AUTH = "mail.smtp.auth";

	protected final static String TRUE = "true";

	protected final static String SMTP = "smtp";

	public SendTextMail()
	{
		this(null,null,null);
	}
	
	public SendTextMail(String host)
	{
		this(host,null,null);
	}
	
	public SendTextMail(String host, String user, String password)
	{
		Proxy.initCfgProxy();
		if (host!=null)
		{
			props.put(SMTPHOST, host);
		}

		if (user != null && user.equals("") == false && password != null && password.equals("") == false)
		{
			//验证
			props.put(AUTH, TRUE);
			MailAuthenticator authenticator = new MailAuthenticator(user,password);
			try
			{
				sendMailSession = Session.getInstance(props,authenticator);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			//无验证
			sendMailSession = Session.getDefaultInstance(props,null);
		}
		newMessage = new MimeMessage(sendMailSession);
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public void setCc(String cc)
	{
		this.cc = cc;
	}
	
	private InternetAddress[] toInternetAddresss(String mails)
	{
		InternetAddress[] itos = null;
		if (mails != null)
		{
			String[] tos = to.split(";");
			itos = new InternetAddress[tos.length];
			for (int i=0,len=tos.length;i<len;i++)
			{
				try
				{
					itos[i] = new InternetAddress(tos[i]);
				}
				catch (AddressException e)
				{
					Log.OutException(e);
				}
			}			
		}
		
		return itos;
	}

	public boolean send()
	{
		try
		{
			newMessage.setFrom(new InternetAddress(from));
			newMessage.addRecipients(Message.RecipientType.TO,toInternetAddresss(to));
			
			if (cc != null)
			{
				newMessage.addRecipients(Message.RecipientType.CC,toInternetAddresss(cc));
			}

			newMessage.setSubject(subject,"utf-8");
			//newMessage.setSentDate(new Date());
			newMessage.setText(content);

			Transport transport=null;
			if (props.get(SMTPHOST)!=null)
			{
				transport = sendMailSession.getTransport(SMTP);
			}
			Transport.send(newMessage);
			if (transport!=null)
			{
				transport.close();
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

	}
	
	/**
	 * 将发送地址加入列队
	 */
	public void add(String to)
	{
		list.add(to);
	}
	
	/**
	 * 根据续列发送邮件
	 */
	public void batchsend()
	{
		String tmp = to;
		for (String pto : list)
		{
			to = pto;
			send();
		}
		to = tmp;
	}
	
	/**
	 * 清除发送列表
	 */
	public void clear()
	{
		list.clear();
	}
}
