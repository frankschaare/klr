 
package de.hannit.fsch.rcp.klr.handler.database;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppe;
import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppen;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class VZAEInsertMonatssummenHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private Tarifgruppen tgs = null;	
private	SimpleDateFormat datumsformat = new SimpleDateFormat("yyyy-MM-dd");

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.TARIFGRUPPEN) Tarifgruppen incoming)
	{
	this.tgs = incoming;
	}	

	@Execute
	public void execute() 
	{
	SQLException e = null;
	int insertCount = 0;
	int errorCount = 0;
	String plugin = this.getClass().getName() + ".execute()";
	
		for (Tarifgruppe t : tgs.getTarifGruppen().values())
		{
		e = dataService.setVZAEMonatsDaten(datumsformat.format(tgs.getBerichtsMonat()), t.getTarifGruppe(), t.getSummeTarifgruppe(), t.getSummeStellen(), t.getVollzeitAequivalent());	
			if (e == null)
			{
			// log.confirm("Loga Daten f�r Personalnummer: " + ds.getPersonalNummer() + " erfolgreich in der Datenbank gespeichert", this.getClass().getName() + ".execute()");	
			insertCount++;
			}
			else 
			{
			log.error("SQLException beim Speichern der Vollzeit�quivalente !",	plugin, e);	
			errorCount++;
			}
		}
		
		if (errorCount == 0)
		{
		log.confirm(insertCount + " Vollzeit�quivalente erfolgreich in die Datenbank eingef�gt.", plugin);	
		}
		else
		{
		log.warn("Beim Speichern der Vollzeit�quivalente in die Datenbank sind " + errorCount + " Fehler aufgetreten.", plugin);	
		}
	}
	
	
	@CanExecute
	public boolean canExecute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Object selection) 
	{
	boolean result = false;	
		if (selection instanceof Tarifgruppen)
		{
		result = true;	
		}
		else
		{
		result = false;
		}
	return result;
	}
		
}