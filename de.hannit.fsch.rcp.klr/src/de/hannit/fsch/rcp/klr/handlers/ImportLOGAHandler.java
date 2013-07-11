 
package de.hannit.fsch.rcp.klr.handlers;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.common.LoGaDatei;
import de.hannit.fsch.common.LogMessage;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class ImportLOGAHandler 
{
@Inject EPartService partService;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication app, EModelService modelService) 
	{
	FileDialog dialog = new FileDialog(shell);
	dialog.setFilterExtensions(new String[] {"*.csv","*.txt", "*.*"});
	String path = dialog.open();
		
		if (path != null) 
		{
		LoGaDatei logaDatei = new LoGaDatei(path);
		logaDatei.hasHeader(true);
		logaDatei.setDelimiter(";");
		logaDatei.read();
		
		// Fenstertitel ermitteln
		String title = "LoGa " + logaDatei.getFields().get(0)[CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_CSV];
				
		// PartStack Details finden:
		MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
		details.getChildren().add(createLOGAPart(title, logaDatei));

			//		broker.send("CSV/Daten", csv);
			//		broker.send(Topics.LOGGING, new LogMessage(IStatus.INFO, this.getClass().getName(), "CSV-Datei: " + path + " wurden an den Event Broker gesendet"));
		}		
		
	}
	
	private MPart createLOGAPart(String title, LoGaDatei logaDatei)
	{
	MPart logaPart = partService.createPart("de.hannit.fsch.rcp.klr.partdescriptor.loga");
	logaPart.setLabel(title);
	logaPart.setContext(EclipseContextFactory.create());
	logaPart.getContext().set(CSVConstants.Loga.CONTEXT_DATEN, logaDatei);
	
	return logaPart;
	}
		
}