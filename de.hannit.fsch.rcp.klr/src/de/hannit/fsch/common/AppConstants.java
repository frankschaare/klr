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
public static final DecimalFormat KOMMAZAHL = new DecimalFormat("#0,00");

public static interface ActiveSelections
{
public static final String AUSWERTUNGSMONAT = "AUSWERTUNGSZEITRAUM/MONAT";
public static final String MONATSBERICHT = "MONATSBERICHT";
}
}
