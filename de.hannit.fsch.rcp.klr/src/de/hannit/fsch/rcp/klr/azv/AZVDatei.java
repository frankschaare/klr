package de.hannit.fsch.rcp.klr.azv;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.csv.azv.AZVDatensatz;
import de.hannit.fsch.klr.dataservice.DataService;

public class AZVDatei extends CSVDatei implements ITableLabelProvider
{
@Inject DataService dataService;	
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;	
private TreeMap<Integer, AZVDatensatz> daten;
/*
 * Die gleiche TreeMap noch einmal zum ermitteln, welche Personalnummer AZV gemeldet hat.
 */
private TreeMap<Integer, AZVDatensatz> datenDistinct;
private AZVDatensatz datenSatz = null;
private SimpleDateFormat format = new SimpleDateFormat(CSVConstants.AZV.BERICHTSMONAT_DATUMSFORMAT_CSV);
private URL url = null;	
private String label = null;	
private boolean checked = false;
private java.sql.Date berichtsMonatSQL;

	public AZVDatei(String arg0)
	{
	super(arg0);
	}

	public AZVDatei(URI arg0)
	{
	super(arg0);
	}

	public AZVDatei(String arg0, String arg1)
	{
	super(arg0, arg1);
	}

	public AZVDatei(File arg0, String arg1)
	{
	super(arg0, arg1);
	}

	public boolean isChecked(){return checked;}
	public void setChecked(boolean checked){this.checked = checked;}

	@Override
	public void read()
	{
	super.read();
	daten = new TreeMap<Integer, AZVDatensatz>();
	datenDistinct = new TreeMap<Integer, AZVDatensatz>();
		
		lineCount = 1;
		for (String line : getLines())
		{
			switch (lineCount) 
			{
			// Erste Zeile wird nur verarbeitet wenn keine Kopfzeile vorhanden ist
			case 1:
				if (!hasHeader) 
				{
				datenSatz = split(line);
				daten.put(lineCount, datenSatz);	
				datenDistinct.put(datenSatz.getPersonalNummer(), datenSatz);
				}
			break;

			default:
			datenSatz = split(line);
			daten.put(lineCount, datenSatz);
			datenDistinct.put(datenSatz.getPersonalNummer(), datenSatz);
			break;
			}	
			lineCount++;
		}
	}
	
	public java.sql.Date getBerichtsMonatSQL()
	{
		return berichtsMonatSQL;
	}

	//TODO: prüfen, ob alle Monate übereinstimmen
	public void setBerichtsMonatSQL(java.sql.Date berichtsMonatSQL)
	{
	this.berichtsMonatSQL = berichtsMonatSQL;
	}

	/*
	 * Die CSV-Daten werden so genau wie möglich geprüft
	 */
	private AZVDatensatz split(String line)
	{
	int pnr	= 0;
	datenSatz = new AZVDatensatz();	
	datenSatz.setSource(line);
	
	String[] parts = line.split(delimiter);
		// PNr.
		try
		{
		pnr = Integer.parseInt(parts[CSVConstants.AZV.PERSONALNUMMER_INDEX_CSV]);	
		datenSatz.setPersonalNummer(pnr);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource() + ", versuche Personalnummer aus der Datenbank zu lesen.", this.getClass().getName() + ".split()", e);
		pnr = 999999;	
		datenSatz.setNachname(parts[CSVConstants.AZV.NACHNAME_INDEX_CSV]);
		datenSatz.setPersonalNummer(pnr);
		}

		// Team
		try
		{
		String strTeam = parts[CSVConstants.AZV.TEAM_INDEX_CSV];	
		datenSatz.setTeam(strTeam);
		}
		catch (Exception e)
		{
		e.printStackTrace();
		getLog().error("Exception beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}		
		
		// Berichtsmonat und Jahr
		try
		{
		Date berichtsMonat = format.parse(parts[CSVConstants.AZV.BERICHTSMONAT_INDEX_CSV] + " " + parts[CSVConstants.AZV.BERICHTSJAHR_INDEX_CSV]);	
		datenSatz.setBerichtsMonat(berichtsMonat);
		setBerichtsMonatSQL(datenSatz.getBerichtsMonatSQL());
		}
		catch (ParseException e)
		{
		e.printStackTrace();
		getLog().error("ParseException beim parsen des Berichtsmonats in Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}	
		
		// Kostenstelle
		try
		{
		String kostenstelle = parts[CSVConstants.AZV.KOSTENSTELLE_INDEX_CSV];	
		if (kostenstelle.length() > 0)
			{
			datenSatz.setKostenstelle(kostenstelle);
			}
		}
		catch (Exception e)
		{
		e.printStackTrace();
		getLog().error("Exception beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}	
		
		// Kostentraeger
		try
		{
		String kostenTraeger = parts[CSVConstants.AZV.KOSTENTRAEGER_INDEX_CSV];	
		if (kostenTraeger.length() > 0)
			{
			datenSatz.setKostentraeger(kostenTraeger);
			}
		}
		catch (Exception e)
		{
		e.printStackTrace();
		getLog().error("Exception beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}	
		
		// Prozentanteil
		try
		{
		int prozentAnteil = Integer.parseInt(parts[CSVConstants.AZV.PROZENTANTEIL_INDEX_CSV]);	
		datenSatz.setProzentanteil(prozentAnteil);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		datenSatz.setPersonalNummer(999999);
		}		
				
	return datenSatz;
	}	
	
	public TreeMap<Integer, AZVDatensatz> getDaten()
	{
		return daten;
	}

	public TreeMap<Integer, AZVDatensatz> getDatenDistinct()
	{
	return datenDistinct;
	}
	
	public void setDaten(TreeMap<Integer, AZVDatensatz> daten)
	{
		this.daten = daten;
	}

	@Override
	public void addListener(ILabelProviderListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

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

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		
		switch (columnIndex)
		{
		case CSVConstants.AZV.PERSONALNUMMER_INDEX_TABLE:
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			if (element instanceof AZVDatensatz) 
			{
			datenSatz =  (AZVDatensatz) element;	
			
				if (datenSatz.isMitarbeiterChecked())
				{
					if (datenSatz.existsMitarbeiter())
					{
						if (datenSatz.personalNummerNachgetragen())
						{
						url = FileLocator.find(bundle, new Path("icons/warn_tsk.gif"), null);	
						}
						else
						{
						url = FileLocator.find(bundle, new Path("icons/checked.gif"), null);
						}
					}
					else
					{
					url = FileLocator.find(bundle, new Path("icons/error_tsk.gif"), null);						
					}					
				ImageDescriptor image = ImageDescriptor.createFromURL(url);	
				return image.createImage();
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

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
	datenSatz =  (AZVDatensatz) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(lineCount);
		lineCount++;
		break;
		
		case CSVConstants.AZV.PERSONALNUMMER_INDEX_TABLE:
		label = String.valueOf(datenSatz.getPersonalNummer());
		break;
		
		case CSVConstants.AZV.TEAM_INDEX_TABLE:
		label = datenSatz.getTeam();
		break;		
		
		case CSVConstants.AZV.BERICHTSMONAT_INDEX_TABLE:
		label = format.format(datenSatz.getBerichtsMonat());
		break;	
		
		case CSVConstants.AZV.KOSTENSTELLE_INDEX_TABLE:
		label = datenSatz.getKostenstelle() == null ? null : datenSatz.getKostenstelle() + " - " + datenSatz.getKostenstellenBeschreibung();
		break;		
		
		case CSVConstants.AZV.KOSTENTRAEGER_INDEX_TABLE:
		label = datenSatz.getKostentraeger() == null ? null : datenSatz.getKostentraeger() + " - " + datenSatz.getKostenTraegerBeschreibung();
		break;	
		
		case CSVConstants.AZV.PROZENTANTEIL_INDEX_TABLE:
		label = String.valueOf(datenSatz.getProzentanteil());
		break;			
		
		default:
		label = "ERROR";
		break;
			
		}
	return label;
	}

}
