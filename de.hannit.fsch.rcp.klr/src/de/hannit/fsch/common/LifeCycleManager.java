/**
 * 
 */
package de.hannit.fsch.common;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

/**
 * @author fsch
 *
 */
public class LifeCycleManager 
{

	/**
	 * 
	 */
	public LifeCycleManager() 
	{
		
		// TODO Auto-generated constructor stub
	}
	@PostContextCreate
	public void postContextCreate(final MApplication app) 
	{
	IEclipseContext c = app.getContext();
	c.set("test", new Object());
	}	
	

}
