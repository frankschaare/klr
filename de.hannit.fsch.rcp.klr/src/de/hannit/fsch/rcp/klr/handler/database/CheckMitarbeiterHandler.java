 
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
import de.hannit.fsch.common.loga.LoGaDatensatz;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class CheckMitarbeiterHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
@Inject IEventBroker broker;

private LoGaDatei logaDatei = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.LOGA_DATEN) LoGaDatei logaDatei)
	{
	this.logaDatei = logaDatei;	
	log.info("Empfange LoGa-Daten (" + logaDatei.getDaten().size() + " Datensätze)", this.getClass().getName() + ".handleEvent()");
	}	
	
	@Execute
	public void execute() 
	{
	boolean exists = false;
	
		for (LoGaDatensatz datensatz : logaDatei.getDaten().values())
		{
		exists = dataService.existsMitarbeiter(datensatz.getPersonalNummer());
		datensatz.setMitarbeiterChecked(true);
		datensatz.setexistsMitarbeiter(exists);
			if (! exists)
			{
			logaDatei.setErrors(true);	
			log.warn("Personalnummer " + datensatz.getPersonalNummer() + " wurde nicht in Tabelle Mitarbeiter gefunden !", this.getClass().getName() + ".execute()");	
			}
		}
	logaDatei.setChecked(true);	
	broker.send(Topics.LOGA_DATEN, logaDatei);
	}
	
	/*
	 * Befehl ist ausführbar, wenn die Mitarbeitertabelle noch nicht geprüft wurde.
	 */
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = true;
		
		if (logaDatei.isChecked())
		{
		ready = false;
		}
	return ready;
	}
		
}