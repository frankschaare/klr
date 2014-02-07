 
package de.hannit.fsch.rcp.klr.handler.csv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.kostenrechnung.KostenTraeger;
import de.hannit.fsch.klr.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.rcp.klr.constants.Topics;

/**
 * Erstellt die CSV-Datei f�r die Entlastung der Kostenstelle 0400 auf andere Kostentr�ger
 * Im Gegensatz zur Datei 01, in der die Kostenstellen umgelegt werden, wird hier pro Monat eine Datei erstellt.
 * 
 * Zudem sind die Bezeichnungen der Konstanten etwas anders und es wird in der ersten Zeile keine Gesamtentlastung gebucht.
 * @author fsch
 * @since 07.02.2014
 *
 */
public class Create03Entlastung0400Handler extends CSVHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private String plugin = this.getClass().getName();

private MonatsSummen mSummen = null;
private TreeMap<String, KostenStelle> tmKostenstellen = null;
private TreeMap<String, KostenTraeger> tmKostentr�ger = null;
private double sumKST = 0;

private static final String DELIMITER = ";";
// Zelle 1 = Monatsletzter
private static final String ZELLE2_ENTLASTUNGSKOSTENSTELLE = "0400";
private static final String ZELLE3 = "1200100";
private static final String ZELLE4 = "AZV EUR";
// Zelle 5 Variabel = Kostentr�ger
/**
 * "AZV " + Monat lang (MMMM)
 */
private static final String ZELLE6_PR�FIX = "AZV ";
// Zelle 7 Variabel = Kostentr�ger Summe
private static final String ZELLE8 = "L-01";
private static final String PATH_PR�FIX = "\\\\RegionHannover.de\\daten\\hannit\\Rechnungswesen A�R\\KLR\\KLR ab 01.01.2011\\Arbeitszeitverteilung\\Reports\\";
private static final String PATH_SUFFIX = "\\CSV\\";
/**
 * Dateiname wird nach dem Muster:
 * 02_CSV_Endlastung 0400 auf KTR_MMMM.csv
 * gebildet.
 */
private static final String DATEINAME_PR�FIX = "03_CSV_Entlastung 0400 auf KTR_";
private static final String DATEINAME_SUFFIX = ".csv";

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.MONATSSUMMEN) MonatsSummen incoming)
	{
	this.mSummen = incoming;
	}	

	@Execute
	public void execute() 
	{
	splitKostenobjekte();
	createCSV();
	}
	
	/*
	 * Erstellt alle Zeilen der Datei und schreibt diese
	 */
	private void createCSV()
	{
	String feld1 = null;
	String feld2 = null;
	String feld3 = null;
	String feld4 = null;
	String feld5 = null;
	String feld6 = null;
	String feld7 = null;
	String feld8 = null;
	
	ArrayList<String> lines = new ArrayList<String>();	
	
	feld1 = getLetzterTagdesMonats(mSummen.getBerichtsMonatAsDate()) + DELIMITER;
	feld2 = ZELLE2_ENTLASTUNGSKOSTENSTELLE + DELIMITER;
	feld3 = ZELLE3 + DELIMITER;
	feld4 = ZELLE4 + DELIMITER;
	feld6 = ZELLE6_PR�FIX + getMonatLang(mSummen.getBerichtsMonatAsDate()) + DELIMITER;
	feld8 = ZELLE8;
	


	
		/*
		 * Abschliessend werden die Zeilen f�r alle Kostentr�ger geschrieben
		 * Die Zellen 1,2,3,4,6 und 8 bleiben dabei gleich
		 */
		for (KostenTraeger ktr : tmKostentr�ger.values())
		{
		feld5 = ktr.getBezeichnung() + DELIMITER;
		feld7 = summenFormat.format(ktr.getSumme())+DELIMITER;
		
		lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7+feld8);
		}
	
	/*
	 * Alle Werte sind nun in der ArrayLIst lines gesichert, 
	 * es muss nur noch die CSV Datei geschrieben werden. 
	 * 
	 * Dazu wird ein Pfad nach dem Muster:
	 * PATH_PR�FIX + YYYY Quartal # + PATH_SUFFIX + DATEINAME_PR�FIX + YYYYMM + DATEINAME_SUFFIX ben�tigt: 	
	 */
	String strPath = PATH_PR�FIX + getJahr(mSummen.getBerichtsMonatAsDate()) + " Quartal " + getQuartalsnummer(mSummen.getBerichtsMonatAsDate()) + PATH_SUFFIX;
	String dateiName =  DATEINAME_PR�FIX + getMonatLang(mSummen.getBerichtsMonatAsDate()) + DATEINAME_SUFFIX;
	Path dateiPfad = Paths.get(strPath);	
		
		/*
		 * Wenn das Verzeichnis nicht existiert, muss es zun�chst neu angelegt werden
		 */
		if (!Files.exists(dateiPfad, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
		{
		log.info("Dateipfad " + dateiPfad.toString() + " wurde nicht gefunden und wird neu angelegt.", this.getClass().getName() + ".execute");	
			try
			{
			Files.createDirectory(dateiPfad);
			log.confirm("Verzeichnis " + dateiPfad.toString() + " wurde erfogreich angelegt.", plugin);
			}
			catch (IOException e)
			{
			log.error("Verzeichnis " + dateiPfad.toString() + " konnte nicht erstellt werde. !", plugin, e);	
			e.printStackTrace();
			}
		}
		else
		{
		log.info("Dateipfad " + dateiPfad.toString() + " existiert bereits. Pr�fe, ob Datei " + dateiName + " ebenfalls existiert.", plugin + ".execute");
		
			Path testPath = FileSystems.getDefault().getPath(strPath, dateiName);
			if (Files.exists(testPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
			{
			log.error("CSV-Datei " + testPath.toString() + " existiert bereits ! Bitte pr�fen und ggf. manuell l�schen.", plugin, null);	
			}
			else
			{
				try
				{
				Files.createFile(testPath);
				Files.write(testPath, lines, Charset.forName("ISO-8859-15"), StandardOpenOption.WRITE);
				log.confirm("Datei " + testPath.toString() + " wurde erfogreich angelegt.", plugin);
				}
				catch (IOException e)
				{
				log.error("Datei " + testPath.toString() + " konnte nicht erstellt werde. !", plugin, e);	
				e.printStackTrace();
				}	
			}
		}

	}

	/*
	 * Splitte die Monatssumen nach Kostenstellen und Kostentr�gern
	 * F�r diese Datei werden nur die Kostenstelle ben�tigt !
	 */
	private void splitKostenobjekte()
	{
		KostenStelle kst = null;
		KostenTraeger ktr = null;
		tmKostenstellen = new TreeMap<String, KostenStelle>();
		tmKostentr�ger = new TreeMap<String, KostenTraeger>();
			
			for (Kostenrechnungsobjekt kto : mSummen.getGesamtKosten().values())
			{
				switch (kto.getBezeichnung().length())
				{
				case 4:
				kst = new KostenStelle();
				kst.setBezeichnung(kto.getBezeichnung());
				kst.setSumme(kto.getSumme());
				tmKostenstellen.put(kst.getBezeichnung(), kst);		
				break;
				case 8:
				ktr = new KostenTraeger();
				ktr.setBezeichnung(kto.getBezeichnung());
				ktr.setSumme(kto.getSumme());
				tmKostentr�ger.put(ktr.getBezeichnung(), ktr);	
				break;

				default:
				log.error("Monatssummen enth�lt ung�ltige Bezeichnung (" + kto.getBezeichnung() + " f�r Kostenrechnungsobjekt !)", this.getClass().getName(), null);	
				break;
				}
			}
			/*
			 * Sicherheitshalber werden Kostenstellen und Kostentr�ger erneut aufsummiert
			 * und mit den Gesamtkosten verglichlichen. Diese M�SSEN gleich sein !	
			 */
				sumKST = 0;
				for (KostenStelle ks : tmKostenstellen.values())
				{
				sumKST += ks.getSumme();	
				}
				
				double sumKTR = 0;
				for (KostenTraeger kt : tmKostentr�ger.values())
				{
				sumKTR += kt.getSumme();	
				}	
		log.confirm("Gesamtsumme (" + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()) + ") wurde erfolgreich in Kostenstellen (" + NumberFormat.getCurrencyInstance().format(sumKST) + ") und Kostentr�gern (" + NumberFormat.getCurrencyInstance().format(sumKTR) + ") gesplittet.", this.getClass().getName() + ".execute()");		
	}
	
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (mSummen.isChecked() && mSummen.isSummeOK() && getMonatsnummer(mSummen.getBerichtsMonatAsDate()).equalsIgnoreCase("02"))
		{
		ready = true;
		}
	return ready;
	}
		
}