/**
 * 
 */
package de.hannit.fsch.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author fsch
 *
 */
public class AuswertungsMonat extends GregorianCalendar
{
private int iActualMonth;
private int iSelectedMonth;
private SimpleDateFormat dateFormat = new SimpleDateFormat();


	/**
	 * 
	 */
	public AuswertungsMonat()
	{
	super.setTimeZone( TimeZone.getTimeZone("CET"));
	super.setTime(new Date());
	setiActualMonth(get(Calendar.MONTH));
	setiSelectedMonth(get(Calendar.MONTH));
	}
	
	public int getiActualMonth()
	{
	return iActualMonth;
	}

	public void setiActualMonth(int iActualMonth)
	{
	this.iActualMonth = iActualMonth;
	}

	public int getiSelectedMonth()
	{
	return iSelectedMonth;
	}

	public void setiSelectedMonth(int iSelectedMonth)
	{
	this.iSelectedMonth = iSelectedMonth;
	}

	public String getActualMonth()
	{
	dateFormat.applyPattern("MMMM");	
	return dateFormat.format(getTime());
	}

	public String getActualYear()
	{
	dateFormat.applyPattern("yyyy");	
	return dateFormat.format(getTime());
	}	

	public boolean lastMonth()
	{
	return ((iActualMonth == iSelectedMonth) ? false : true);
	}		

	/**
	 * @param zone
	 */
	public AuswertungsMonat(TimeZone zone)
	{
	super(zone);
	}

	/**
	 * @param aLocale
	 */
	public AuswertungsMonat(Locale aLocale)
	{
	super(aLocale);
	}

	/**
	 * @param zone
	 * @param aLocale
	 */
	public AuswertungsMonat(TimeZone zone, Locale aLocale)
	{
	super(zone, aLocale);
	}

	/**
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 */
	public AuswertungsMonat(int year, int month, int dayOfMonth)
	{
	super(year, month, dayOfMonth);
	}

	/**
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 */
	public AuswertungsMonat(int year, int month, int dayOfMonth, int hourOfDay,
			int minute)
	{
	super(year, month, dayOfMonth, hourOfDay, minute);
	}

	/**
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param hourOfDay
	 * @param minute
	 * @param second
	 */
	public AuswertungsMonat(int year, int month, int dayOfMonth, int hourOfDay,
			int minute, int second)
	{
	super(year, month, dayOfMonth, hourOfDay, minute, second);
	}

}
