 
package de.hannit.fsch.rcp.klr.handler.user;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;


public class InsertHandler 
{
@Inject DataService dataService;	
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;

private Mitarbeiter toInsert = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.MITARBEITER_INSERT) Mitarbeiter incoming)
	{
	this.toInsert = incoming;	
	}

	@Execute
	public void execute() 
	{
	String plugin = this.getClass().getName() + ".execute()";	
	SQLException e = dataService.setMitarbeiter(toInsert);
	
		if (e != null)
		{
		log.error("Fehler beim Speichern von Mitabeiter " + toInsert.getNachname() + " in der Datenbank", plugin, e);	
		}
		else 
		{
		log.confirm("Mitarbeiter " + toInsert.getNachname() + " (" + toInsert.getPersonalNRAsString() + ") wurde erfolgreich gespeichert.", plugin);	
		}
	}
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (toInsert != null)
		{
		ready = true;
		}
	return ready;
	}
		
}