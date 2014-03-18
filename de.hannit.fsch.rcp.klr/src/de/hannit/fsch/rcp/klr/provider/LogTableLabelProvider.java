/**
 * 
 */
package de.hannit.fsch.rcp.klr.provider;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

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
private TreeMap<String, Image> imageCache = null;
private static final String KEY_ERROR = "error";
private static final String KEY_WARN = "warn";
private static final String KEY_CONFIRM = "confirm";
private static final String KEY_INFO = "info";


	/**
	 * 
	 */
	public LogTableLabelProvider() 
	{
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());	
	imageCache = new TreeMap<String, Image>();	
	
	url = FileLocator.find(bundle, new Path("icons/error_tsk.gif"), null);
	ImageDescriptor image = ImageDescriptor.createFromURL(url);
	imageCache.put(LogTableLabelProvider.KEY_ERROR, image.createImage());
	
	url = FileLocator.find(bundle, new Path("icons/warn_tsk.gif"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put(LogTableLabelProvider.KEY_WARN, image.createImage());
	
	url = FileLocator.find(bundle, new Path("icons/checked.gif"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put(LogTableLabelProvider.KEY_CONFIRM, image.createImage());
	
	url = FileLocator.find(bundle, new Path("icons/info_tsk.gif"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put(LogTableLabelProvider.KEY_INFO, image.createImage());
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
	public void dispose() 
	{
		if (imageCache != null)
		{
			for (Image img : imageCache.values())
			{
			img.dispose();	
			}
		imageCache = null;	
		}
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
	String key = null;		
	
		switch (columnIndex)
		{
		case 0:
			if (element instanceof Event) 
			{
			event = (Event) element;	
			}
			
			switch ((int) event.getProperty(EventConstants.EVENT_FILTER))
			{
			case IStatus.ERROR: key = LogTableLabelProvider.KEY_ERROR; break;
			case IStatus.WARNING: key = LogTableLabelProvider.KEY_WARN;	break;	
			case IStatus.OK: key = LogTableLabelProvider.KEY_CONFIRM; break;	
			default: key = LogTableLabelProvider.KEY_INFO; break;
			}
		return imageCache.get(key);

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
