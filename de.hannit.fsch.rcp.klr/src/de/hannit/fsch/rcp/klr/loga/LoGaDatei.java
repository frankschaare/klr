/**
 * 
 */
package de.hannit.fsch.rcp.klr.loga;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;

/**
 * @author fsch
 *
 */
public class LoGaDatei extends CSVDatei implements ITableLabelProvider
{
private static final long serialVersionUID = -4808470669223797111L;

private String label = null;	

private TreeMap<Integer, LoGaDatensatz> daten;
private LoGaDatensatz datenSatz = null;
private SimpleDateFormat format = new SimpleDateFormat(CSVConstants.Loga.ABRECHNUNGSMONAT_DATUMSFORMAT_CSV);
private URL url = null;

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

	/*
	 * (non-Javadoc)
	 * @see de.hannit.fsch.common.CSVDatei#read()
	 */
	@Override
	public void read()
	{
	super.read();
	daten = new TreeMap<Integer, LoGaDatensatz>();
		
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
				daten.put(datenSatz.getPersonalNummer(), datenSatz);	
				}
			break;

			default:
			datenSatz = split(line);
			daten.put(datenSatz.getPersonalNummer(), datenSatz);	
			break;
			}	
			lineCount++;
		}
	}

	/*
	 * Die CSV-Daten werden so genau wie möglich geprüft
	 */
	private LoGaDatensatz split(String line)
	{
	datenSatz = new LoGaDatensatz();	
	datenSatz.setSource(line);
	
	String[] parts = line.split(delimiter);
		// PNr.
		try
		{
		int pnr = Integer.parseInt(parts[CSVConstants.Loga.PERSONALNUMMER_INDEX_CSV]);	
		datenSatz.setPersonalNummer(pnr);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}

		// Summe( Betrag )
		try
		{
		double brutto = Double.parseDouble(parts[CSVConstants.Loga.BRUTTO_INDEX_CSV].replace(",", "."));	
		datenSatz.setBrutto(brutto);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}
		
		// Abrechnungsmonat
		try
		{
		Date abrechnungsMonat = format.parse(parts[CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_CSV]);	
		datenSatz.setAbrechnungsMonat(abrechnungsMonat);
		}
		catch (ParseException e)
		{
		e.printStackTrace();
		getLog().error("ParseException beim parsen des Abrechnungsmonats in Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}		
		
		// Tarifgruppe
		try
		{
		String tarifGruppe = parts[CSVConstants.Loga.TARIFGRUPPE_INDEX_CSV];	
		datenSatz.setTarifGruppe(tarifGruppe);;
		}
		catch (Exception e)
		{
		e.printStackTrace();
		getLog().error("Exception beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}
		
		// Tarifstufe
		try
		{
		int tarifStufe = Integer.parseInt(parts[CSVConstants.Loga.TARIFSTUFE_INDEX_CSV]);	
		datenSatz.setTarifstufe(tarifStufe);
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}		
		
		// Stellenanteil
		try
		{
		double stellenAnteil = Double.parseDouble(parts[CSVConstants.Loga.STELLENNTEIL_INDEX_CSV].replace(",", "."));	
		datenSatz.setStellenAnteil(stellenAnteil);;
		}
		catch (NumberFormatException e)
		{
		e.printStackTrace();
		getLog().error("NumberFormatException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
		// e.printStackTrace();
		getLog().error("ArrayIndexOutOfBoundsException beim parsen der Zeile: " + datenSatz.getSource(), this.getClass().getName() + ".split()", e);
		datenSatz.setStellenAnteil(999999);
		}
		
	return datenSatz;
	}
	
	public TreeMap<Integer, LoGaDatensatz> getDaten()
	{
	return daten;
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
	
		switch (columnIndex)
		{
		case 1:
			Bundle bundle = FrameworkUtil.getBundle(this.getClass());
			if (element instanceof LoGaDatensatz) 
			{
			datenSatz =  (LoGaDatensatz) element;	
			
				if (datenSatz.mitarbeiterChecked())
				{
					if (datenSatz.existsMitarbeiter())
					{
						url = FileLocator.find(bundle, new Path("icons/checked.gif"), null);
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) 
	{
	datenSatz =  (LoGaDatensatz) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(lineCount);
		lineCount++;
		break;
		
		case CSVConstants.Loga.PERSONALNUMMER_INDEX_TABLE:
		label = String.valueOf(datenSatz.getPersonalNummer());
		break;
		
		case CSVConstants.Loga.BRUTTO_INDEX_TABLE:
		label = String.valueOf(datenSatz.getBrutto()).replace(".", ",") + " €";
		break;
		
		case CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_TABLE:
		label = format.format((datenSatz.getAbrechnungsMonat()));
		break;
		
		case CSVConstants.Loga.TARIFGRUPPE_INDEX_TABLE:
		label = datenSatz.getTarifGruppe();
		break;
		
		case CSVConstants.Loga.TARIFSTUFE_INDEX_TABLE:
		label = String.valueOf(datenSatz.getTarifstufe());
		break;		
		
		case CSVConstants.Loga.STELLENNTEIL_INDEX_TABLE:
			if (datenSatz.getStellenAnteil() > 1)
			{
			log.warn("Stellenanteil bei Personalnummer: " + datenSatz.getPersonalNummer() + " enthält ungültigen Wert !", this.getClass().getName() + ".ITableLabelProvider.getColumnText()");	
			label = "ERROR";
			}
			else
			{
			label = String.valueOf(datenSatz.getStellenAnteil());
			}
		break;		
		
		default:
		label = "ERROR";
		break;
			
		}
	return label;
	}

}
