package de.hannit.fsch.rcp.klr.provider;

import java.net.URL;
import java.util.TreeMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.klr.model.Constants;


public class AZVWebservicePartLabelProvider implements ITableLabelProvider, ITableColorProvider
{
private URL url = null;
private TreeMap<String, Image> imageCache = null;
private String columnText = null;
	

	public AZVWebservicePartLabelProvider()
	{
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());	
	imageCache = new TreeMap<String, Image>();	
		
	url = FileLocator.find(bundle, new Path("icons/warn_tsk.gif"), null);
	ImageDescriptor image = ImageDescriptor.createFromURL(url);
	imageCache.put("pnrNachgetragen", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/checked.gif"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("pnrVorhanden", image.createImage());
	url = FileLocator.find(bundle, new Path("icons/error_tsk.gif"), null);
	image = ImageDescriptor.createFromURL(url);
	imageCache.put("pnrFehlt", image.createImage());
	}

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
		if (imageCache != null)
		{
			for (Image img : imageCache.values())
			{
			img.dispose();	
			}
		imageCache = null;	
		}

	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		// TODO Auto-generated method stub
		return false;
	}

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
	AZVDatensatz datenSatz = null;
	String key = null;	
	
		switch (columnIndex)
		{
		case Constants.AZV.PERSONALNUMMER_INDEX_TABLE:
			
			if (element instanceof AZVDatensatz) 
			{
			datenSatz =  (AZVDatensatz) element;	
			
				if (datenSatz.isMitarbeiterChecked())
				{
					if (datenSatz.existsMitarbeiter())
					{
						if (datenSatz.personalNummerNachgetragen())
						{
						key = "pnrNachgetragen";	
						}
						else
						{
						key = "pnrVorhanden";
						}
					}
					else
					{
					key = "pnrFehlt";						
					}					
				return imageCache.get(key);
				}
				else
				{
				return null;
				}
			}

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
		if (element != null && element instanceof AZVDatensatz)
		{
		AZVDatensatz azv = (AZVDatensatz) element;
		
			switch (columnIndex)
			{
			case 0:
			columnText = String.valueOf(azv.getRowCount()).trim();
			break;
			case Constants.AZV.PERSONALNUMMER_INDEX_TABLE:
			columnText = String.valueOf(azv.getPersonalNummer());
			break;
			case Constants.AZV.USERNAME_INDEX_TABLE:
			columnText = azv.getUserName();
			break;
			case Constants.AZV.NACHNAME_INDEX_TABLE:
			columnText = azv.getNachname();
			break;
			case Constants.AZV.TEAM_INDEX_TABLE:
			columnText = azv.getTeam();
			break;
			case Constants.AZV.BERICHTSMONAT_INDEX_TABLE:
			columnText = azv.getBerichtsMonatAsString() + " " + azv.getBerichtsJahrAsString();
			break;
			case Constants.AZV.KOSTENSTELLE_INDEX_TABLE:
			columnText = (azv.getKostenstelle() != null) ? azv.getKostenstelle() + ": " + azv.getKostenstellenBeschreibung() : "";
			break;
			case Constants.AZV.KOSTENTRAEGER_INDEX_TABLE:
			columnText = (azv.getKostentraeger() != null) ? azv.getKostentraeger() + ": " + azv.getKostenTraegerBeschreibung() : "";
			break;
			case Constants.AZV.PROZENTANTEIL_INDEX_TABLE:
			columnText = String.valueOf(azv.getProzentanteil()) + " %";
			break;
			default:
				break;
			}
		}
		
	return columnText;
	}

	@Override
	public Color getForeground(Object element, int columnIndex)
	{
	Color cellColor = null;
		
		if (element != null && element instanceof AZVDatensatz)
		{
		AZVDatensatz azv = (AZVDatensatz) element;	
			if (azv.isMitarbeiterChecked())
			{
			cellColor = (azv.existsAZVDatensatz()) ? Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED) : Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
			}
		}
	return cellColor;	
	}

	@Override
	public Color getBackground(Object element, int columnIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
