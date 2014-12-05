/**
 * 
 */
package easy.robot.chat;

import java.io.IOException;
import java.net.ConnectException;

/**
 * @author Neo(starneo@gmail.com)2014-7-5
 *
 */
public abstract class ChatRobot
{
	abstract public String chat(String msg)  throws ConnectException, IOException;
}
