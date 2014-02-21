 
package de.hannit.fsch.rcp.klr.handler.personal;

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
import de.hannit.fsch.common.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;

/**
 * Erstellt die CSV-Datei für die Entlastung der Kostenstelle 0400 auf andere Kostenträger
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
	 * Der NavPart lädt die Mitarbeiterliste für den aktuellen Monat inclusive aller AZV-Anteil
	 * Hieraus werden hier die Personaldurchschnittskosten gebildet.
	 */
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.PERSONALDURCHSCHNITTSKOSTEN) PersonalDurchschnittsKosten incoming)
	{
	this.pdk = incoming;
	}
	
	@Execute
	public void execute(MApplication app, EModelService modelService) 
	{
	
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