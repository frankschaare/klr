 
package de.hannit.fsch.rcp.klr.handlers;

import java.util.TreeMap;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.LogMessage;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class LogHandler 
{
private TreeMap<Integer, LogMessage> logStack = new TreeMap<Integer, LogMessage>();	

	@Inject
	@Optional
	public void handleEvent(@UIEventTopic(Topics.LOGGING) LogMessage msg)
	{
	logStack.put(logStack.size(), msg);	
	
	}
	
	@Execute
	public void execute() 
	{
		//TODO Your code goes here
	}
	
	
	@CanExecute
	public boolean canExecute() {
		//TODO Your code goes here
		return true;
	}
		
}