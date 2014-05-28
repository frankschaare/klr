package de.hannit.fsch.rcp.klr.parts;

import java.util.TreeMap;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.kostenrechnung.KostenStelle;
import de.hannit.fsch.klr.model.kostenrechnung.KostenTraeger;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class KSTEditingSupport extends EditingSupport
{
private final TableViewer viewer;
private TreeMap<Integer, KostenStelle> kst = null;
private IEventBroker broker;

	public KSTEditingSupport(TableViewer viewer, TreeMap<Integer, KostenStelle> kst, IEventBroker broker)
	{
	super(viewer);
	this.viewer = viewer;
	this.kst = kst;
	this.broker = broker;
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
	return new ComboBoxCellEditor(viewer.getTable(), getComboValuesKostenStellen());
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
	String strKST = azv.getKostenstelle();
	KostenStelle k = null;
	
		for (Integer key : kst.keySet())
		{
		k = kst.get(key);	
			if (k.getBezeichnung().equalsIgnoreCase(strKST))
			{
			index = key;	
			}
		}
	return index;
	}

	@Override
	protected void setValue(Object element, Object value)
	{
	KostenStelle newKST = null;	
	String oldValue = null;
	String newValue = null;
		if (element instanceof Arbeitszeitanteil)
		{
		Arbeitszeitanteil azv = (Arbeitszeitanteil) element;
		oldValue = azv.getKostenstelle();
		newKST = kst.get(value);
		newValue = newKST.getBezeichnung();
		
			if (oldValue == null)
			{
			azv.setKostenstelle(newKST.getBezeichnung());
			azv.setKostenStelleBezeichnung(newKST.getBeschreibung());
			azv.setKostentraeger(null);
			azv.setKostenTraegerBezeichnung(null);
			broker.send(Topics.AZV_EDITED, azv);
			viewer.update(element, null);
			}
			else
			{
				if (! oldValue.equalsIgnoreCase(newValue))
				{
				azv.setKostenstelle(newKST.getBezeichnung());
				azv.setKostenStelleBezeichnung(newKST.getBeschreibung());
				azv.setKostentraeger(null);
				azv.setKostenTraegerBezeichnung(null);	
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
	
	public String[] getComboValuesKostenStellen()
	{
	String[] result;
		if (this.kst != null)
		{
		result = new String[this.kst.size()];	
			for (int i = 0; i < this.kst.size(); i++)
			{
			result[i] = kst.get(i).getBezeichnung() + ": " + kst.get(i).getBeschreibung();
			}
		}
		else
		{
		result = new String[]{"error"};	
		}
	return result;
	}
}