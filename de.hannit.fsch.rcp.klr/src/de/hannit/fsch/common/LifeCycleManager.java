/**
 * 
 */
package de.hannit.fsch.common;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.TreeMap;

import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.ILoggerProvider;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import de.hannit.fsch.rcp.klr.constants.Topics;

/**
 * @author fsch
 * @since 11.07.2013
 * @see UIEvents
 * 
 * Beispiel für die Implementierung des LifeCycleManagers
 * Die Klasse UIEvents definiert sehr viele Topics, die genutzt werden können.
 *
 */
@SuppressWarnings("restriction")
public class LifeCycleManager 
{
	/**
	 * 
	 */
	public LifeCycleManager() 
	{

	}

	@PostContextCreate
	public void startup(IEclipseContext context, IEventBroker broker) 
	{
	context.set(AppConstants.LOGGER, new ContextLogger(context, broker));	
	}
}
	
	
	/*	
	@PostContextCreate
	public void postContextCreate(final IEventBroker broker) 
	{
	broker.subscribe(UIEvents.Context.CONTEXT, new EventHandler()
	{
		
		@Override
		public void handleEvent(Event event)
		{
		}
	});	

	}	
	*/


