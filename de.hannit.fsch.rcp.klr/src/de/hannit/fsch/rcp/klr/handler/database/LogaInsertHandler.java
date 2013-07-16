 
package de.hannit.fsch.rcp.klr.handler.database;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class LogaInsertHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
private LoGaDatei logaDatei = null;	

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.LOGA_DATEN) LoGaDatei logaDatei)
	{
	this.logaDatei = logaDatei;	
	log.info("Empfange LoGa-Daten (" + logaDatei.getDaten().size() + " Datensätze)", this.getClass().getName() + ".handleEvent()");
	}	

	@Execute
	public void execute() {
		//TODO Your code goes here
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