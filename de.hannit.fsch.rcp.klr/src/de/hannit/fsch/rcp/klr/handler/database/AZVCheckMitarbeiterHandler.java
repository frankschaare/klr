 
package de.hannit.fsch.rcp.klr.handler.database;

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
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.rcp.klr.azv.AZVDatei;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AZVCheckMitarbeiterHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
@Inject IEventBroker broker;

private AZVDatei azvDatei = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_DATEN) AZVDatei azvDatei)
	{
	this.azvDatei = azvDatei;	
	log.info("Empfange AZV-Daten (" + azvDatei.getDaten().size() + " Datens�tze)", this.getClass().getName() + ".handleEvent()");
	}	
	
	@Execute
	public void execute() 
	{
	boolean exists = false;
	String plugin = this.getClass().getName() + ".execute()";
	
		for (AZVDatensatz datensatz : azvDatei.getDaten().values())
		{
		int pnr = 0;	
		exists = dataService.existsMitarbeiter(datensatz.getPersonalNummer());
		datensatz.setMitarbeiterChecked(true);
		datensatz.setExistsMitarbeiter(exists);
			if (! exists)
			{
			log.warn("Personalnummer " + datensatz.getPersonalNummer() + " wurde nicht in Tabelle Mitarbeiter gefunden ! Versuche, die PersonalNR aus der Danbank zu lesen.", plugin);
			
				pnr = dataService.getPersonalnummer(datensatz.getNachname());
				switch (pnr)
				{
				case 0:
				azvDatei.setErrors(true);
				log.error("Bitte Mitarbeiter: " + datensatz.getNachname() + " VOR Verarbeitung dieser Datei eintragen.", plugin, null);
				break;

				default:
				datensatz.setPersonalNummer(pnr);
				datensatz.setExistsMitarbeiter(true);
				datensatz.setpersonalNummerNachgetragen(true);
				log.confirm("Personalnummer f�r Mitarbeiter: " + datensatz.getNachname() + " wurde aus der Datenbank nachgetragen.", plugin);
				break;
				}
			}
		}
	azvDatei.setChecked(true);	
	broker.send(Topics.AZV_DATEN, azvDatei);
	}
	
	/*
	 * Befehl ist ausf�hrbar, wenn die Mitarbeitertabelle noch nicht gepr�ft wurde.
	 */
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = true;
		
		if (azvDatei.isChecked())
		{
		ready = false;
		}
	return ready;
	}
		
}