package de.hannit.fsch.rcp.klr.parts;

import java.util.TreeMap;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.kostenrechnung.KostenTraeger;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class KTREditingSupport extends EditingSupport
{
private final TableViewer viewer;
private TreeMap<Integer, KostenTraeger> ktr = null;
private IEventBroker broker;

	public KTREditingSupport(TableViewer viewer, TreeMap<Integer, KostenTraeger> ktr, IEventBroker broker)
	{
	super(viewer);
	this.viewer = viewer;
	this.ktr = ktr;
	this.broker = broker;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
	return new ComboBoxCellEditor(viewer.getTable(), getComboValuesKostenTraeger());
	}

	@Override
	protected boolean canEdit(Object element)
	{
	return true;
	}

	@Override
	protected Object getValue(Object element)
	{
	int index = -1;	
	Arbeitszeitanteil azv = (Arbeitszeitanteil) element;
	String strKTR = azv.getKostentraeger();
	KostenTraeger k = null;
	
		for (Integer key : ktr.keySet())
		{
		k = ktr.get(key);	
			if (k.getBezeichnung().equalsIgnoreCase(strKTR))
			{
			index = key;	
			}
		}
	return index;
	}

	@Override
	protected void setValue(Object element, Object value)
	{
	KostenTraeger newKTR = null;	
	String oldValue = null;
	String newValue = null;
		if (element instanceof Arbeitszeitanteil)
		{
		Arbeitszeitanteil azv = (Arbeitszeitanteil) element;
		oldValue = azv.getKostentraeger();
		newKTR = ktr.get(value);
		newValue = newKTR.getBezeichnung();
		
			if (oldValue == null)
			{
			azv.setKostentraeger(newKTR.getBezeichnung());
			azv.setKostenTraegerBezeichnung(newKTR.getBeschreibung());
			azv.setKostenstelle(null);
			azv.setKostenStelleBezeichnung(null);
			broker.send(Topics.AZV_EDITED, azv);
			viewer.update(element, null);
			}
			else
			{
				if (! oldValue.equalsIgnoreCase(newValue))
				{
				azv.setKostentraeger(newKTR.getBezeichnung());
				azv.setKostenTraegerBezeichnung(newKTR.getBeschreibung());
				azv.setKostenstelle(null);
				azv.setKostenStelleBezeichnung(null);				
				broker.send(Topics.AZV_EDITED, azv);
				viewer.update(element, null);
				}
			}
		
		/*
		MessageBox mBox = new MessageBox(viewer.getTable().getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		mBox.setText("Änderungen speichern ?");
		mBox.setMessage("Sollen die Änderungen für die AZV-Meldung mit der ID: " + azv.getID() + " in der Datenbank gespeichert werden ?");
		
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
	
	public String[] getComboValuesKostenTraeger()
	{
	String[] result;
		if (this.ktr != null)
		{
		result = new String[this.ktr.size()];	
			for (int i = 0; i < this.ktr.size(); i++)
			{
			result[i] = ktr.get(i).getBezeichnung() + ": " + ktr.get(i).getBeschreibung();
			}
		}
		else
		{
		result = new String[]{"error"};	
		}
	return result;
	}

}
