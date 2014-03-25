package de.hannit.fsch.rcp.klr.provider;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.hannit.fsch.common.Dezimalformate;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;

public class GemeinkostenLabelProvider implements ITableLabelProvider
{
private String label;

	public GemeinkostenLabelProvider()
	{
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
	Arbeitszeitanteil azv = (Arbeitszeitanteil) element;
	
		switch (columnIndex) 
		{
		case 0:
		label = azv.getKostentraeger();
		break;
		
		case 1:
		label = azv.getKostenTraegerBezeichnung();
		break;
		
		case 2:
		label = Dezimalformate.KOMMAZAHL.format(azv.getProzentanteilGemeinkosten());
		break;
		
		case 3:
		label = NumberFormat.getCurrencyInstance().format(azv.getAnteilGemeinkosten());
		break;
				
		default:
		label = " ";
		break;
			
		}
	return label;
	}

}
