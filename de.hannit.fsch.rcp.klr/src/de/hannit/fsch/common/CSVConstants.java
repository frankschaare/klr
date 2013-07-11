/**
 * 
 */
package de.hannit.fsch.common;

/**
 * @author fsch
 *
 */
public class CSVConstants
{
	/**
	 * Felder der Loga Daten
	 */
	public static interface LogaFields 
	{
	/**
	 * Mandant:
	 * - 33 = Beamte HannIT
	 * - 37 = HannIT
	 */
	public static final int MANDANT = 0;	
	public static final int MANDANT_HANNIT_BEAMTE = 33;
	public static final int MANDANT_HANNIT = 37;
	
	/**
	 * AK:
	 * - 01 = Beamte HannIT
	 * - 02 = HannIT
	 */
	public static final int AK = 1;
	
	/**
	 * Personalnummer
	 */
	public static final int PERSONALNUMMER = 2;
	
	/**
	 * Brutto
	 */
	public static final int BRUTTO = 5;
	
	/**
	 * Abrechnungsmonat
	 */
	public static final int ABRECHNUNGSMONAT = 6;
	
	/**
	 * Tarifgruppe
	 */
	public static final int TARIFGRUPPE = 7;
	
	/**
	 * Tarifstufe
	 */
	public static final int TARIFSTUFE = 8;
	
	/**
	 * Stellenanteil
	 */
	public static final int STELLENNTEIL = 9;
	
	}	
	
	/**
	 * Felder der AZV Daten
	 */
	public static interface AZVFields {}	
}
