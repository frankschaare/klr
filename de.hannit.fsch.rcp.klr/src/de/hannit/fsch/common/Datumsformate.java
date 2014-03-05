package de.hannit.fsch.common;

import java.text.SimpleDateFormat;

public class Datumsformate
{
public static final SimpleDateFormat MONATLANG_JAHR = new SimpleDateFormat("MMMM yyyy");	
public static final SimpleDateFormat STANDARDFORMAT = new SimpleDateFormat( "dd.MM.yy" );
public static final SimpleDateFormat STANDARDFORMAT_JAHR_VIERSTELLIG = new SimpleDateFormat( "dd.MM.yyyy" );
public static final SimpleDateFormat STANDARDFORMAT_SQLSERVER = new SimpleDateFormat( "yyyy-MM-dd" );
}
