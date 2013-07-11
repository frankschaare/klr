/**
 * 
 */
package de.hannit.fsch.common;

import java.util.TreeMap;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;

/**
 * @author fsch
 *
 */
public class RunAndTrackExample extends RunAndTrack
{

	/**
	 * 
	 */
	public RunAndTrackExample()
	{
		// TODO Auto-generated constructor stub
	}

	public RunAndTrackExample(IEclipseContext context, TreeMap<Integer, LogMessage> logStack)
	{
	context.declareModifiable(AppConstants.LOG_STACK);
	context.modify(AppConstants.LOG_STACK, logStack);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.e4.core.contexts.RunAndTrack#changed(org.eclipse.e4.core.contexts.IEclipseContext)
	 */
	@Override
	public boolean changed(IEclipseContext context)
	{
	System.out.println("RunAndTrack wurde aufgerufen");
	return true;
	}

	@Override
	protected synchronized void runExternalCode(Runnable runnable)
	{
		// TODO Auto-generated method stub
		super.runExternalCode(runnable);
	}
	
	

}
