package de.hannit.fsch.rcp.klr.handler.azv;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.widgets.TreeItem;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AddAZVRowHandler
{
@Inject IEventBroker broker;
private Mitarbeiter selectedMitarbeiter = null;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.TREESELECTION_MITARBEITER) TreeItem incoming)
	{
		if (incoming.getData() instanceof Mitarbeiter)
		{
		selectedMitarbeiter = (Mitarbeiter) incoming.getData(); 	
		}
	}	

	@Execute
	public void execute() 
	{
		if (selectedMitarbeiter != null)
		{
		Arbeitszeitanteil azv = selectedMitarbeiter.getAzvMonat().get(selectedMitarbeiter.getAzvMonat().firstKey());
		Arbeitszeitanteil addRow = new Arbeitszeitanteil();
		addRow.setID(AppConstants.AZV_ADDROW);
		addRow.setPersonalNummer(selectedMitarbeiter.getPersonalNR());
		addRow.setITeam(selectedMitarbeiter.getTeamNR());
		addRow.setBerichtsMonat(azv.getBerichtsMonat());
		broker.send(Topics.AZV_ADDROW, addRow);
		}
		
	}

	@CanExecute
	public boolean canExecute() 
	{
	return true;
	}
}
