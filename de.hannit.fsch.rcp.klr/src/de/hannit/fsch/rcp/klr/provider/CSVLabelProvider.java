package de.hannit.fsch.rcp.klr.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class CSVLabelProvider implements ITableLabelProvider 
{
private String[] fields = null;
private String label = null;
private int lineCount = 1;

	public CSVLabelProvider() {	}

	@Override
	public void addListener(ILabelProviderListener listener) {}

	@Override
	public void dispose() {	}

	@Override
	public boolean isLabelProperty(Object element, String property) {return false;}

	@Override
	public void removeListener(ILabelProviderListener listener) {}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {return null;}

	@Override
	public String getColumnText(Object element, int columnIndex) 
	{
		switch (columnIndex) 
		{
		case 0:
		label = String.valueOf(lineCount);
		lineCount++;
		break;

		default:
		fields = (String[]) element;
		label = fields[columnIndex];
		break;
			
		}
	return label;
	}

}
