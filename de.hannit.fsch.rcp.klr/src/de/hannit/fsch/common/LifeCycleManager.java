/**
 * 
 */
package de.hannit.fsch.common;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

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


