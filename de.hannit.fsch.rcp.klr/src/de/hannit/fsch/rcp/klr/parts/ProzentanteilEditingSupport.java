package de.hannit.fsch.rcp.klr.parts;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class ProzentanteilEditingSupport extends EditingSupport
{
private IEventBroker broker;	
private final TableViewer viewer;
private final CellEditor editor;

	public ProzentanteilEditingSupport(TableViewer viewer, IEventBroker broker)
	{
	super(viewer);
	this.viewer = viewer;
	this.broker = broker;
	this.editor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
	return editor;
	}

	@Override
	protected boolean canEdit(Object element)
	{
	return true;
	}

	@Override
	protected Object getValue(Object element)
	{
	Arbeitszeitanteil azv = (Arbeitszeitanteil)element;
	return String.valueOf(azv.getProzentanteil());
	}

	@Override
	protected void setValue(Object element, Object value)
	{
	int newProzentAnteil = Integer.parseInt(String.valueOf(value));
	
		if (element instanceof Arbeitszeitanteil)
		{
		Arbeitszeitanteil azv = (Arbeitszeitanteil) element;	
			if (newProzentAnteil < 1 || newProzentAnteil > 100)
			{
			MessageBox errorBox = new MessageBox(viewer.getTable().getShell(), SWT.ICON_ERROR | SWT.OK);
			errorBox.setText("Fehlerhafte Eingabe");
			errorBox.setMessage("Sie müssen für den Prozentanteil eine Ganzzahl zwischen 1 und 100 eigeben !");
			errorBox.open();
			}
			else
			{
			azv.setProzentanteil(newProzentAnteil);
			broker.send(Topics.AZV_EDITED, azv);
			viewer.update(element, null);
			/*
			MessageBox mBox = new MessageBox(viewer.getTable().getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			mBox.setText("Änderungen speichern ?");
			mBox.setMessage("Sollen die Änderungen für die AZV-Meldung mit der ID: " + id + " in der Datenbank gespeichert werden ?");
			
				switch (mBox.open())
				{
				case SWT.YES:
				viewer.update(element, null);
				SQLException error = dataService.updateAZV(azv);
				break;
					
				default:
				break;
				}
			*/	
			}
		}
	}
}
