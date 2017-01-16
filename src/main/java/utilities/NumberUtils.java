package utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class NumberUtils {


	public static double roundHoursTwoDecimals(double hours)
	{
		DecimalFormat twoDecimals = new DecimalFormat("###.##");
		return Double.valueOf(twoDecimals.format(hours));
	}

	public static double roundHoursFourDecimals(double hours)
	{
		DecimalFormat twoDecimals = new DecimalFormat("###.####");
		return Double.valueOf(twoDecimals.format(hours));
	}

	public static double roundDollars (double dollars)
	{
		DecimalFormat twoDecimals = new DecimalFormat("#####.00");
		twoDecimals.setMinimumFractionDigits(2);		
		return Double.valueOf(twoDecimals.format(dollars));
	}
	
	public static String formatAsMoney (double dollars)
	{
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(NumberUtils.roundDollars(dollars));
	}
	
	public static String formatAsPercent( double amount)
	{
		NumberFormat formatter = NumberFormat.getPercentInstance();
		return formatter.format(amount);
	}	
	
	public static double convertToDouble(String value)
	{
		double retValue = 0.0;
		if (!StringUtils.isEmpty(value))
		{
			retValue = Double.parseDouble(value);
		}
		
		return retValue;
	}
	
}
