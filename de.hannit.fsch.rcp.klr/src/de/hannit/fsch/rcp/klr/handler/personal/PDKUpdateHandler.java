 
package de.hannit.fsch.rcp.klr.handler.personal;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.common.mitarbeiter.Team;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;

/**
 * Erstellt die CSV-Datei f�r die Entlastung der Kostenstelle 0400 auf andere Kostentr�ger
 * Im Gegensatz zur Datei 01, in der die Kostenstellen umgelegt werden, wird hier pro Monat eine Datei erstellt.
 * 
 * Zudem sind die Bezeichnungen der Konstanten etwas anders und es wird in der ersten Zeile keine Gesamtentlastung gebucht.
 * @author fsch
 * @since 07.02.2014
 *
 */
public class PDKUpdateHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private String plugin = this.getClass().getName();

private PersonalDurchschnittsKosten pdk = null;

	
	/*
	 * Der NavPart l�dt die Mitarbeiterliste f�r den aktuellen Monat inclusive aller AZV-Anteil
	 * Hieraus werden hier die Personaldurchschnittskosten gebildet.
	 */
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.PERSONALDURCHSCHNITTSKOSTEN) PersonalDurchschnittsKosten incoming)
	{
	this.pdk = incoming;
	}
	
	@Execute
	public void execute() 
	{
	SQLException e = null;	
	int teamNR = -1;
	double sumBruttoAngestellte = 0;
	double sumVZAEAngestellte = 0;
	double sumBruttoBeamte = 0;
	double sumVZAEBeamte = 0;
	double abzugVorkostenstellen = 0;
	
	e = dataService.deletePersonaldurchschnittskosten(pdk.getBerichtsMonat());
	
		if (e != null)
		{
		log.error("SQLException beim L�schen der Personaldurchschnittskosten f�r Monat " + Datumsformate.MONATLANG_JAHR.format(pdk.getBerichtsMonat()),	plugin + ".execute()", e);	
		}
		else
		{
		log.confirm("Personaldurchschnittskosten f�r Berichtsmonat " + Datumsformate.MONATLANG_JAHR.format(pdk.getBerichtsMonat()) + " erfolgreich in der Datenbank gel�scht", plugin + ".execute()");	
		}
	
		for (Team team : pdk.getTeams().values())
		{
		// Spalte 1: Organisationseinheit
		teamNR = team.getTeamNummer();	
		
		// Spalte 3: Summe Brutto Angestellte
		sumBruttoAngestellte = pdk.getSummeBruttoAngestellte(teamNR);

		// Spalte 4: Summe VZ� Angestellte		
		sumVZAEAngestellte = pdk.getSummeVZAEAngestellte(teamNR);
			
		// Spalte 5: Summe Brutto Beamte		
		sumBruttoBeamte = pdk.getSummeBruttoBeamte(teamNR);
			
		// Spalte 6: Summe VZ� Beamte
		sumVZAEBeamte = pdk.getSummeVZAEBeamte(teamNR);
			
		// Spalte 7: Abzug Vorkostenstellen
		abzugVorkostenstellen = pdk.getSummeVorkostenstellen(teamNR);
		
		e = dataService.setPersonaldurchschnittskosten(teamNR, pdk.getBerichtsMonat(), sumBruttoAngestellte, sumVZAEAngestellte, sumBruttoBeamte, sumVZAEBeamte, abzugVorkostenstellen);
		
			if (e != null)
			{
			log.error("SQLException beim Speichern der Personaldurchschnittskosten f�r Team: " + teamNR + " (" +  teamNR + "; " + pdk.getBerichtsMonat() + "; "  + sumBruttoAngestellte + "; " +  sumVZAEAngestellte + "; " +  sumBruttoBeamte + "; " + sumVZAEBeamte + "; " + abzugVorkostenstellen + ")",	plugin + ".execute()", e);	
			}
			else
			{
			log.confirm("Personaldurchschnittskosten f�r Team: " + teamNR + " erfolgreich in der Datenbank gespeichert", plugin + ".execute()");	
			}
		}
	}

	@CanExecute
	public boolean canExecute() 
	{
	boolean result = false;	
		if (pdk.isChecked() && pdk.isDatenOK() && dataService.existsPersonaldurchschnittskosten(pdk.getBerichtsMonat()))
		{
		result = true;	
		}
	return result;
	}
		
}