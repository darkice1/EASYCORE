package easy.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import easy.config.Config;

/**
 * <p>
 * <i>Copyright: 9esoft.com (c) 2005-2006 <br>
 * Company: ��������Ƽ���չ���޹�˾ </i>
 * </p>
 * 
 * Licence�ӿ��ļ�
 * 
 * @version 1.0 ( <i>2006-5-6 Neo </i>)
 */

public abstract class Licence
{
	protected int usernumbuer = 0;

	protected EDate startdate;

	protected EDate enddate;

	protected String project;
	
	protected  String ver;

	public final static int TRUE = 0;

	public final static int ERROR_STARTDATE = 1;

	/**
	 * ��ֹ���ڹ���
	 */
	public final static int ERROR_DATEEXPIRED = 2;

	/**
	 * LICENCE����ʧ��
	 */
	public final static int ERROR_LICENCE = 3;

	/**
	 * �û�����
	 */
	public final static int ERROR_USERNUMBER = 4;

	private final static String DATEFORMAE = "yyyy-MM-dd";

	private String licence;

	protected List<String> md5list;// = new ArrayList<String>();

	protected static Licence LIC;
	
	private static boolean isinit = false;

	public Licence()
	{
		md5list = new ArrayList<String>();
		licence = "";
		
		if (isinit == false)
		{
			isinit = true;
			LIC = getLicenceA();
		}
		p_init();
		init();
		licence = intLicence();
	}
	
	private void p_init()
	{
		
		String sd = Config.getProperty("STARTDATE");
		String ed = Config.getProperty("ENDDATE");
		
		project = Config.getProperty("PROJECT");
		
		ver = Config.getProperty("VER","0.1");
		
		usernumbuer = Integer.parseInt(Config.getProperty("USERNUMBER"));
		md5list.add(Integer.toString(usernumbuer));

		if (sd == null)
		{
			startdate = new EDate();
		}
		else
		{
			startdate = new EDate(sd, DATEFORMAE);
		}
		
		if (ed != null)
		{
			enddate = new EDate(ed, DATEFORMAE);
		}
		else
		{
			enddate = new EDate("9999-12-30",DATEFORMAE);
		}
	}

	/**
	 * ��ʼ����Ϣ(���󷽷�)
	 * �����md5list��֤��Ϣ
	 * 
	 * @return �û���
	 */
	protected abstract void init();
	
	/**
	 * ȡ��Licence����(���󷽷�)
	 * 
	 * @return �û���
	 */
	protected abstract Licence getLicenceA();
	
	protected String intLicence()
	{
		StringBuffer buf = new StringBuffer();
		for (String str : md5list)
		{
			buf.append(str);
		}

		return Format.Md5(buf.toString());
	}
	
	public String getLicence()
	{
		return licence;
	}

	/**
	 * ȡ��ϵͳ�����û���(���󷽷�)
	 * 
	 * @return �û���
	 */
	protected abstract int getSystemNumberA();

	/**
	 * ȡ��ϵͳ�����û���(�������÷���)
	 * 
	 * @return �û���
	 */
	public int getSystemNumber()
	{
		return LIC.getSystemNumberA();
	}

	/**
	 * ͨ��config�ļ�����licence
	 * 
	 * @return ������Licence.TRUEΪ�ɹ� ����Ϊʧ����Ϣ
	 */
	public int checklic()
	{
		return checklic(Config.getProperty("LICENCE"));
	}

	/**
	 * �ļ�����licence
	 * 
	 * @return ������Licence.TRUEΪ�ɹ� ����Ϊʧ����Ϣ
	 */
	public int checklic(String lic)
	{
		if (new EDate(EDate.getSQLDate(new Date()), DATEFORMAE).getTime() < LIC.startdate.getTime())
		{
			return ERROR_STARTDATE;
		}

		if (new EDate().getTime() > LIC.enddate.getTime())
		{
			return ERROR_DATEEXPIRED;
		}

		if (licence.equals(lic) == false)
		{
			return ERROR_LICENCE;
		}

		if (LIC.getSystemNumberA() > LIC.usernumbuer)
		{
			return ERROR_USERNUMBER;
		}

		return TRUE;
	}

	/**
	 * ���ش�������
	 * 
	 * @param error
	 * @return ��������
	 */
	public static String getErrorMsg(int error)
	{
		if (error == ERROR_STARTDATE)
		{
			return "��ʼ�������ô���";
		}
		else if (error == ERROR_DATEEXPIRED)
		{
			return "LICENCE����";
		}
		else if (error == ERROR_LICENCE)
		{
			return "LICENCE����ʧ��";
		}
		else if (error == ERROR_USERNUMBER)
		{
			return "�û�����";
		}
		else
		{
			return "δ֪����";
		}
	}
	/**
	 * @return Returns the enddate.
	 */
	public EDate getEnddate()
	{
		return enddate;
	}
	/**
	 * @return Returns the project.
	 */
	public String getProject()
	{
		return project;
	}
	/**
	 * @return Returns the startdate.
	 */
	public EDate getStartdate()
	{
		return startdate;
	}
	/**
	 * @return Returns the usernumbuer.
	 */
	public int getUsernumbuer()
	{
		return usernumbuer;
	}

	public long getDistanceDay()
	{
		return ((LIC.enddate.getTime() - new EDate(EDate.getSQLDate(new Date()), DATEFORMAE).getTime())/86400000);
	}
}