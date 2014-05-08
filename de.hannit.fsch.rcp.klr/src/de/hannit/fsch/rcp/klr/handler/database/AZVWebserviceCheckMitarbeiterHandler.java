 
package de.hannit.fsch.rcp.klr.handler.database;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AZVWebserviceCheckMitarbeiterHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
@Inject IEventBroker broker;

private AZVDaten azvDaten = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_WEBSERVICE) AZVDaten incoming)
	{
	this.azvDaten = incoming;	
	// log.info("Empfange AZV-Daten (" + azvDatei.getDaten().size() + " Datensätze)", this.getClass().getName() + ".handleEvent()");
	}	
	
	@Execute
	public void execute() 
	{
	boolean exists = false;
	boolean existsAZVMeldung = false;
	java.sql.Date berichtsMonat = azvDaten.getBerichtsMonatSQL();
	
	String plugin = this.getClass().getName() + ".execute()";
	
		for (AZVDatensatz datensatz : azvDaten.getAzvMeldungen())
		{
		int pnr = 0;	
		// Ist bereits eine AZV-Meldung in der Datenbank vorhanden ?
		existsAZVMeldung = dataService.existsAZVDatensatz(datensatz.getPersonalNummer(), berichtsMonat);
		datensatz.setExistsAZVDatensatz(existsAZVMeldung);
		
		exists = dataService.existsMitarbeiter(datensatz.getPersonalNummer());
		datensatz.setMitarbeiterChecked(true);
		datensatz.setExistsMitarbeiter(exists);
			if (! exists)
			{
			log.warn("Personalnummer " + datensatz.getPersonalNummer() + " wurde nicht in Tabelle Mitarbeiter gefunden ! Versuche, die PersonalNR aus der Datenbank zu lesen.", plugin);
			
				pnr = dataService.getPersonalnummer(datensatz.getNachname());
				switch (pnr)
				{
				case 0:
				azvDaten.setErrors(true);
				log.error("Bitte Mitarbeiter: " + datensatz.getNachname() + " VOR Verarbeitung dieser Datei eintragen.", plugin, null);
				break;

				default:
				datensatz.setPersonalNummer(pnr);
				datensatz.setExistsMitarbeiter(true);
				datensatz.setpersonalNummerNachgetragen(true);
				log.confirm("Personalnummer für Mitarbeiter: " + datensatz.getNachname() + " wurde aus der Datenbank nachgetragen.", plugin);
				break;
				}
			}
		}
		
	checkTeamMitgliedschaften();	
	azvDaten.setChecked(true);	
	broker.send(Topics.AZV_WEBSERVICE, azvDaten);
	}
	
	private void checkTeamMitgliedschaften()
	{
	TreeMap<Integer, ArrayList<Integer>> tm = azvDaten.setTeamMitgliedschaft();
	ArrayList<Integer> teamNummern = null;
	TreeMap<Integer, Integer> checked = new TreeMap<>();
	
	log.info("Es wurden Teammitgliedschaften für " + tm.size() + " Mitarbeiter aus den AZV-Meldungen ermittelt. Prüfe Daten...", this.getClass().getName() + ".checkTeamMitgliedschaften()");
		
		for (Integer pnr : tm.keySet())
		{
		teamNummern = tm.get(pnr);
			switch (teamNummern.size())
			{
			// I.d.R. sollte ein Mitarbeiter AZV-Meldungen für genau EIN Team abgeben:
			case 1:
			checked.put(pnr, teamNummern.get(teamNummern.size() - 1));	
			break;

			default:
			log.warn("Mitarbeiter " + pnr + " hat AZV-Meldungen ein fehlerhaftes oder für mehrere Team abgegeben. Bitte manuell prüfen !", this.getClass().getName() + ".checkTeamMitgliedschaften()");				
			break;
			}
		}
	azvDaten.setTeamMitglieder(checked);
	log.confirm("Prüfung abgeschlossen. Für " + tm.size() + " Mitarbeiter wurden gültige AZV-Meldungen ermittelt.", this.getClass().getName() + ".checkTeamMitgliedschaften()");
	}	
	
	/*
	 * Befehl ist ausführbar, wenn die Mitarbeitertabelle noch nicht geprüft wurde.
	 */
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
		
		if (! azvDaten.isChecked() && azvDaten.isRequestComplete())
		{
		ready = true;
		}
	return ready;
	}
		
}