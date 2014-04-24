 
package de.hannit.fsch.rcp.klr.handler.database;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.Constants;
import de.hannit.fsch.klr.model.azv.AZVDatensatz;
import de.hannit.fsch.klr.model.azv.Arbeitszeitanteil;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class LogaInsertHandler 
{
@Inject IEventBroker broker;	
@Inject EPartService partService;
@Inject ESelectionService selectionService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private LoGaDatei logaDatei = null;	

	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.LOGA_DATEN) LoGaDatei logaDatei)
	{
	this.logaDatei = logaDatei;	
	log.info("Empfange LoGa-Daten (" + logaDatei.getDaten().size() + " Datensätze)", this.getClass().getName() + ".handleEvent()");
	}	

	@Execute
	public void execute() 
	{
	SQLException e = null;
	int insertCount = 0;
	int errorCount = 0;
	String plugin = this.getClass().getName() + ".execute()";
	
		for (LoGaDatensatz ds : logaDatei.getDaten().values())
		{
			if (ds.getTarifGruppe().equalsIgnoreCase(Constants.Loga.TARIFGRUPPE_AUZUBIS))
			{
			processAuszubildende(ds);	
			}

		e = dataService.setLoGaDaten(ds);	
			if (e == null)
			{
			// log.confirm("Loga Daten für Personalnummer: " + ds.getPersonalNummer() + " erfolgreich in der Datenbank gespeichert", this.getClass().getName() + ".execute()");	
			insertCount++;
			}
			else 
			{
			log.error("SQLException beim Loga-Import von Datensatz: " + ds.getSource(),	plugin, e);	
			errorCount++;
			}
		}
		
		if (errorCount == 0)
		{
		logaDatei.setSaved(true);
		broker.send(Topics.LOGA_DATEN, logaDatei);
		log.confirm(insertCount + " Loga-Datensätze erfolgreich in die Datenbank eingefügt. Versende LoGa-Datei mit Flag saved=true.", plugin);	
		}
		else
		{
		log.warn("Beim Import der Loga-Daten in die Datenbank sind " + errorCount + " Fehler aufgetreten.", plugin);	
		}
	}
	
	/*
	 * Auszubildende werden gesondert behandelt:
	 * - Sie werden Team 6 zugeordnet
	 * - Es wird eine Standard AZV-Meldung mit 100% Ausbildung generiert, sofern nicht vorhanden
	 */
	private void processAuszubildende(LoGaDatensatz ds)
	{
	SQLException e = null;	
	String plugin = this.getClass().getName() + "processAuszubildende(LoGaDatensatz ds)";
	
		// Wenn der Auszubildende noch nicht in den Teams erfasst wurde:
		if (! dataService.existsTeammitgliedschaft(ds.getPersonalNummer()))
		{
		e =	dataService.setTeammitgliedschaft(ds.getPersonalNummer(), AppConstants.INTEGER_TEAM_AUSZUBILDENDE, ds.getAbrechnungsMonatSQL());	
			if (e == null)
			{
			log.confirm("Auszubildender: " + ds.getPersonalNummer() + " wurde automatisch in der Tabelle Teammitgliedschaften gespeichert", plugin);	
			}
			else 
			{
			log.error("SQLException beim automatischem Speichern der Teammitgliedschaft für Datensatz: " + ds.getSource(),	plugin, e);	
			}
		}
	// Zusätzlich wird eine Standard AZV-Meldung generiert, sofern noch keine gespeichert ist:
	ArrayList<Arbeitszeitanteil> arbeitszeitAnteile = dataService.getArbeitszeitanteile(ds.getPersonalNummer(), ds.getAbrechnungsMonatSQL());
		if (arbeitszeitAnteile.isEmpty())
		{
		AZVDatensatz azv = new AZVDatensatz();
		azv.setPersonalNummer(ds.getPersonalNummer());
		azv.setTeam(AppConstants.TEAM1);
		azv.setBerichtsMonat(ds.getAbrechnungsMonat());
		azv.setKostenstelle(AppConstants.KOSTENSTELLE_AUSBILDUNG);
		azv.setProzentanteil(100);
		
		e =	dataService.setAZVDaten(azv);	
			if (e == null)
			{
			log.confirm("Standard AZV-Meldung für Auszubildenden: " + ds.getPersonalNummer() + " wurde automatisch gespeichert", plugin);	
			}
			else 
			{
			log.error("SQLException beim automatischem Speichern der Standard AZV-Meldung für Datensatz: " + ds.getSource(),	plugin, e);	
			}		
		}
	}

	@CanExecute
	public boolean canExecute() 
	{
	boolean ready = false;
	
		if (logaDatei.isChecked() && ! logaDatei.hasErrors() && !logaDatei.isSaved())
		{
		ready = true;
		}
	return ready;
	}
		
}