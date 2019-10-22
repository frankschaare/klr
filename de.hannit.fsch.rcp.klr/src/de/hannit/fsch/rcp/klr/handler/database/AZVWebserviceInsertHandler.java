 
package de.hannit.fsch.rcp.klr.handler.database;

import java.sql.Date;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;

import com.ibm.icu.util.Calendar;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AZVWebserviceInsertHandler 
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private AZVDaten azvDaten = null;	

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_WEBSERVICE) AZVDaten azvDaten)
	{
	this.azvDaten = azvDaten;	
	}	

	@Execute
	public void execute() 
	{
	SQLException e = null;
	int insertCount = 0;
	int errorCount = 0;
	String plugin = this.getClass().getName() + ".execute()";
	
	/*
	 * Update zum 01.01.2018:
	 * Da die Teammitgliedschaft nicht mehr vom AZV-Webservice ausgelesen wird,
	 * wird diese Funktion nicht mehr aufgerufen
	 * 
	 * setTeammitgliedschaften();
	 */

		
		for (AZVDatensatz ds : azvDaten.getAzvMeldungen())
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
		/*
		e =  dataService.setDatenimport(azvDaten.getName(), azvDaten.getWebServiceIP(), insertCount, azvDaten.getBerichtsMonatSQL(), "AZV-Webservice");
			if (e == null)
			{
			log.confirm("AZV-Daten aus OS/ECM Webservice an IP: " + azvDaten.getWebServiceIP() + " erfolgreich in der Tabelle Datenimporte gespeichert", this.getClass().getName() + ".execute()");	
			}
			else 
			{
			log.error("SQLException beim Sichern von Datenimport: " + azvDaten.getName(),	plugin, e);	
			}
		 */
		}
		else
		{
		log.warn("Beim Import der AZV-Daten in die Datenbank sind " + errorCount + " Fehler aufgetreten.", plugin);	
		}
	}
	
	
	private void setTeammitgliedschaften()
	{
	SQLException e = null;	
	String plugin = this.getClass().getName() + ".setTeammitgliedschaften()";
	int aktuellesTeam = -1;
	int gespeichertesTeam = -1;
	
		for (Integer pnr : azvDaten.getTeamMitglieder().keySet())
		{
		aktuellesTeam = azvDaten.getTeamMitglieder().get(pnr);	
		gespeichertesTeam = dataService.getAktuellesTeam(pnr);	
		
			if (gespeichertesTeam == - 1) // Personalnummer nicht vorhanden. Neue Teammitgliedschaft einfügen
			{
			e = dataService.setTeammitgliedschaft(pnr, aktuellesTeam, azvDaten.getBerichtsMonatSQL());
				if (e != null)
				{
				log.error("SQLException beim Speichern der Teammitgliedschaft für Personalnummer: " + pnr, plugin, e);	
				}
				else
				{
				log.confirm("Teammitgliedschaft für Personalnummer " + pnr + " erfolgreich in der Datenbank gespeichert", plugin);
				}
			}
			else
			{	
				// Neue Teaminformation. Endatum (Vormonat) im vorhandenen Datensatz setzten und neuen Datensatz einfügen
				if (aktuellesTeam != gespeichertesTeam)
				{
				Calendar cal = Calendar.getInstance();
				cal.setTime(azvDaten.getBerichtsMonatSQL());
				cal.add(Calendar.DAY_OF_MONTH, - 1);
				java.sql.Date endDatum = new Date(cal.getTimeInMillis());
				
				e = dataService.updateTeammitgliedschaft(pnr, gespeichertesTeam, aktuellesTeam, azvDaten.getBerichtsMonatSQL(), endDatum);
					if (e != null)
					{
					log.error("SQLException beim Update der Teammitgliedschaft für Personalnummer: " + pnr, plugin, e);
					}
					else
					{
					log.confirm("Teammitgliedschaft für Personalnummer" + pnr + " erfolgreich aktualisiert. Neue Teamnummer ist Team " + aktuellesTeam, plugin);
					}
					
				}
			}
		}
		
	}

	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (azvDaten.isChecked() && ! azvDaten.hasErrors() && ! azvDaten.azvMeldungenVorhanden())
		{
		ready = true;
		}
	return ready;
	}
		
}