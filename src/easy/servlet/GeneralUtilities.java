package easy.servlet;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

public class GeneralUtilities
{

    public GeneralUtilities()
    {
    }

    public static String generateFileName(String s, String s1)
    {
        File file = new File(s + s1);
        for(; file.exists(); file = new File(s + s1))
            s1 = "{" + ThreadLocalRandom.current().nextLong() + "}" + s1;

        return s1;
    }
}
