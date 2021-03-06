 
package de.hannit.fsch.rcp.klr.handler.csv;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.mitarbeiter.GemeinKosten;
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
public class GemeinkostenHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private MPart gkPart = null;
private IEclipseContext partContext = null;
@Inject EPartService partService;

private GemeinKosten gk = null;

	
	/*
	 * Der NavPart l�dt die Mitarbeiterliste f�r den aktuellen Monat inclusive aller AZV-Anteil
	 * Hieraus werden hier die Personaldurchschnittskosten gebildet.
	 */
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.GEMEINKOSTERKOSTEN) GemeinKosten incoming)
	{
	this.gk = incoming;
	}
	
	@Execute
	public void execute(MApplication app, EModelService modelService) 
	{
	gk.splitTeams();
	
	// PartStack Details finden:
	MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
	gkPart = createPDKPart("Gemeinkostenverteilung");
	details.getChildren().add(gkPart);
	partService.activate(gkPart);	
	// setPersonaldurchschnittskosten();	
	}

	private MPart createPDKPart(String title)
	{
	gkPart = partService.createPart("de.hannit.fsch.rcp.klr.partdescriptor.csv.gemeinkosten");
	gkPart.setLabel(title);
	partContext = EclipseContextFactory.create();
	gkPart.setContext(partContext);
	gkPart.getContext().set(AppConstants.CONTEXT_GEMEINKOSTEN, gk);
	
	return gkPart;
	}
			

	@CanExecute
	public boolean canExecute() 
	{
	return (gk.isChecked() && gk.isDatenOK()) ? true : false;
	}
		
}