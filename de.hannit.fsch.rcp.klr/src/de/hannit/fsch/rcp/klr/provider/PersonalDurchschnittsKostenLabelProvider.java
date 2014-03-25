package de.hannit.fsch.rcp.klr.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.hannit.fsch.klr.model.mitarbeiter.SummenZeile;


public class PersonalDurchschnittsKostenLabelProvider implements ITableLabelProvider
{
private String label;	

	public PersonalDurchschnittsKostenLabelProvider()
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
	SummenZeile sz = (SummenZeile) element;
	
		switch (columnIndex) 
		{
		case 0:
		label = sz.getColumn0();
		break;
		
		case 1:
		label = sz.getColumn1();
		break;
		
		case 2:
		label = sz.getColumn2();
		break;
		
		case 3:
		label = sz.getColumn3();
		break;
		
		case 4:
		label = sz.getColumn4();
		break;		
		
		case 5:
		label = sz.getColumn5();
		break;		
		
		case 6:
		label = sz.getColumn6();
		break;		

		case 7:
		label = sz.getColumn7();
		break;		
		
		case 8:
		label = sz.getColumn8();
		break;				
		
		default:
		label = " ";
		break;
			
		}
	return label;
	}


}
