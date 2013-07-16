 
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
import de.hannit.fsch.common.loga.LoGaDatensatz;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class LogaInsertHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
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
	SQLException e = null;
	int insertCount = 0;
	int errorCount = 0;
	String plugin = this.getClass().getName() + ".execute()";
	
		for (LoGaDatensatz ds : logaDatei.getDaten().values())
		{
		e = dataService.setLoGaDaten(ds);	
			if (e == null)
			{
			// log.confirm("Loga Daten für Personalnummer: " + ds.getPersonalNummer() + " erfolgreich in der Datenbank gespeichert", this.getClass().getName() + ".execute()");	
			insertCount++;
			}
			else 
			{
			log.error("SQLException beim Loga-Import von Datensatz: " + ds.getSource(),	plugin, e);	
			errorCount++;
			}
		}
		
		if (errorCount == 0)
		{
		log.confirm(insertCount + " Loga-Datensätze erfolgreich in die Datenbank eingefügt.", plugin);	
		}
		else
		{
		log.warn("Beim Import der Loga-Daten in die Datenbank sind " + errorCount + " Fehler aufgetreten.", plugin);	
		}
	}
	
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (logaDatei.isChecked() && ! logaDatei.hasErrors())
		{
		ready = true;
		}
	return ready;
	}
		
}