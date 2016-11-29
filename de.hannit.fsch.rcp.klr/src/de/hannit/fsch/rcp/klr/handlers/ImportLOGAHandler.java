 
package de.hannit.fsch.rcp.klr.handlers;

import java.util.TreeMap;

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
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.loga.LoGaDatensatz;
import de.hannit.fsch.klr.model.organisation.Organisation;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.csv.CSVDatei;
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;

public class ImportLOGAHandler 
{
@Inject DataService dataService;		
@Inject EPartService partService;
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(AppConstants.ORGANISATION) Organisation hannit;


private MPart logaPart = null;
private IEclipseContext partContext = null;

	@Execute
	public void execute(@Named(AppConstants.LOG_STACK) TreeMap<Integer, Event> logStack, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication app, EModelService modelService) 
	{
	FileDialog dialog = new FileDialog(shell);
	dialog.setFilterExtensions(new String[] {"*.csv","*.txt", "*.*"});
	String path = dialog.open();
		
		if (path != null) 
		{
		LoGaDatei logaDatei = new LoGaDatei(path);
		logaDatei.hasHeader(true);
		logaDatei.setDelimiter(";");
		logaDatei.setLog(log);
		logaDatei.read();
		checkLogaData(logaDatei);			
		
		log.info("LoGa-Datei: " + path + " wurde mit " + logaDatei.getLineCount() + " Zeilen eingelesen.", this.getClass().getName());
		
		// Fenstertitel ermitteln
		String title = "LoGa " + logaDatei.getFields().get(0)[CSVConstants.Loga.ABRECHNUNGSMONAT_INDEX_CSV];
				
		// PartStack Details finden:
		MPartStack details = (MPartStack) modelService.find("de.hannit.fsch.rcp.klr.partstack.details", app);
		logaPart = createLOGAPart(title, logaDatei);
		details.getChildren().add(logaPart);
		partService.activate(logaPart);
		
		broker.send(Topics.LOGA_DATEN, logaDatei);
		}		
		
	}
	
	/*
	 * Versuche eventuell fehlende Daten nachzutragen
	 */
	private void checkLogaData(LoGaDatei logaDatei)
	{
	int pnrVorstand = hannit.getVorstand().getPersonalNR(); 
			
		for (LoGaDatensatz ds : logaDatei.getDaten().values())
		{
			if (ds.getPersonalNummer() == pnrVorstand)
			{
			ds.setTarifGruppe(hannit.getVorstand().getTarifGruppe());
			ds.setStellenAnteil(1);
			}
			// Sonderfall Schnese
			if (ds.getPersonalNummer() == 120025)
			{
			ds.setTarifGruppe("A13");
			ds.setStellenAnteil(1);
			}
			
			// Stellenanteil unbekannt ?
			if (ds.getStellenAnteil() == 999999)
			{
			ds.setStellenAnteil(dataService.getLetzterStellenanteil(ds.getPersonalNummer()));	
			}
		}
		
	}	
	
	private MPart createLOGAPart(String title, CSVDatei logaDatei)
	{
	logaPart = partService.createPart("de.hannit.fsch.rcp.klr.partdescriptor.loga");
	logaPart.setLabel(title);
	partContext = EclipseContextFactory.create();
	logaPart.setContext(partContext);
	logaPart.getContext().set(CSVConstants.Loga.CONTEXT_DATEN, logaDatei);
	
	return logaPart;
	}
		
}