 
package de.hannit.fsch.rcp.klr.handler.azv;

import java.util.TreeMap;

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
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.soa.osecm.IAZVClient;

public class OpenAZVServiceHandler 
{
@Inject EPartService partService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject IAZVClient webService;
@Inject DataService dataservice;

private MPart azvWebservicePart = null;
private IEclipseContext partContext = null;
private String webServiceIP = null;
private TreeMap<Integer, String> result = null;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication app, EModelService modelService) 
	{
	/*
	 * Bevor es losgeht, wird geprüft:
	 * - Welches ist der letzte Monat, für den AZV-Meldungen vorliegen ?
	 * - Wieviele AZV-Meldungen existieren für diesen Monat ?
	 */
	result = dataservice.getAZVMAXMonat();
	
	// Fenstertitel ermitteln
	webServiceIP = webService.getServerInfo();		
	
	String title = "AZV-Daten OS/ECM (" + webServiceIP + ")";
	MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
	azvWebservicePart = createAZVPart(title);
	details.getChildren().add(azvWebservicePart);
	partService.activate(azvWebservicePart);
	}

	private MPart createAZVPart(String title)
	{
	azvWebservicePart = partService.createPart(AppConstants.PartIDs.AZVWEBSERVICEPART);
	azvWebservicePart.setLabel(title);
	partContext = EclipseContextFactory.create();
	azvWebservicePart.setContext(partContext);
	azvWebservicePart.getContext().set(CSVConstants.AZV.CONTEXT_WEBSERVICEIP, webServiceIP);
	azvWebservicePart.getContext().set(CSVConstants.AZV.CONTEXT_DATEN_LETZERBERICHTSMONAT, result);
	
	return azvWebservicePart;
	}
		
}