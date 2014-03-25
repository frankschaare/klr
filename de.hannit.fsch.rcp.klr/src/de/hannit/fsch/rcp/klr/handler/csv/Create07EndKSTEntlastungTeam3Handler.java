 
package de.hannit.fsch.rcp.klr.handler.csv;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.mitarbeiter.GemeinKosten;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class Create07EndKSTEntlastungTeam3Handler extends CSVHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private String plugin = this.getClass().getName();

private GemeinKosten gk = null;

private static final String DELIMITER = ";";
private static final String ZELLE3 = "1200180";
private static final String ZELLE4_PRÄFIX = "KTRE ";
private static final String ZELLE6_PRÄFIX = "AZV Team ";
private static final String ZELLE8 = "L-01";
private static final String PATH_PRÄFIX = "\\\\RegionHannover.de\\daten\\hannit\\Rechnungswesen AöR\\KLR\\KLR ab 01.01.2011\\Arbeitszeitverteilung\\Reports\\";
private static final String PATH_SUFFIX = "\\CSV\\";
private static final String DATEINAME_PRÄFIX = "07_CSV_EndKST-Entlastung Team 3 ";
private static final String DATEINAME_SUFFIX = ".csv";

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.GEMEINKOSTERKOSTEN) GemeinKosten incoming)
	{
	this.gk = incoming;
	}	
	
	@Execute
	public void execute() 
	{
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
	
	feld1 = getLetzterTagdesMonats(gk.getBerichtsMonat()) + DELIMITER;
	feld2 = gk.getVorkostenStelle() + DELIMITER;
	feld3 = ZELLE3 + DELIMITER;
	feld4 = ZELLE4_PRÄFIX + gk.getVorkostenStelle() + DELIMITER;
	feld6 = ZELLE6_PRÄFIX + gk.getAktuellesTeam() + DELIMITER;
	feld8 = ZELLE8;
	
		/*
		 * Abschliessend werden die Zeilen für alle Kostenstellen geschrieben
		 * Die Zellen 1,6 und 6 bleiben dabei gleich
		 */
		for (Arbeitszeitanteil azv : gk.getAufteilungGemeinKosten().values())
		{
		feld5 = azv.getKostentraeger() + DELIMITER;
		feld7 = summenFormat.format(azv.getAnteilGemeinkosten()) + DELIMITER;;
		
		lines.add(feld1+feld2+feld3+feld4+feld5+feld6+feld7+feld8);
		}
	
	/*
	 * Alle Werte sind nun in der ArrayLIst lines gesichert, 
	 * es muss nur noch die CSV Datei geschrieben werden. 
	 * 
	 * Dazu wird ein Pfad nach dem Muster:
	 * PATH_PRÄFIX + YYYY Quartal # + PATH_SUFFIX + DATEINAME_PRÄFIX + YYYYMM + DATEINAME_SUFFIX benötigt: 	
	 */
	String strPath = PATH_PRÄFIX + getJahr(gk.getBerichtsMonat()) + " Quartal " + getQuartalsnummer(gk.getBerichtsMonat()) + PATH_SUFFIX;
	String dateiName =  DATEINAME_PRÄFIX + getJahr(gk.getBerichtsMonat()) + getAuswertungsmonat(gk.getBerichtsMonat()) + DATEINAME_SUFFIX;
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
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (gk.isChecked() && gk.isDatenOK() && gk.getAktuellesTeam().equalsIgnoreCase("3"))
		{
		ready = true;
		}
	return ready;
	}
		
}