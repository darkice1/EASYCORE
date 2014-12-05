package easy.servlet;

/**
 * <p><i>Copyright: 9esoft.com (c) 2005-2006<br>
 * Company: 九州易软科技发展有限公司</i></p>
 *
 * GeneralUtilities
 *
 * @version 1.0 (<i>2006-5-11 Neo</i>)
 */

import java.io.File;
import java.util.Random;

public class GeneralUtilities
{

    public GeneralUtilities()
    {
    }

    public static String generateFileName(String s, String s1)
    {
        File file = new File(s + s1);
        Random random = new Random(System.currentTimeMillis());
        for(; file.exists(); file = new File(s + s1))
            s1 = "{" + random.nextLong() + "}" + s1;

        return s1;
    }
}
