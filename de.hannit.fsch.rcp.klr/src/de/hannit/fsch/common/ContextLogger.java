/**
 * 
 */
package de.hannit.fsch.common;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import de.hannit.fsch.rcp.klr.constants.Topics;

/**
 * @author fsch
 *
 */
public class ContextLogger
{
private IEclipseContext appContext;	
private IEventBroker broker;
private TreeMap<Integer, Event> logStack = new TreeMap<Integer, Event>();	
private Dictionary<String, Object> props = new Hashtable<String, Object>();

	/**
	 * 
	 */
	public ContextLogger(IEclipseContext context, IEventBroker broker)
	{
	this.appContext = context;
	this.broker = broker;
	
	props.put(EventConstants.TIMESTAMP, new Date());
	props.put(EventConstants.SERVICE_ID, this.getClass().getName());
	props.put(EventConstants.EVENT_FILTER, IStatus.INFO);
	props.put(EventConstants.MESSAGE, "Applikations Context wurde erstellt, LogStack erfolreich initialisiert.");
	
	logStack.put(logStack.size(), new Event(Topics.LOGGING, props));
	
	appContext.declareModifiable(AppConstants.LOG_STACK);
	appContext.modify(AppConstants.LOG_STACK, logStack);
	}
	
	@SuppressWarnings("unchecked")
	public TreeMap<Integer, Event> getLogStack()
	{
	return (TreeMap<Integer, Event>) appContext.get(AppConstants.LOG_STACK);
	}

	public void setLogStack(TreeMap<Integer, Event> logStack)
	{
	appContext.modify(AppConstants.LOG_STACK, logStack);
	}



	public void info(String msg, String plugin) 
	{
	logStack = getLogStack();
	
	props.put(EventConstants.TIMESTAMP, new Date());
	props.put(EventConstants.SERVICE_ID, plugin);
	props.put(EventConstants.EVENT_FILTER, IStatus.INFO);
	props.put(EventConstants.MESSAGE, msg);
	
	logStack.put(logStack.size(), new Event(Topics.LOGGING, props));
	setLogStack(logStack);
	broker.post(Topics.LOGGING, null);
	}

	public void error(String msg, String plugin, Exception e)
	{
	logStack = getLogStack();
		
	props.put(EventConstants.TIMESTAMP, new Date());
	props.put(EventConstants.SERVICE_ID, plugin);
	props.put(EventConstants.EVENT_FILTER, IStatus.ERROR);
	props.put(EventConstants.MESSAGE, msg);
		
		if (e != null)
		{
		String eName = msg.split("\\s")[0];
			if (! eName.contains("Exception"))
			{
			eName = "Exception";	
			}
		props.put(EventConstants.EXCEPTION, eName);	
		props.put(EventConstants.EXCEPTION_CLASS, e);
		props.put(EventConstants.EXCEPTION_MESSAGE, e.getMessage());
		}

	logStack.put(logStack.size(), new Event(Topics.LOGGING, props));
	setLogStack(logStack);
	broker.post(Topics.LOGGING, null);	
	}

	public void warn(String msg, String plugin)
	{
	logStack = getLogStack();
		
	props.put(EventConstants.TIMESTAMP, new Date());
	props.put(EventConstants.SERVICE_ID, plugin);
	props.put(EventConstants.EVENT_FILTER, IStatus.WARNING);
	props.put(EventConstants.MESSAGE, msg);
		
	logStack.put(logStack.size(), new Event(Topics.LOGGING, props));
	setLogStack(logStack);
	broker.post(Topics.LOGGING, null);	
	}

	public void confirm(String msg, String plugin)
	{
	logStack = getLogStack();
	
	props.put(EventConstants.TIMESTAMP, new Date());
	props.put(EventConstants.SERVICE_ID, plugin);
	props.put(EventConstants.EVENT_FILTER, IStatus.OK);
	props.put(EventConstants.MESSAGE, msg);
		
	logStack.put(logStack.size(), new Event(Topics.LOGGING, props));
	setLogStack(logStack);
	broker.post(Topics.LOGGING, null);	
	}

}
