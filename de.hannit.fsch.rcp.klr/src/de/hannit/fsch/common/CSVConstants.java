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
	public static interface Loga 
	{
	public static final String CONTEXT_DATEN = "LOGA";
	
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
	public static final int PERSONALNUMMER_INDEX_TABLE = 1;
	public static final int PERSONALNUMMER_INDEX_CSV = 2;
	
	/**
	 * Brutto
	 */
	public static final int BRUTTO_INDEX_TABLE = 2;
	public static final String BRUTTO_LABEL_TABLE = "Brutto";
	public static final int BRUTTO_INDEX_CSV = 6;
	
	/**
	 * Abrechnungsmonat
	 */
	public static final int ABRECHNUNGSMONAT_INDEX_TABLE = 3;
	public static final String ABRECHNUNGSMONAT_LABEL_TABLE = "Monat";
	public static final int ABRECHNUNGSMONAT_INDEX_CSV = 7;
	public static final String ABRECHNUNGSMONAT_DATUMSFORMAT_CSV = "dd.MM.yyyy";
	
	
	
	/**
	 * Tarifgruppe
	 */
	public static final int TARIFGRUPPE_INDEX_TABLE = 4;
	public static final String TARIFGRUPPE_LABEL_TABLE = "Tarifgruppe";
	public static final int TARIFGRUPPE_INDEX_CSV = 8;
	
	/**
	 * Tarifstufe
	 */
	public static final int TARIFSTUFE_INDEX_TABLE = 5;
	public static final String TARIFSTUFE_LABEL_TABLE = "Tarifstufe";	
	public static final int TARIFSTUFE_INDEX_CSV = 9;
	
	/**
	 * Stellenanteil
	 */
	public static final int STELLENNTEIL_INDEX_TABLE = 6;
	public static final String STELLENNTEIL_LABEL_TABLE = "Stellenanteil";	
	public static final int STELLENNTEIL_INDEX_CSV = 10;
	
	}	
	
	/**
	 * Felder der AZV Daten
	 */
	public static interface ERGEBNIS 
	{
	public static final String ERGEBNIS_IMPORT_DIR = "\\\\regionhannover.de\\daten\\hannit\\Rechnungswesen A�R\\KLR\\KLR ab 01.01.2011\\Auswertungen\\2013Q1\\CSV";
	}
	
	/**
	 * Felder der AZV Daten
	 */
	public static interface AZV 
	{
	public static final String CONTEXT_DATEN = "AZV";
	public static final String CONTEXT_WEBSERVICEIP = "IP";
	public static final String AZV_IMPORT_DIR = "\\\\regionhannover.de\\daten\\DMS_System\\Zusatzkomponenten\\Workflow\\Azv\\Importe";


	/**
	 * Nachname
	 */
	public static final int NACHNAME_INDEX_CSV = 2;
	
	/**
	 * Personalnummer
	 */
	public static final int PERSONALNUMMER_INDEX_TABLE = 1;
	public static final int PERSONALNUMMER_INDEX_CSV = 7;
		
	/**
	 * Team
	 */
	public static final int TEAM_INDEX_TABLE = 2;
	public static final int TEAM_INDEX_CSV = 4;
	
	/**
	 * Abrechnungsjahr
	 */
	public static final int BERICHTSMONAT_INDEX_CSV = 9;
	public static final int BERICHTSMONAT_INDEX_TABLE = 3;
	public static final String BERICHTSMONAT_LABEL_TABLE = "Berichtsmonat";	
	public static final int BERICHTSJAHR_INDEX_CSV = 10;
	public static final String BERICHTSMONAT_DATUMSFORMAT_CSV = "MMMM yyyy";
	
	/**
	 * Kostenstelle / Tr�ger
	 */
	public static final int KOSTENSTELLE_INDEX_CSV = 12;
	public static final int KOSTENSTELLE_INDEX_TABLE = 4;
	public static final String KOSTENSTELLE_LABEL_TABLE = "Kostenstelle";		
	public static final int KOSTENTRAEGER_INDEX_CSV = 13;
	public static final int KOSTENTRAEGER_INDEX_TABLE = 5;
	public static final String KOSTENTRAEGER_LABEL_TABLE = "Kostentr�ger";	
	
	/**
	 * Prozentanteil
	 */
	public static final int PROZENTANTEIL_INDEX_CSV = 14;
	public static final int PROZENTANTEIL_INDEX_TABLE = 6;
	public static final String PROZENTANTEIL_LABEL_TABLE = "%";		
	}	
}
