 
package de.hannit.fsch.rcp.klr.handler.csv;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.csv.CSV01Datei;

public class Append01Entlastung0400Handler extends CSVHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
private CSV01Datei csvDatei = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.CSV01) CSV01Datei incoming)
	{
	this.csvDatei = incoming;
	}	
	
	@Execute
	public void execute() 
	{
	csvDatei.append();
	}
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
		if (csvDatei != null && csvDatei.exists())
		{
		ready = true;
		}
	return ready;
	}
		
}