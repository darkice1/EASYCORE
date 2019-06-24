package easy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Whois
{
	public static String getInfo(String query) throws IOException
	{
		return getInfo(query, "whois.markmonitor.com");
	}

	public static String getInfo(String query, String server) throws IOException
	{
		final int bufsize = 512;
		Socket sock = new Socket(server, 43);

		OutputStream os = sock.getOutputStream();
		//		BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));  

		//InputStream is = sock.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));  
		
		query += "\r\n";
		os.write(query.getBytes("iso8859_1"));
		StringBuffer buf = new StringBuffer();
		if (in != null)
		{
			int byteread = 0;
			char charbuf[] = new char[bufsize];

			while ((byteread = in.read(charbuf)) != -1)
			{
				buf.append(charbuf, 0, byteread);
				charbuf = new char[bufsize];
			}
			in.close();
		}
		sock.close();
		return buf.toString();
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println(getInfo("baidu.com"));
	}
}