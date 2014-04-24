 
package de.hannit.fsch.rcp.klr.handler.database;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.rcp.klr.azv.AZVDatei;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AZVInsertHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private AZVDatei azvDatei = null;	

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_DATEN) AZVDatei azvDatei)
	{
	this.azvDatei = azvDatei;	
	log.info("Empfange LoGa-Daten (" + azvDatei.getDaten().size() + " Datensätze)", this.getClass().getName() + ".handleEvent()");
	}	

	@Execute
	public void execute() 
	{
	SQLException e = null;
	int insertCount = 0;
	int errorCount = 0;
	String plugin = this.getClass().getName() + ".execute()";
	
		for (AZVDatensatz ds : azvDatei.getDaten().values())
		{
			// Schritt 1: Kostenstelle / Kostenträger prüfen
			if (ds.getKostenstelle() != null)
			{
				if (! dataService.existsKostenstelle(ds.getKostenstelle()))
				{
				e = dataService.setKostenstelle(ds.getKostenstelle(), ds.getKostenstellenBeschreibung());
					if (e != null){log.error("SQLException beim der Kostenstelle: " + ds.getKostenstelle() + " (" +  ds.getSource() + ")",	plugin, e);}	
				}
			}
			else
			{
				if (! dataService.existsKostentraeger(ds.getKostentraeger()))
				{
				e = dataService.setKostentraeger(ds.getKostentraeger(), ds.getKostenTraegerBeschreibung());
					if (e != null){log.error("SQLException beim des Kostenträgers: " + ds.getKostentraeger() + " (" +  ds.getSource() + ")",	plugin, e);}
				}
			}
		e = dataService.setAZVDaten(ds);	
			if (e == null)
			{
			// log.confirm("Loga Daten für Personalnummer: " + ds.getPersonalNummer() + " erfolgreich in der Datenbank gespeichert", this.getClass().getName() + ".execute()");	
			insertCount++;
			}
			else 
			{
			log.error("SQLException beim AZV-Import von Datensatz: " + ds.getSource(),	plugin, e);	
			errorCount++;
			}
		}
		
		if (errorCount == 0)
		{
		log.confirm(insertCount + " AZV-Datensätze erfolgreich in die Datenbank eingefügt.", plugin);	
		e =  dataService.setDatenimport(azvDatei.getName(), azvDatei.getPath(), insertCount, azvDatei.getBerichtsMonatSQL(), "AZV");
			if (e == null)
			{
			log.confirm("AZV-Datei : " + azvDatei.getPath() + " erfolgreich in der Tabelle Datenimporte gespeichert", this.getClass().getName() + ".execute()");	
			}
			else 
			{
			log.error("SQLException beim Sichern von Datenimport: " + azvDatei.getPath(),	plugin, e);	
			}
		}
		else
		{
		log.warn("Beim Import der AZV-Daten in die Datenbank sind " + errorCount + " Fehler aufgetreten.", plugin);	
		}
	}
	
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (azvDatei.isChecked() && ! azvDatei.hasErrors())
		{
		ready = true;
		}
	return ready;
	}
		
}