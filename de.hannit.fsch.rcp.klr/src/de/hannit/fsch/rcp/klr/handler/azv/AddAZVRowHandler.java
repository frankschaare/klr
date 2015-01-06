package de.hannit.fsch.rcp.klr.handler.azv;

import java.lang.invoke.SwitchPoint;

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
		Arbeitszeitanteil addRow = new Arbeitszeitanteil();
		addRow.setID(AppConstants.AZV_ADDROW);
		addRow.setPersonalNummer(selectedMitarbeiter.getPersonalNR());
		addRow.setITeam(selectedMitarbeiter.getTeamNR());
			switch (selectedMitarbeiter.getAzvMonat().size())
			{
			case 0: //Keine AZV Meldung vorhanden
			java.util.Date berichtsMonat = selectedMitarbeiter.getAbrechnungsMonat(); 	
			addRow.setBerichtsMonat(new java.sql.Date(berichtsMonat.getTime()));
				
				// Es wird versucht, eine sinnvolle Vorbelegung zu erreichern:
				switch (selectedMitarbeiter.getStatus())
				{
				case Mitarbeiter.STATUS_AUSZUBILDENDER:
				addRow.setKostenstelle(AppConstants.KOSTENSTELLE_AUSBILDUNG);
				addRow.setKostenStelleBezeichnung(AppConstants.KOSTENSTELLE_AUSBILDUNG_BESCHREIBUNG);
				addRow.setProzentanteil(100);
				break;

				// Für Mitarbeiter aus Team 5 wird der Service-Desk vorbelegt:
				default:
					switch (selectedMitarbeiter.getTeamNR())
					{
					case 5:
					addRow.setKostenstelle(AppConstants.KOSTENSTELLE_SERVICEDESK);
					addRow.setKostenStelleBezeichnung(AppConstants.KOSTENSTELLE_SERVICEDESK_BESCHREIBUNG);
					addRow.setProzentanteil(100);						
					break;
					default:
					break;
					}
				break;
				}
			break;

			default: // Mindestens eine AZV vorhanden
			Arbeitszeitanteil azv = selectedMitarbeiter.getAzvMonat().get(selectedMitarbeiter.getAzvMonat().firstKey());
			addRow.setBerichtsMonat(azv.getBerichtsMonat());
			break;
			}
		broker.send(Topics.AZV_ADDROW, addRow);
		}
		
	}

	@CanExecute
	public boolean canExecute() 
	{
	return true;
	}
}
