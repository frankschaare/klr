/**
 * 
 */
package de.hannit.fsch.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;

/**
 * @author fsch
 *
 */
public class LogMessage implements IStatus 
{
private int severity = IStatus.INFO;
private String message = "";
private String plugin = "";
private String timeStamp;
	/**
	 * 
	 */
	public LogMessage(int severity, String plugin, String message) 
	{
	this.severity = severity;
	this.plugin = plugin;
	this.message = message;
	setTimeStamp();
	}

	public String getTimeStamp() 
	{
	return timeStamp;
	}

	private void setTimeStamp() 
	{
	DateFormat fmt = new SimpleDateFormat( "dd.MM.yy hh:mm:ss" );	
	this.timeStamp = fmt.format(new Date());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#getChildren()
	 */
	@Override
	public IStatus[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#getCode()
	 */
	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#getException()
	 */
	@Override
	public Throwable getException() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#getMessage()
	 */
	@Override
	public String getMessage() {return message;}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#getPlugin()
	 */
	@Override
	public String getPlugin() {return plugin;}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#getSeverity()
	 */
	@Override
	public int getSeverity() 
	{
	return severity;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#isMultiStatus()
	 */
	@Override
	public boolean isMultiStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#isOK()
	 */
	@Override
	public boolean isOK() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IStatus#matches(int)
	 */
	@Override
	public boolean matches(int severityMask) {
		// TODO Auto-generated method stub
		return false;
	}

}
