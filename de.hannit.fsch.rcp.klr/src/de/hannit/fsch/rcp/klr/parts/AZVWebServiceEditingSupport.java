package de.hannit.fsch.rcp.klr.parts;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.hannit.fsch.klr.model.azv.AZVDatensatz;

public class AZVWebServiceEditingSupport extends EditingSupport
{
private final TableViewer tableViewer;	
private String[] pnrs;

	public AZVWebServiceEditingSupport(TableViewer viewer)
	{
	super(viewer);
	this.tableViewer = viewer; 
	}

	public void setPnrs(String[] incoming)
	{
	this.pnrs = incoming;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
    return new ComboBoxCellEditor(tableViewer.getTable(), pnrs);
	}

	@Override
	protected boolean canEdit(Object element)
	{
	return true;
	}

	@Override
	protected Object getValue(Object element)
	{
	AZVDatensatz azv = (AZVDatensatz) element;
	return azv.getPersonalNummer();
	}

	@Override
	protected void setValue(Object element, Object value)
	{
	AZVDatensatz azv = (AZVDatensatz) element;
	int index = (int) value;
	int pnr = Integer.parseInt(pnrs[index]);
	azv.setPersonalNummer(pnr);
	tableViewer.update(element, null);
	}

}
