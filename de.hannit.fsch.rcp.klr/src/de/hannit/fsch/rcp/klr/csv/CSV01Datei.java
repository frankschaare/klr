/**
 * 
 */
package de.hannit.fsch.rcp.klr.csv;

import java.io.File;
import java.net.URI;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author fsch
 *
 */
public class CSV01Datei extends CSVDatei implements ITableLabelProvider, ITableColorProvider
{
private static final long serialVersionUID = -1532024416411533051L;

public static final String ZELLE2_NEHME = "0";
public static final String ZELLE2_GEBE = "1";
public static final String ZELLE4_NEHME = "1100100";
public static final String ZELLE4_GEBE = "1110100";
public static final String ZELLE5_PRÄFIX = "UML-";
public static final String ZELLE6_PRÄFIX = "AZV ";
public static final String ENTLASTUNGSKONTO = "0400";
//public static final String PATH_PRÄFIX = "\\\\RegionHannover.de\\daten\\hannit\\Rechnungswesen AöR\\KLR\\Arbeitszeitverteilung\\Reports\\";
public static final String PATH_SUFFIX = "\\CSV\\";
public static final String DATEINAME_PRÄFIX = "01_CSV_Entlastung 0400 auf andere KST";
public static final String DATEINAME_SUFFIX = ".csv";	

	/**
	 * @param arg0
	 */
	public CSV01Datei(String strPath)
	{
	super(strPath, (CSV01Datei.DATEINAME_PRÄFIX + CSV01Datei.DATEINAME_SUFFIX));
	}

	/**
	 * @param arg0
	 */
	public CSV01Datei(URI arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSV01Datei(String arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public CSV01Datei(File arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
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
		if (element instanceof String)
		{
		String line = (String) element;	
		String[] parts = line.split(CSVDatei.DEFAULT_DELIMITER);	
		return parts[columnIndex];
		}
		else
		{
		return "";	
		}	
	
	}

	@Override
	public Color getForeground(Object element, int columnIndex)
	{
	Color cellColor = null;
	
		if (element instanceof String)
		{
		String line = (String) element;	
		String[] parts = line.split(CSVDatei.DEFAULT_DELIMITER);	
		
			if (getLines() != null && getLines().contains(line))
			{
			cellColor = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);	
			}
			else
			{
				try
				{
				Double betrag = Double.parseDouble(parts[(parts.length - 1)].replace(",", "."));
				cellColor = (betrag > 0) ? Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN) : Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
				}
				catch (NumberFormatException e)
				{
				e.printStackTrace();
				}
			}
		}
	return cellColor;	
	}

	@Override
	public Color getBackground(Object element, int columnIndex)
	{
	return null;
	}

}
