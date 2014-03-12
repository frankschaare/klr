/**
 * 
 */
package de.hannit.fsch.rcp.klr.azv;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.csv.azv.AZVDatensatz;

/**
 * @author fsch
 *
 */
public class AZVDaten implements ITableLabelProvider
{
private ArrayList<AZVDatensatz> azvMeldungen = null;
private String webServiceIP = null;
private String requestedMonth = null;
private String requestedYear = null;
private boolean requestComplete = false;
private boolean checked = false;
private boolean errors = false;
private String columnText = null;
private java.sql.Date berichtsMonatSQL;

private URL url = null;
private TreeMap<String, Image> imageCache = null;

	/**
	 * 
	 */
	public AZVDaten()
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

	public ArrayList<AZVDatensatz> getAzvMeldungen()
	{
	return azvMeldungen;
	}

	public String getWebServiceIP() {return webServiceIP;}
	public void setWebServiceIP(String webServiceIP){this.webServiceIP = webServiceIP;}

	public void setAzvMeldungen(ArrayList<AZVDatensatz> azvMeldungen)
	{
	this.azvMeldungen = azvMeldungen;
	}

	public boolean isChecked(){return checked;}
	public void setChecked(boolean checked){this.checked = checked;}
	
	public boolean hasErrors(){return errors;}
	public void setErrors(boolean errors){this.errors = errors;}
	
	public boolean isRequestComplete() {return requestComplete;}
	public void setRequestComplete(boolean requestComplete)	{this.requestComplete = requestComplete;}

	public String getRequestedMonth(){return requestedMonth;}
	public void setRequestedMonth(String requestedMonth){this.requestedMonth = requestedMonth;}

	public String getRequestedYear(){return requestedYear;}
	public void setRequestedYear(String requestedYear)
	{
	this.requestedYear = requestedYear;
		if (this.requestedMonth != null)
		{
		setBerichtsMonatSQL();	
		}
	}

	public String getName(){return "OS/ECM Webservice an IP: " + getWebServiceIP() + " " + requestedMonth + " " + requestedYear;}
	
	public java.sql.Date getBerichtsMonatSQL() {return (berichtsMonatSQL != null) ? berichtsMonatSQL : getAzvMeldungen().get(0).getBerichtsMonatSQL();}

	//TODO: prüfen, ob alle Monate übereinstimmen
	public void setBerichtsMonatSQL()
	{
		if (requestedMonth != null && requestedYear != null)
		{
			try
			{
			java.util.Date date = Datumsformate.MONATLANG_JAHR.parse(requestedMonth + " " + requestedYear);
			this.berichtsMonatSQL = new java.sql.Date(date.getTime());
			}
			catch (ParseException e)
			{
			e.printStackTrace();
			}	
		}	
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
	AZVDatensatz datenSatz = null;
	String key = null;	
	
		switch (columnIndex)
		{
		case CSVConstants.AZV.PERSONALNUMMER_INDEX_TABLE:
			
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
			case CSVConstants.AZV.PERSONALNUMMER_INDEX_TABLE:
			columnText = String.valueOf(azv.getPersonalNummer());
			break;
			case CSVConstants.AZV.USERNAME_INDEX_TABLE:
			columnText = azv.getUserName();
			break;
			case CSVConstants.AZV.NACHNAME_INDEX_TABLE:
			columnText = azv.getNachname();
			break;
			case CSVConstants.AZV.TEAM_INDEX_TABLE:
			columnText = azv.getTeam();
			break;
			case CSVConstants.AZV.BERICHTSMONAT_INDEX_TABLE:
			columnText = azv.getBerichtsMonatAsString() + " " + azv.getBerichtsJahrAsString();
			break;
			case CSVConstants.AZV.KOSTENSTELLE_INDEX_TABLE:
			columnText = (azv.getKostenstelle() != null) ? azv.getKostenstelle() + ": " + azv.getKostenstellenBeschreibung() : "";
			break;
			case CSVConstants.AZV.KOSTENTRAEGER_INDEX_TABLE:
			columnText = (azv.getKostentraeger() != null) ? azv.getKostentraeger() + ": " + azv.getKostenTraegerBeschreibung() : "";
			break;
			case CSVConstants.AZV.PROZENTANTEIL_INDEX_TABLE:
			columnText = String.valueOf(azv.getProzentanteil()) + " %";
			break;
			default:
				break;
			}
		}
		
	return columnText;
	}

}
