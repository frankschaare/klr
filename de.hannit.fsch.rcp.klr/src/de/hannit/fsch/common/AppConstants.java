/**
 * 
 */
package de.hannit.fsch.common;

import java.text.DecimalFormat;

/**
 * @author fsch
 *
 */
public class AppConstants 
{
public static final String LOGGER = "LOGGER";
public static final String LOG_STACK = "LOGSTACK";
public static final String CONTEXT_TARIFGRUPPEN = "cTARIFGRUPPEN";
public static final String CONTEXT_PERSONALDURCHSCHNITTSKOSTEN = "cPDK";
public static final String CONTEXT_GEMEINKOSTEN = "cGK";
public static final String CONTEXT_ERGEBNIS = "cERGEBNIS";
public static final String CONTEXT_SELECTED_MITARBEITER = "selectedMitarbeiter";
public static final DecimalFormat KOMMAZAHL = new DecimalFormat("#0,00");

public static final String ENDKOSTENSTELLE_TEAM1 = "1110";
public static final String ENDKOSTENSTELLE_TEAM2 = "2010";
public static final String ENDKOSTENSTELLE_TEAM3 = "3010";
public static final String ENDKOSTENSTELLE_TEAM4 = "4010";

public static interface ActiveSelections
{
public static final String AUSWERTUNGSMONAT = "AUSWERTUNGSZEITRAUM/MONAT";
public static final String MONATSBERICHT = "MONATSBERICHT";
public static final String PART_GEMEINKOSTEN = "de.hannit.fsch.rcp.klr.partdescriptor.csv.gemeinkosten";
}
}
