/**
 * 
 */
package de.hannit.fsch.rcp.klr.provider;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

/**
 * @author fsch
 *
 */
public class LogTableLabelProvider implements ITableLabelProvider 
{
private Event event = null;	
private String label = null;
private DateFormat dfmt = new SimpleDateFormat( "dd.MM.yyyy HH:mm:ss" );
private URL url = null;


	/**
	 * 
	 */
	public LogTableLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) 
	{
		switch (columnIndex)
		{
		case 0:
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			if (element instanceof Event) 
			{
				event = (Event) element;	
			}
			
			switch ((int) event.getProperty(EventConstants.EVENT_FILTER))
			{
			case IStatus.ERROR:
				url = FileLocator.find(bundle, new Path("icons/error_tsk.gif"), null);	
				break;
			case IStatus.WARNING:
				url = FileLocator.find(bundle, new Path("icons/warn_tsk.gif"), null);	
				break;	
			case IStatus.OK:
				url = FileLocator.find(bundle, new Path("icons/checked.gif"), null);	
				break;	
			default:
				url = FileLocator.find(bundle, new Path("icons/info_tsk.gif"), null);	
				break;
			}
		ImageDescriptor image = ImageDescriptor.createFromURL(url);	
		return image.createImage();

		default:
		return null;	
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) 
	{
		if (element instanceof Event) 
		{
		event = (Event) element;	
		}
		switch (columnIndex) 
		{
		case 0:
		label = (String) event.getProperty(EventConstants.MESSAGE);
		break;
		case 1:
		label = (String) event.getProperty(EventConstants.SERVICE_ID);
		break;		
		case 2:
		label = dfmt.format(event.getProperty(EventConstants.TIMESTAMP));
		break;
		default:
		label = "";
		break;
			
		}
	return label;
	}

}
