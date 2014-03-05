 
package de.hannit.fsch.rcp.klr.handler.database;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.csv.azv.AZVDatensatz;
import de.hannit.fsch.common.loga.LoGaDatensatz;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.kostenrechnung.Ergebnis;
import de.hannit.fsch.klr.kostenrechnung.Kostenrechnungsobjekt;
import de.hannit.fsch.rcp.klr.azv.AZVDatei;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class ErgebnisUpdateHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private Ergebnis ergebnis;	

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.ERGEBNIS_DATEN) Ergebnis incoming)
	{
	this.ergebnis = incoming;	
	}	

	@Execute
	public void execute() 
	{
	int teamNR = ergebnis.getTeamNR();
	java.sql.Date berichtsMonat = ergebnis.getBerichtsMonat();

	SQLException e = null;
	int insertCount = 0;
	int errorCount = 0;
	String plugin = this.getClass().getName() + ".execute()";
	
	// Schritt 1: Vorhandenes Ergebnis löschen
	e = dataService.deleteErgebnis(berichtsMonat, teamNR);
	// TODO: Wenn das Ergebnis gelöscht wird, muss es auch aus der Tabelle Datenimporte entfernt werden.
		if (e != null)
		{
		log.error("SQLException beim Löschen des Ergebnisses für Monat " + Datumsformate.MONATLANG_JAHR.format(berichtsMonat) + " für Team " + teamNR, plugin, e);	
		}
		else
		{
		log.confirm("Ergebnis für Berichtsmonat " + Datumsformate.MONATLANG_JAHR.format(berichtsMonat) + " und Team " + teamNR + " erfolgreich in der Datenbank gelöscht", plugin + ".execute()");	
		}
	// Schritt 2: Ergebnis speichern
		for (Kostenrechnungsobjekt service : ergebnis.getActiveServices())
		{
		e = dataService.setErgebnis(service.getKostenart(), teamNR, berichtsMonat, service.getErtrag(), service.getMaterialAufwand(), service.getAfa(), service.getSonstigeBetrieblicheAufwendungen(), service.getPersonalKosten(), service.getSummeEinzelkosten(), service.getDeckungsbeitrag1(), service.getVerteilungKST1110(), service.getVerteilungKST2010(), service.getVerteilungKST2020(), service.getVerteilungKST3010(), service.getVerteilungKST4010(), service.getVerteilungKSTGesamt(), service.getErgebnis());	
			if (e == null)
			{
			insertCount++;
			}
			else 
			{
			errorCount++;
			}
		}
				
			if (errorCount == 0)
			{
			log.confirm(insertCount + " Ergebnis-Datensätze erfolgreich in die Datenbank eingefügt.", plugin);	

			e =  dataService.setDatenimport(ergebnis.getDateiName(), ergebnis.getDatenQuelle(), insertCount, ergebnis.getBerichtsMonat(), "Ergebnis");
				if (e == null)
				{
				log.confirm("Ergebnis: " + ergebnis.getDatenQuelle() + " erfolgreich in der Tabelle Datenimporte gespeichert", plugin);	
				}
				else 
				{
				log.error("SQLException beim Sichern von Datenimport: " + ergebnis.getDatenQuelle(), plugin, e);	
				}
			}
			else
			{
			log.warn("Beim Speichern des Ergebnisses in die Datenbank sind " + errorCount + " Fehler aufgetreten.", plugin);	
			}		
		
	}
	
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
		if (ergebnis.getTeamNR() > 0 &&  dataService.existsErgebnis(ergebnis.getTeamNR(), ergebnis.getBerichtszeitraumVon()))
		{
		ready = true;
		}
	return ready;
	}
		
}