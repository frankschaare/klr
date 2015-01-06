package de.hannit.fsch.rcp.klr.handler.azv;

import java.sql.SQLException;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class SaveAZVChangesHandler
{
@Inject DataService dataService;	
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;

private TreeMap<String, Arbeitszeitanteil> changes = new TreeMap<>();
private String plugin  = this.getClass().getName() + ".handleEvent(Arbeitszeitanteil azv)";
// private int summeProzentAnteile = 0;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_EDITED) Arbeitszeitanteil incoming)
	{
		if (incoming instanceof Arbeitszeitanteil)
		{
		String id = incoming.getID() != null ? incoming.getID() : AppConstants.AZV_ADDROW;
			if (changes.containsKey(id))
			{
			changes.remove(id);
			changes.put(id, incoming);
			}
			else
			{
			changes.put(id, incoming);
			}
		log.warn("Tabelle Datenbankinhalte enthält " + changes.size() + " ungespeicherte AZV-Meldungen", plugin);
		
			// TODO: Summe vergleichen
			/*
			summeProzentAnteile = 0;
			for (Arbeitszeitanteil azv : changes.values())
			{
			summeProzentAnteile += azv.getProzentanteil();	
			}
			if (summeProzentAnteile != 100)
			{
			log.error("Summe Prozentanteile ist ungleich 100. Bitte vorhandene Daten vor dem Speichern anpassen !", plugin, null);	
			}
			else
			{
			log.confirm("Summe Prozentanteile ist gleich 100. Daten vor dem gespeichert werden.", plugin);	
			}
			*/
		}
	}	

	@Execute
	public void execute() 
	{
	SQLException e = dataService.saveAZVChanges(changes);
	plugin  = this.getClass().getName() + ".execute()";
		if (e != null)
		{
		log.error("Datenbankfehler beim Speichern der Änderungen. Es wurde ein Rollback durchgeführt", plugin, e);	
		}
		else
		{
		log.confirm("Änderungen wurden erfolgreich in der Datenbank gespeichert.", plugin);	
		}
	changes = new TreeMap<>();		
	}

	@CanExecute
	public boolean canExecute() 
	{
	boolean result = false;	
		switch (changes.size())
		{
		case 0: 		
		break;

		default:
		result = true;	
		break;
		}
		/*
		if (summeProzentAnteile != 100)
		{
		result = false;	
		}
		*/
	return result;
	}
}
