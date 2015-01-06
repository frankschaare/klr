 
package de.hannit.fsch.rcp.klr.handler.csv;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.CallCenterImport;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;

public class OpenCallcenterDateiHandler 
{
@Inject EPartService partService;
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject DataService dataService;
private String plugin = this.getClass().getName();


@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, EPartService partService) 
	{
	FileDialog dialog = new FileDialog(shell);
	dialog.setFilterPath(CSVConstants.ERGEBNIS.ERGEBNIS_IMPORT_DIR);
	dialog.setFilterExtensions(new String[] {"*.csv","*.txt", "*.*"});
	String path = dialog.open();
		
		if (path != null) 
		{
		CallCenterImport csv = new CallCenterImport(path);
		csv.setDelimiter(";");
		csv.hasHeader(false);
		csv.read();	
		log.confirm("Callcenter-Datei wurde mit " + csv.getLineCount() + " Zeilen eingelesen", plugin);
		
		dataService.setCallcenterDaten(csv.getLines());
		}	
	}		
}