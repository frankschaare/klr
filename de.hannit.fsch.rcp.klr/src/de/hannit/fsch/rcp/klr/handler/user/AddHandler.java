 
package de.hannit.fsch.rcp.klr.handler.user;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class AddHandler 
{
@Inject EPartService partService;	
private MPart addPart = null;
private IEclipseContext partContext = null;

	@Execute
	public void execute(MApplication app, EModelService modelService) 
	{
	// PartStack Details finden:
	MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
	addPart = createAddPart();
	details.getChildren().add(addPart);
	partService.activate(addPart);
	}
	
	private MPart createAddPart()
	{
	addPart = partService.createPart("de.hannit.fsch.rcp.klr.partdescriptor.user.add");
	partContext = EclipseContextFactory.create();
	addPart.setContext(partContext);

	return addPart;
	}	
	
	
	@CanExecute
	public boolean canExecute() 
	{
	return true;
	}
		
}