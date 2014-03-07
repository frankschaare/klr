 
package de.hannit.fsch.rcp.klr.handler.azv;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.soa.osecm.IAZVClient;

public class OpenAZVServiceHandler 
{
@Inject EPartService partService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IAZVClient webService;

private MPart azvPart = null;
private IEclipseContext partContext = null;
private String webServiceIP = null;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication app, EModelService modelService) 
	{
	// Fenstertitel ermitteln
	webServiceIP = webService.getServerInfo();		
	String title = "AZV-Daten OS/ECM (" + webServiceIP + ")";
				
		// PartStack Details finden:
	MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
	azvPart = createAZVPart(title);
	details.getChildren().add(azvPart);
	partService.activate(azvPart);
	}
	
	private MPart createAZVPart(String title)
	{
	azvPart = partService.createPart(AppConstants.PartIDs.AZVPART);
	azvPart.setLabel(title);
	partContext = EclipseContextFactory.create();
	azvPart.setContext(partContext);
	azvPart.getContext().set(CSVConstants.AZV.CONTEXT_DATEN, null);
	azvPart.getContext().set(CSVConstants.AZV.CONTEXT_WEBSERVICEIP, webServiceIP);
	
	return azvPart;
	}
		
}