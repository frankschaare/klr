 
package de.hannit.fsch.rcp.klr.handler.azv;

import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.rcp.klr.azv.AZVDatei;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class OpenAZVHandler 
{
@Inject EPartService partService;
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;

private MPart azvPart = null;
private IEclipseContext partContext = null;

	@Execute
	public void execute(@Named(AppConstants.LOG_STACK) TreeMap<Integer, Event> logStack, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication app, EModelService modelService) 
	{
	FileDialog dialog = new FileDialog(shell);
	dialog.setFilterPath(CSVConstants.AZV.AZV_IMPORT_DIR);
	dialog.setFilterExtensions(new String[] {"*.csv","*.txt", "*.*"});
	String path = dialog.open();
		
		if (path != null) 
		{
		AZVDatei azvDatei = new AZVDatei(path);
		azvDatei.hasHeader(true);
		azvDatei.setDelimiter("\\|");
		azvDatei.setLog(log);
		azvDatei.read();
		
		log.info("AZV-Datei: " + path + " wurde mit " + azvDatei.getLineCount() + " Zeilen eingelesen.", this.getClass().getName());
		
		// Fenstertitel ermitteln
		String title = "AZV-Daten " + azvDatei.getFields().get(0)[CSVConstants.AZV.BERICHTSMONAT_INDEX_CSV] + azvDatei.getFields().get(0)[CSVConstants.AZV.BERICHTSJAHR_INDEX_CSV];
				
		// PartStack Details finden:
		MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
		azvPart = createAZVPart(title, azvDatei);
		details.getChildren().add(azvPart);
		partService.activate(azvPart);
		
		broker.send(Topics.AZV_DATEN, azvDatei);
		}		
		
	}
	
	private MPart createAZVPart(String title, AZVDatei azvDatei)
	{
	azvPart = partService.createPart("de.hannit.fsch.rcp.klr.partdescriptor.azv");
	azvPart.setLabel(title);
	partContext = EclipseContextFactory.create();
	azvPart.setContext(partContext);
	azvPart.getContext().set(CSVConstants.AZV.CONTEXT_DATEN_DATEI, azvDatei);
	
	return azvPart;
	}
		
}