/**
 * 
 */
package de.hannit.fsch.klr.kostenrechnung;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * KLR Ergebnis für einen bestimmten Berichtszeitraum
 * @author fsch
 * @since 28.02.2014
 *
 */
public class Ergebnis implements ITableLabelProvider
{
public static final String COLUMN0_SERVICE = "Service";
public static final String COLUMN1_ERLÖSE = "Direkte Erlöse";
public static final String COLUMN2_MATERIALAUFWAND = "Materialaufwand";
public static final String COLUMN3_AFA = "Abschreibungen";
public static final String COLUMN4_SBA = "Sonstige betriebliche Aufwendungen";
public static final String COLUMN5_PERSONALKOSTEN = "Personalkosten";
public static final String COLUMN6_SUMME_EINZELKOSTEN = "Summe Einzelkosten";
public static final String COLUMN7_DECKUNGSBEITRAG1 = "Deckungsbeitrag 1";
public static final String COLUMN8_VERTEILUNG_KST1110 = "Verteilung KSt. 1110";
public static final String COLUMN9_VERTEILUNG_KST2010 = "Verteilung KSt. 2010";
public static final String COLUMN10_VERTEILUNG_KST2020 = "Verteilung KSt. 2020";
public static final String COLUMN11_VERTEILUNG_KST3010 = "Verteilung KSt. 3010";
public static final String COLUMN12_VERTEILUNG_KST4010 = "Verteilung KSt. 4010";
public static final String COLUMN13_VERTEILUNG_KSTGESAMT = "Gesamt Verteilung KST";
public static final String COLUMN14_ERGBNIS = "Ergebnis";

public static final int INDEX0_SERVICE = 0;
public static final int INDEX1_ERLÖSE = 1;
public static final int INDEX2_MATERIALAUFWAND = 2;
public static final int INDEX3_AFA = 3;
public static final int INDEX4_SBA = 4;
public static final int INDEX5_PERSONALKOSTEN = 5;
public static final int INDEX6_SUMME_EINZELKOSTEN = 6;
public static final int INDEX7_DECKUNGSBEITRAG1 = 7;
public static final int INDEX8_VERTEILUNG_KST1110 = 8;
public static final int INDEX9_VERTEILUNG_KST2010 = 9;
public static final int INDEX10_VERTEILUNG_KST2020 = 10;
public static final int INDEX11_VERTEILUNG_KST3010 = 11;
public static final int INDEX12_VERTEILUNG_KST4010 = 12;
public static final int INDEX13_VERTEILUNG_KSTGESAMT = 13;
public static final int INDEX14_ERGBNIS = 14;

public static final String CSV_ZEILE1_DATUMSFILTER = "Datumsfilter";
public static final String CSV_ZEILE2_WÄHRUNG = "Währung";
public static final String CSV_ZEILE4_ERLÖSE = "Direkte Erlöse des Services";
public static final String CSV_ZEILE5_MATERIALAUFWAND = "Materialaufwand";
public static final String CSV_ZEILE6_AFA = "Abschreibungen auf Anlagen";
public static final String CSV_ZEILE7_SBA = "Sonstige betriebliche Aufwendungen";
public static final String CSV_ZEILE8_PERSONALKOSTEN = "Personalkosten - nach AZV auf Kostenträger";
public static final String CSV_ZEILE9_EINZELKOSTEN = "Summe Einzelkosten";
public static final String CSV_ZEILE10_DECKUNGSBEITRAG1 = "Deckungsbeitrag 1 (direkte Erlöse - direkte Kosten)";
public static final String CSV_ZEILE11_KST1110 = "Verteilung KSt. 1110 - Web-Applikation";
public static final String CSV_ZEILE12_KST2010 = "Verteilung KSt. 2010 - Allgemein";
public static final String CSV_ZEILE13_KST2020 = "Verteilung KSt. 2020 - Einkauf";
public static final String CSV_ZEILE14_KST3010 = "Verteilung KSt. 3010 - Allgemein";
public static final String CSV_ZEILE15_KST4010 = "Verteilung KSt. 4010 - Allgemein";
public static final String CSV_ZEILE16_KSTGESAMT = "Gesamt Verteilung von KST";
public static final String CSV_ZEILE17_ERGBNIS = "Ergebnis des Kostenträgers";

private Date berichtszeitraumVon = null;
private Date berichtszeitraumBis = null;
private TreeMap<Integer, Kostenrechnungsobjekt> services = null;
/**
 * Woher stammen die Daten ?
 * Datenquelle kann eine CSV-Datei oder die Datenbank sein
 */
private String datenQuelle = null;
private String dateiName = null;
/**
 * Für welches Team ist das Ergebnis ? 
 */
private int teamNR = 0;

public Ergebnis(){}

public int getTeamNR(){return teamNR;}
public void setTeamNR(int teamNR){this.teamNR = teamNR;}

public String getDateiName(){return dateiName;}
public void setDateiName(String name){this.dateiName = name;}

public String getDatenQuelle(){return datenQuelle;}
public void setDatenQuelle(String datenQuelle){this.datenQuelle = datenQuelle;}

public TreeMap<Integer, Kostenrechnungsobjekt> getServices(){return services;}
/*
 * Liefert nur die Services, auf denen gebucht wurde
 */
public ArrayList<Kostenrechnungsobjekt> getActiveServices()
{
ArrayList<Kostenrechnungsobjekt> activeServices = new ArrayList<Kostenrechnungsobjekt>();	
	for (Kostenrechnungsobjekt service : services.values())
	{
		if (service.isAktiv())
		{
		activeServices.add(service);	
		}
	}
return activeServices;
}
public void setServices(TreeMap<Integer, Kostenrechnungsobjekt> services){this.services = services;}
public Date getBerichtszeitraumVon(){return berichtszeitraumVon;}
public java.sql.Date getBerichtsMonat()
{
java.sql.Date sqlDatum = new java.sql.Date(this.berichtszeitraumVon.getTime());
return sqlDatum;
}
public void setBerichtszeitraumVon(Date berichtszeitraumVon){this.berichtszeitraumVon = berichtszeitraumVon;}
public Date getBerichtszeitraumBis(){return berichtszeitraumBis;}
public void setBerichtszeitraumBis(Date berichtszeitraumBis){this.berichtszeitraumBis = berichtszeitraumBis;}


@Override
public void addListener(ILabelProviderListener listener)
{
	// TODO Auto-generated method stub
	
}


@Override
public void dispose()
{
	// TODO Auto-generated method stub
	
}


@Override
public boolean isLabelProperty(Object element, String property)
{
	// TODO Auto-generated method stub
	return false;
}


@Override
public void removeListener(ILabelProviderListener listener)
{
	// TODO Auto-generated method stub
	
}


@Override
public Image getColumnImage(Object element, int columnIndex)
{
	// TODO Auto-generated method stub
	return null;
}


@Override
public String getColumnText(Object element, int columnIndex)
{
String result = "";
Kostenrechnungsobjekt service = null;

	if (element instanceof Kostenrechnungsobjekt)
	{
	service = (Kostenrechnungsobjekt) element;
		switch (columnIndex)
		{
		case Ergebnis.INDEX0_SERVICE:
		result = service.getKostenart() + ": " + service.getBezeichnung();	
		break;
		case Ergebnis.INDEX1_ERLÖSE:
		result = (service.getErtrag() != 0) ? NumberFormat.getCurrencyInstance().format(service.getErtrag()) : "";	
		break;
		case Ergebnis.INDEX2_MATERIALAUFWAND:
		result = (service.getMaterialAufwand() != 0) ? NumberFormat.getCurrencyInstance().format(service.getMaterialAufwand()) : "";	
		break;			
		case Ergebnis.INDEX3_AFA:
		result = (service.getAfa() != 0) ? NumberFormat.getCurrencyInstance().format(service.getAfa()) : "";
		break;			
		case Ergebnis.INDEX4_SBA:
		result = (service.getSonstigeBetrieblicheAufwendungen() != 0) ? NumberFormat.getCurrencyInstance().format(service.getSonstigeBetrieblicheAufwendungen()) : "";	
		break;			
		case Ergebnis.INDEX5_PERSONALKOSTEN:
		result = (service.getPersonalKosten() != 0) ? NumberFormat.getCurrencyInstance().format(service.getPersonalKosten()) : "";	
		break;			
		case Ergebnis.INDEX6_SUMME_EINZELKOSTEN:
		result = (service.getSummeEinzelkosten() != 0) ? NumberFormat.getCurrencyInstance().format(service.getSummeEinzelkosten()) : "";	
		break;			
		case Ergebnis.INDEX7_DECKUNGSBEITRAG1:
		result = (service.getDeckungsbeitrag1() != 0) ? NumberFormat.getCurrencyInstance().format(service.getDeckungsbeitrag1()) : "";	
		break;	
		case Ergebnis.INDEX8_VERTEILUNG_KST1110:
		result = (service.getVerteilungKST1110() != 0) ? NumberFormat.getCurrencyInstance().format(service.getVerteilungKST1110()) : "";	
		break;			
		case Ergebnis.INDEX9_VERTEILUNG_KST2010:
		result = (service.getVerteilungKST2010() != 0) ? NumberFormat.getCurrencyInstance().format(service.getVerteilungKST2010()) : "";
		break;			
		case Ergebnis.INDEX10_VERTEILUNG_KST2020:
		result = (service.getVerteilungKST2020() != 0) ? NumberFormat.getCurrencyInstance().format(service.getVerteilungKST2020()) : "";	
		break;			
		case Ergebnis.INDEX11_VERTEILUNG_KST3010:
		result = (service.getVerteilungKST3010() != 0) ? NumberFormat.getCurrencyInstance().format(service.getVerteilungKST3010()) : "";
		break;			
		case Ergebnis.INDEX12_VERTEILUNG_KST4010:
		result = (service.getVerteilungKST4010() != 0) ? NumberFormat.getCurrencyInstance().format(service.getVerteilungKST4010()) : "";	
		break;			
		case Ergebnis.INDEX13_VERTEILUNG_KSTGESAMT:
		result = (service.getVerteilungKSTGesamt() != 0) ? NumberFormat.getCurrencyInstance().format(service.getVerteilungKSTGesamt()) : "";	
		break;			
		case Ergebnis.INDEX14_ERGBNIS:
		result = (service.getErgebnis() != 0) ? NumberFormat.getCurrencyInstance().format(service.getErgebnis()) : "";	
		break;			
		
		default:
			break;
		}
	}

return result;
}

}
