/**
 * 
 */
package de.hannit.fsch.common;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.TreeMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import de.hannit.fsch.rcp.klr.constants.Topics;

/**
 * @author fsch
 *
 */
public class LoGaDatei extends CSVDatei implements ITableLabelProvider
{
private static final long serialVersionUID = -4808470669223797111L;

private String[] fields = null;
private String label = null;	
private int lineCount = 1;

	/**
	 * @param arg0
	 */
	public LoGaDatei(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public LoGaDatei(URI arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LoGaDatei(String arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public LoGaDatei(File arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setAppContext(IEclipseContext appContext)
	{
	super.appContext = appContext;
	}

	public void setBroker(IEventBroker broker)
	{
	super.broker = broker;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) 
	{
	fields = (String[]) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(lineCount);
		lineCount++;
		break;
		
		case CSVConstants.Loga.PERSONALNUMMER_INDEX_TABLE:
		label = fields[CSVConstants.Loga.PERSONALNUMMER_INDEX_CSV];
		break;
		
		case CSVConstants.Loga.BRUTTO_INDEX_TABLE:
		label = fields[CSVConstants.Loga.BRUTTO_INDEX_CSV];
		break;
		
		case CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_TABLE:
		label = fields[CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_CSV];
		break;
		
		case CSVConstants.Loga.TARIFGRUPPE_INDEX_TABLE:
		label = fields[CSVConstants.Loga.TARIFGRUPPE_INDEX_CSV];
		break;
		
		case CSVConstants.Loga.TARIFSTUFE_INDEX_TABLE:
		label = fields[CSVConstants.Loga.TARIFSTUFE_INDEX_CSV];
		break;		
		
		case CSVConstants.Loga.STELLENNTEIL_INDEX_TABLE:
			try
			{
			label = fields[CSVConstants.Loga.STELLENNTEIL_INDEX_CSV];
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
			label = "ERROR";

			Dictionary<String, Object> props = new Hashtable<String, Object>();
			props.put(EventConstants.TIMESTAMP, new Date());
			props.put(EventConstants.SERVICE_ID, this.getClass().getName() + ".ITableLabelProvider.getColumnText()");
			props.put(EventConstants.EVENT_FILTER, IStatus.ERROR);
			props.put(EventConstants.MESSAGE, "ArrayIndexOutOfBoundsException bei Personalnummer: " + fields[CSVConstants.Loga.PERSONALNUMMER_INDEX_CSV]);
			
			super.logStack = (TreeMap<Integer, Event>) appContext.get(AppConstants.LOG_STACK);
			logStack.put(logStack.size(), new Event(Topics.LOGGING, props));
			super.appContext.modify(AppConstants.LOG_STACK, logStack);
			super.broker.send(Topics.LOGGING, new Event(Topics.LOGGING, props));
			}
		break;		
		
		default:
		label = "ERROR";
		break;
			
		}
	return label;
	}

}
