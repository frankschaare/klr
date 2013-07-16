package de.hannit.fsch.rcp.klr.azv;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.common.csv.azv.AZVDatensatz;

public class AZVDatei extends CSVDatei implements ITableLabelProvider
{
private TreeMap<Integer, AZVDatensatz> daten;
/*
 * Die gleiche TreeMap noch einmal zum ermitteln, welche Personalnummer AZV gemeldet hat.
 */
private TreeMap<Integer, AZVDatensatz> datenDistinct;
private AZVDatensatz datenSatz = null;
private SimpleDateFormat format = new SimpleDateFormat(CSVConstants.AZV.BERICHTSMONAT_DATUMSFORMAT_CSV);
private URL url = null;	
private String label = null;	

	public AZVDatei(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public AZVDatei(URI arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public AZVDatei(String arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public AZVDatei(File arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

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
	
	/*
	 * Die CSV-Daten werden so genau wie möglich geprüft
	 */
	private AZVDatensatz split(String line)
	{
	datenSatz = new AZVDatensatz();	
	datenSatz.setSource(line);
	
	String[] parts = line.split(delimiter);
		// PNr.
		try
		{
		int pnr = Integer.parseInt(parts[CSVConstants.AZV.PERSONALNUMMER_INDEX_CSV]);	
		datenSatz.setPersonalNummer(pnr);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		datenSatz.setPersonalNummer(999999);
		}
		
		// Berichtsmonat und Jahr
		try
		{
		Date berichtsMonat = format.parse(parts[CSVConstants.AZV.BERICHTSMONAT_INDEX_CSV] + " " + parts[CSVConstants.AZV.BERICHTSJAHR_INDEX_CSV]);	
		datenSatz.setBerichtsMonat(berichtsMonat);
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
		// TODO Auto-generated method stub
		return null;
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
		
		case CSVConstants.AZV.BERICHTSMONAT_INDEX_TABLE:
		label = format.format(datenSatz.getBerichtsMonat());
		break;	
		
		case CSVConstants.AZV.KOSTENSTELLE_INDEX_TABLE:
		label = datenSatz.getKostenstelle();
		break;		
		
		case CSVConstants.AZV.KOSTENTRAEGER_INDEX_TABLE:
		label = datenSatz.getKostentraeger();
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
