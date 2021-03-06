 
package de.hannit.fsch.rcp.klr.handler.personal;

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
import de.hannit.fsch.klr.model.mitarbeiter.PersonalDurchschnittsKosten;
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
public class PersonalDurchschnittskostenHandler
{
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private MPart pdkPart = null;
private IEclipseContext partContext = null;
@Inject EPartService partService;

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
	public void execute(MApplication app, EModelService modelService) 
	{
	// PartStack Details finden:
	MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
	pdkPart = createPDKPart("Personaldurchschnittskosten");
	details.getChildren().add(pdkPart);
	partService.activate(pdkPart);	
	// setPersonaldurchschnittskosten();	
	}

	private MPart createPDKPart(String title)
	{
	pdkPart = partService.createPart("de.hannit.fsch.rcp.klr.partdescriptor.personal.durchschnittskosten");
	pdkPart.setLabel(title);
	partContext = EclipseContextFactory.create();
	pdkPart.setContext(partContext);
	pdkPart.getContext().set(AppConstants.CONTEXT_PERSONALDURCHSCHNITTSKOSTEN, pdk);
	
	return pdkPart;
	}
			

	@CanExecute
	public boolean canExecute() 
	{
	return (pdk.isChecked() && pdk.isDatenOK()) ? true : false;
	}
		
}