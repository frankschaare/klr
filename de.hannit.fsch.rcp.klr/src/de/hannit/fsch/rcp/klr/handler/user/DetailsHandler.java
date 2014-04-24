 
package de.hannit.fsch.rcp.klr.handler.user;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.TreeItem;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.klr.model.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class DetailsHandler 
{
private TreeItem selection = null;	
private Mitarbeiter selectedMitarbeiter = null;

	/*
	 * Wird im NavPart ein Mitarbeiter im Navigationsbaum ausgewählt, wird er über den Broker versendet
	 */
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.TREESELECTION_MITARBEITER) TreeItem incoming)
	{
	this.selection = incoming;	
	}
	
	@Execute
	public void execute(EPartService partService) 
	{
	MPart detailsPart = partService.findPart("de.hannit.fsch.rcp.klr.part.MitarbeiterDetailsPart");
	
	/*
	 * Mitarbeiter im PartContext speichern:
	 */
	IEclipseContext partContext = EclipseContextFactory.create();
	detailsPart.setContext(partContext);
	detailsPart.getContext().set(AppConstants.CONTEXT_SELECTED_MITARBEITER, selectedMitarbeiter);
	
	detailsPart.setVisible(true);
	partService.activate(detailsPart);
	}
	
	@CanExecute
	public boolean canExecute() 
	{
	boolean result = false;	
		if (selection.getData() instanceof Mitarbeiter)
		{
		result =true;
		selectedMitarbeiter = (Mitarbeiter) selection.getData();
		}
	return result;
	}
		
}