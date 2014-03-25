package de.hannit.fsch.rcp.klr.provider;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.hannit.fsch.klr.model.mitarbeiter.Tarifgruppe;

public class TarifgruppenLabelProvider implements ITableLabelProvider
{
private String label;
private Tarifgruppe tarifgruppe;

	public TarifgruppenLabelProvider()
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
	tarifgruppe =  (Tarifgruppe) element;

		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(tarifgruppe.getTarifGruppe());
		break;
		
		case 1:
		label = NumberFormat.getCurrencyInstance().format((tarifgruppe.getSummeTarifgruppe()));
		break;
		
		case 2:
		label = String.valueOf(tarifgruppe.getSummeStellen());
		break;
		
		case 3:
		label = NumberFormat.getCurrencyInstance().format((tarifgruppe.getVollzeitAequivalent()));
		break;
		
		default:
		label = "ERROR";
		break;
			
		}
	return label;
	}
}
