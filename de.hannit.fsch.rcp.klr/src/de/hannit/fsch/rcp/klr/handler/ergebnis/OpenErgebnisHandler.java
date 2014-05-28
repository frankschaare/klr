 
package de.hannit.fsch.rcp.klr.handler.ergebnis;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.Ergebnis;
import de.hannit.fsch.common.ErgebnisImport;
import de.hannit.fsch.klr.model.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class OpenErgebnisHandler 
{
@Inject EPartService partService;
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
private String plugin = this.getClass().getName();
private Ergebnis ergebnis = null;
private TreeMap<Integer, Kostenrechnungsobjekt> services = null;

@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService partService) 
	{
	FileDialog dialog = new FileDialog(shell);
	dialog.setFilterPath(CSVConstants.ERGEBNIS.ERGEBNIS_IMPORT_DIR);
	dialog.setFilterExtensions(new String[] {"*.csv","*.txt", "*.*"});
	String path = dialog.open();
		
		if (path != null) 
		{
		ErgebnisImport csv = new ErgebnisImport(path);
		csv.setDelimiter(";");
		csv.hasHeader(false);
		csv.read();
	
		ergebnis = new Ergebnis();
		ergebnis.setDatenQuelle(csv.getPath());
		ergebnis.setDateiName(csv.getName());
			/*
			 * Zeilen werden erstmalig durchlaufen und es wird die Zeile gesucht, 
			 * deren erstes Feld leer ist. Dies ist die Zeile mit den Services:
			 */
			int found = 0;
			String[] serviceKatalog = null;
			for (String[] parts : csv.getFields())
			{
				if (parts[0].isEmpty())
				{
				found+=1;
				serviceKatalog = parts;
				}
			}
			switch (found)
			{
			case 1:
			log.confirm("Servicekatalog wurde gefunden, beginne mit der Erstellung der Services...", plugin + ".execute()");	
			setServices(serviceKatalog);
			processServices(csv.getFields());
			log.info("Ergebnis-Datei: " + path + " wurde mit " + services.size() + " Services eingelesen.", plugin + ".execute()");
			ergebnis.setServices(services);
			
			MPart ergebnisImportPart = partService.findPart("de.hannit.fsch.rcp.klr.part.ErgebnisImportPart");
				/*
				 * Wird der Handler erstmalig aufgerufen, werden Services im PartContext gespeichert.
				 * Erfolgt ein erneuter Aufruf, werden die Daten nur über den Broker versendet
				 */				
				if (ergebnisImportPart.getContext() != null)
				{
				broker.send(Topics.ERGEBNIS_DATEN, ergebnis);	
				}
				else 
				{
				IEclipseContext partContext = EclipseContextFactory.create();
				ergebnisImportPart.setContext(partContext);
				ergebnisImportPart.getContext().set(AppConstants.CONTEXT_ERGEBNIS, ergebnis);
				ergebnisImportPart.setVisible(true);
				partService.activate(ergebnisImportPart);
				}
			break;

			default:
			log.error("Fehlerhaftes Format der Importdatei, Import kann nicht erstellt werden !", plugin + ".execute()", null);	
			break;
			}		
		}	
	}

	private void processServices(ArrayList<String[]> fields)
	{
		for (String[] parts : fields)
		{
			String test = parts[0];
			
			switch (test)
			{
			// Erste Zeile enthält Datumsfilter:
			case Ergebnis.CSV_ZEILE1_DATUMSFILTER:
			parseDatumsfilter(parts[1]);	
			break;
			// Zweite Zeile enthält nur die Währung, wird z.Zt. nicht berücksichtigt:
			case Ergebnis.CSV_ZEILE2_WÄHRUNG:
			break;
			// Vierte Zeile enthält die Erlöse des Services
			case Ergebnis.CSV_ZEILE4_ERLÖSE:
			setErloese(parts);	
			break;			
			// Fünfte Zeile enthält den Materialaufwand
			case Ergebnis.CSV_ZEILE5_MATERIALAUFWAND:
			setMaterialaufwand(parts);	
			break;		
			// Sechste Zeile enthält die AfA
			case Ergebnis.CSV_ZEILE6_AFA:
			setAfA(parts);	
			break;		
			// Siebte Zeile enthält die sonstigen betrieblichen Aufwendungen
			case Ergebnis.CSV_ZEILE7_SBA:
			setSBA(parts);	
			break;				
			// Achte Zeile enthält die Personalkosten
			case Ergebnis.CSV_ZEILE8_PERSONALKOSTEN:
			setPersonalkosten(parts);	
			break;				
			// Neunte Zeile enthält die Summe Einzelkosten
			case Ergebnis.CSV_ZEILE9_EINZELKOSTEN:
			setEinzelkosten(parts);	
			break;	
			// Zehnte Zeile enthält den Deckungsbeitrag1
			case Ergebnis.CSV_ZEILE10_DECKUNGSBEITRAG1:
			setDeckungsbeitrag1(parts);	
			break;					
			// Elfte Zeile enthält die Verteilung KSt. 1110
			case Ergebnis.CSV_ZEILE11_KST1110:
			setVerteilung1110(parts);	
			break;	
			// Zwölfte Zeile enthält die Verteilung KSt. 2010
			case Ergebnis.CSV_ZEILE12_KST2010:
			setVerteilung2010(parts);	
			break;					
			// Dreizehnte Zeile enthält die Verteilung KSt. 2020
			case Ergebnis.CSV_ZEILE13_KST2020:
			setVerteilung2020(parts);	
			break;	
			// Vierzehnte Zeile enthält die Verteilung KSt. 3010
			case Ergebnis.CSV_ZEILE14_KST3010:
			setVerteilung3010(parts);	
			break;				
			// Fünfzehnte Zeile enthält die Verteilung KSt. 4010
			case Ergebnis.CSV_ZEILE15_KST4010:
			setVerteilung4010(parts);	
			break;	
			// Sechzehnte Zeile enthält die Summe der Verteilungen
			case Ergebnis.CSV_ZEILE16_KSTGESAMT:
			setVerteilungKST(parts);	
			break;					
			// Siebzehnte Zeile enthält das Ergebnis
			case Ergebnis.CSV_ZEILE17_ERGBNIS:
			setErgebnis(parts);	
			break;	
			
			default:
			
			break;
			}		
		}
	}

	private void setErgebnis(String[] parts)
	{
	double ergebnis= 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		ergebnis = parseWert(parts[i]);
		services.get(i).setErgebnis(ergebnis);
		}
	}

	private void setVerteilungKST(String[] parts)
	{
	double verteilung = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		verteilung = parseWert(parts[i]);
		services.get(i).setVerteilungKSTGesamt(verteilung);
		}
	}

	private void setVerteilung4010(String[] parts)
	{
	double verteilung = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		verteilung = parseWert(parts[i]);
		services.get(i).setVerteilungKST4010(verteilung);
		}
	}

	private void setVerteilung3010(String[] parts)
	{
	double verteilung = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		verteilung = parseWert(parts[i]);
		services.get(i).setVerteilungKST3010(verteilung);
		}
	}

	private void setVerteilung2020(String[] parts)
	{
	double verteilung = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		verteilung = parseWert(parts[i]);
		services.get(i).setVerteilungKST2020(verteilung);
		}
	}

	private void setVerteilung2010(String[] parts)
	{
	double verteilung = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		verteilung = parseWert(parts[i]);
		services.get(i).setVerteilungKST2010(verteilung);
		}
	}

	private void setVerteilung1110(String[] parts)
	{
	double verteilung = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		verteilung = parseWert(parts[i]);
		services.get(i).setVerteilungKST1110(verteilung);
		}
		
	}

	private void setDeckungsbeitrag1(String[] parts)
	{
	double deckungsbeitrag = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		deckungsbeitrag = parseWert(parts[i]);
		services.get(i).setDeckungsbeitrag1(deckungsbeitrag);
		}
		
	}

	private void setEinzelkosten(String[] parts)
	{
	double summeEinzelkosten = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		summeEinzelkosten = parseWert(parts[i]);
		services.get(i).setSummeEinzelkosten(summeEinzelkosten);
		}
	}

	private void setPersonalkosten(String[] parts)
	{
	double pk = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		pk = parseWert(parts[i]);
		services.get(i).setPersonalKosten(pk);
		}	
	}

	private void setSBA(String[] parts)
	{
	double sba = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		sba = parseWert(parts[i]);
		services.get(i).setSonstigeBetrieblicheAufwendungen(sba);
		}
		
	}

	private void setAfA(String[] parts)
	{
	double afa = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		afa = parseWert(parts[i]);
		services.get(i).setAfa(afa);
		}
	}

	private void setMaterialaufwand(String[] parts)
	{
	double materialAufwand = 0;
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		materialAufwand = parseWert(parts[i]);
		services.get(i).setMaterialAufwand(materialAufwand);
		}
	}
	

	private void setErloese(String[] parts)
	{
	double ertrag = 0;
	
		/*
		 *  Achtung, 0 ist die Bezeichnung und der letzte Eintrag ist die Gesamtsumme !
		 *  Die Schleife ignoriert daher den ersten und letzen Eintrag des Arrays.
		 */
		for (int i = 1; i < (parts.length - 1); i++)
		{
		ertrag = parseWert(parts[i]);
		services.get(i).setErtrag(ertrag);
		}
	}
	
	/*
	 * Parsed den gegebenen String in eine Kommazahl.
	 * Ist der String leer, wird 0 zurückgegeben
	 */
	private double parseWert(String strWert)
	{
	double wert = 0;
		if (!strWert.isEmpty())
		{
		strWert = strWert.replaceAll("\\.", "");
		strWert = strWert.replace(",", ".");
		wert = Double.parseDouble(strWert);
		}
	return wert;
	}

	/*
	 * Splittet die Ergebnisse und erzeugt daraus die entsprechenden Kostenrechnungsobjekte
	 */
	private void setServices(String[] parts)
	{
	services = new TreeMap<Integer, Kostenrechnungsobjekt>();
	Kostenrechnungsobjekt service = null;
	String[] serviceDaten = null;
	
		// Achtung, 0 ist leer !
		for (int i = 1; i < parts.length; i++)
		{
			if (parts[i].contains(":"))
			{
			serviceDaten = parts[i].split(":");	
			service = new Kostenrechnungsobjekt();
			service.setKostenart(serviceDaten[0]);
			service.setBezeichnung(serviceDaten[1]);
			
			services.put(i, service);
			}
		}
	}

	/*
	 * Aus der ersten Zeile wird BerichtszeitraumVon und BerichtszeitraumBis gelesen
	 */
	private void parseDatumsfilter(String string)
	{
	String[] zeitraum = string.split("\\.\\.");	
		try
		{
		Date zeitraumVon = Datumsformate.STANDARDFORMAT.parse(zeitraum[0]);
		ergebnis.setBerichtszeitraumVon(zeitraumVon);
		}
		catch (ParseException e)
		{
		e.printStackTrace();	
		log.error("Fehler beim parsen des Startdatums: " + zeitraum[0], plugin + "parseDatumsfilter("+string+")", e);	
		}
		
		try
		{
		Date zeitraumBis = Datumsformate.STANDARDFORMAT.parse(zeitraum[1]);
		ergebnis.setBerichtszeitraumBis(zeitraumBis);
		}
		catch (ParseException e)
		{
		e.printStackTrace();	
		log.error("Fehler beim parsen des Enddatums: " + zeitraum[1], plugin + "parseDatumsfilter("+string+")", e);	
		}		
	}		
}