/**
 * 
 */
package de.hannit.fsch.klr.kostenrechnung;

/**
 * @author fsch
 *
 */
public class Kostenrechnungsobjekt
{
public static final String KST = "Kostenstelle";
public static final String KTR = "Kostenträger";
	
private String bezeichnung = null;
private double summe = 0;
private String art = null;

	/**
	 * Oberklasse für alle Kostenstellen / Kostenträger
	 */
	public Kostenrechnungsobjekt() {}

	public String getBezeichnung()
	{
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung)
	{
	this.bezeichnung = bezeichnung;
	setArt(bezeichnung);
	}

	public double getSumme()
	{
		return summe;
	}

	public void setSumme(double summe)
	{
		this.summe = summe;
	}

	public String getArt()
	{
		return art;
	}

	public void setArt(String art)
	{
		if (art.length() == 4)
		{
		this.art = KST;	
		}
		else
		{
		this.art = KTR;
		}
	}
	
	

}
