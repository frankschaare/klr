 
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

public class Create01Entlastung0400Handler extends CSVHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private String plugin = this.getClass().getName();

private MonatsSummen mSummen = null;
private TreeMap<String, KostenStelle> tmKostenstellen = null;
private TreeMap<String, KostenTraeger> tmKostenträger = null;
private double sumKST = 0;

private static final String DELIMITER = ";";
private static final String ZELLE2_NEHME = "0";
private static final String ZELLE2_GEBE = "1";
private static final String ZELLE4_NEHME = "1100100";
private static final String ZELLE4_GEBE = "1110100";
private static final String ENTLASTUNGSKONTO = "0400";
private static final String PATH_PRÄFIX = "\\\\RegionHannover.de\\daten\\hannit\\Rechnungswesen AöR\\KLR\\KLR ab 01.01.2011\\Arbeitszeitverteilung\\Reports\\";
private static final String PATH_SUFFIX = "\\CSV\\";
private static final String DATEINAME_PRÄFIX = "01_CSV_Entlastung 0400 auf andere KST ";
private static final String DATEINAME_SUFFIX = ".csv";

/**
 * "UML-" + Monatsziffer (01,02,03)
 */
private static final String ZELLE5_PRÄFIX = "UML-";
/**
 * "AZV " + Monat lang (MMMM)
 */
private static final String ZELLE6_PRÄFIX = "AZV ";


	
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
	@SuppressWarnings("unused")
	private void createCSV()
	{
	String feld1 = null;
	String feld2 = null;
	String feld3 = null;
	String feld4 = null;
	String feld5 = null;
	String feld6 = null;
	String feld7 = null;
	
	ArrayList<String> lines = new ArrayList<String>();	
	
	feld1 = getLetzterTagdesMonats(mSummen.getBerichtsMonatAsDate()) + DELIMITER;
	feld2 = ZELLE2_NEHME + DELIMITER;
	feld3 = ENTLASTUNGSKONTO + DELIMITER;
	feld4 = ZELLE4_NEHME + DELIMITER;
	feld5 = ZELLE5_PRÄFIX + getMonatsnummer(mSummen.getBerichtsMonatAsDate()) + DELIMITER;
	feld6 = ZELLE6_PRÄFIX + getMonatLang(mSummen.getBerichtsMonatAsDate()) + DELIMITER;
	
	DecimalFormat df = new DecimalFormat("0.00");
	feld7 = "-" + df.format(sumKST);
	
	lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7);
	
		/*
		 * Abschliessend werden die Zeilen für alle Kostenstellen geschrieben
		 * Die Zellen 1,6 und 6 bleiben dabei gleich
		 */
	feld2 = ZELLE2_GEBE + DELIMITER;
	feld4 = ZELLE4_GEBE + DELIMITER;
		for (KostenStelle kst : tmKostenstellen.values())
		{
		feld3 = kst.getBezeichnung() + DELIMITER;
		feld7 = df.format(kst.getSumme());
		
		lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7);
		}
	
	/*
	 * Alle Werte sind nun in der ArrayLIst lines gesichert, 
	 * es muss nur noch die CSV Datei geschrieben werden. 
	 * 
	 * Dazu wird ein Pfad nach dem Muster:
	 * PATH_PRÄFIX + YYYY Quartal # + PATH_SUFFIX + DATEINAME_PRÄFIX + YYYYMM + DATEINAME_SUFFIX benötigt: 	
	 */
	String strPath = PATH_PRÄFIX + getJahr(mSummen.getBerichtsMonatAsDate()) + " Quartal " + getQuartalsnummer(mSummen.getBerichtsMonatAsDate()) + PATH_SUFFIX;
	String dateiName =  DATEINAME_PRÄFIX + getJahr(mSummen.getBerichtsMonatAsDate()) + getAuswertungsmonat(mSummen.getBerichtsMonatAsDate()) + DATEINAME_SUFFIX;
	Path dateiPfad = Paths.get(strPath);	
		
		/*
		 * Wenn das Verzeichnis nicht existiert, muss es zunächst neu angelegt werden
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
		log.info("Dateipfad " + dateiPfad.toString() + " existiert bereits. Prüfe, ob Datei " + dateiName + " ebenfalls existiert.", plugin + ".execute");
		
			Path testPath = FileSystems.getDefault().getPath(strPath, dateiName);
			if (Files.exists(testPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS}))
			{
			log.error("CSV-Datei " + testPath.toString() + " existiert bereits ! Bitte prüfen und ggf. manuell löschen.", plugin, null);	
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
	 * Splitte die Monatssumen nach Kostenstellen und Kostenträgern
	 * Für diese Datei werden nur die Kostenstellen benötigt !
	 */
	private void splitKostenobjekte()
	{
		KostenStelle kst = null;
		KostenTraeger ktr = null;
		tmKostenstellen = new TreeMap<String, KostenStelle>();
		tmKostenträger = new TreeMap<String, KostenTraeger>();
			
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
				tmKostenträger.put(ktr.getBezeichnung(), ktr);	
				break;

				default:
				log.error("Monatssummen enthält ungültige Bezeichnung (" + kto.getBezeichnung() + " für Kostenrechnungsobjekt !)", this.getClass().getName(), null);	
				break;
				}
			}
			/*
			 * Sicherheitshalber werden Kostenstellen und Kostenträger erneut aufsummiert
			 * und mit den Gesamtkosten verglichlichen. Diese MÜSSEN gleich sein !	
			 */
				sumKST = 0;
				for (KostenStelle ks : tmKostenstellen.values())
				{
				sumKST += ks.getSumme();	
				}
				
				double sumKTR = 0;
				for (KostenTraeger kt : tmKostenträger.values())
				{
				sumKTR += kt.getSumme();	
				}	
		log.confirm("Gesamtsumme (" + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()) + ") wurde erfolgreich in Kostenstellen (" + NumberFormat.getCurrencyInstance().format(sumKST) + ") und Kostenträgern (" + NumberFormat.getCurrencyInstance().format(sumKTR) + ") gesplittet.", this.getClass().getName() + ".execute()");		
	}
	
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (mSummen.isChecked() && mSummen.isSummeOK())
		{
		ready = true;
		}
	return ready;
	}
		
}