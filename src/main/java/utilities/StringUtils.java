package utilities;

/**
 * Created by Robertson_Laptop on 6/5/2016.
 */
public class StringUtils {

    public static boolean isEmpty(String var)
    {
        return ((var == null) || (var.length() == 0));
    }

    public static boolean stringToBoolean(String value)
    {
        boolean retValue = false;
        if (!isEmpty(value))
        {
            if (value.equalsIgnoreCase("true"))
            {
                retValue = true;
            }
            else if (value.contains("Y"))
            {
                retValue = true;
            }
        }

        return retValue;
    }
}
