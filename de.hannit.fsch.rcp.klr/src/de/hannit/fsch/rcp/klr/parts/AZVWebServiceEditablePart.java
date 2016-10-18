 
package de.hannit.fsch.rcp.klr.parts;

import java.util.Date;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.klr.model.azv.AZVDaten;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.provider.AZVWebservicePartLabelProvider;

public class AZVWebServiceEditablePart 
{
@Inject IEventBroker broker;
@Inject DataService dataService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(CSVConstants.AZV.CONTEXT_DATEN_LETZERBERICHTSMONAT) TreeMap<Integer, String> result;
@Inject @Named(CSVConstants.AZV.CONTEXT_WEBSERVICEIP) String strIP;
private  AZVDaten azvDaten = new AZVDaten();

private String plugin = this.getClass().getName();
private Group grpBerichtsmonat = null;
private Label lblMonat = null;
private TableViewerColumn column = null;
private Group grpAZVMeldungen;
private Table table;
private TableViewer tableViewer;
private Label lblBerichtsMonatInfo;
private DateTime dateTime;
private Date maxDate;


	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_WEBSERVICE) AZVDaten incoming)
	{
	this.azvDaten = incoming;	
	updateControls();
	}	

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.MAXDATE) Date incoming)
	{
	this.maxDate = incoming;
	}	
	
	private void updateControls()
	{
		if (azvDaten.getAzvMeldungen() != null)
		{
		grpAZVMeldungen.setText(azvDaten.getAzvMeldungen().size() + " AZV-Meldungen für den Monat " + azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear() + " vom OS/ECM Webservice empfangen.");
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new AZVWebservicePartLabelProvider());
		tableViewer.setInput(azvDaten.getAzvMeldungen().toArray());
		}
	}

	@Inject
	public AZVWebServiceEditablePart() 
	{
	}

	@PostConstruct
	public void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(1, false));
		
		grpBerichtsmonat = new Group(parent, SWT.NONE);
		grpBerichtsmonat.setLayout(new GridLayout(15, false));
		grpBerichtsmonat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBerichtsmonat.setText("Berichtsmonat ausw\u00E4hlen");
		
		lblMonat = new Label(grpBerichtsmonat, SWT.NONE);
		lblMonat.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMonat.setText("Monat: ");
		
		dateTime = new DateTime(grpBerichtsmonat, SWT.DROP_DOWN);
		dateTime.setTouchEnabled(true);
		dateTime.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{	
				if (azvDaten != null)
				{
				azvDaten.setBerichtsMonatSQL(dateTime.getMonth(), dateTime.getYear());
				log.info("EventBroker versendet AZV-Anfrage für den Berichtsmonat: " + azvDaten.getBerichtsMonatAsString(), plugin);
				broker.send(Topics.AZV_WEBSERVICE, azvDaten);
				}
			}
			
		});
		new Label(grpBerichtsmonat, SWT.NONE);
		
		lblBerichtsMonatInfo = new Label(grpBerichtsmonat, SWT.NONE);
		lblBerichtsMonatInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 12, 1));
		
		grpAZVMeldungen = new Group(parent, SWT.NONE);
		grpAZVMeldungen.setLayout(new GridLayout(1, true));
		grpAZVMeldungen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpAZVMeldungen.setText("AZV-Meldungen");
		
		tableViewer = new TableViewer(grpAZVMeldungen, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		column = new TableViewerColumn(tableViewer, SWT.RIGHT, CSVConstants.AZV.PERSONALNUMMER_INDEX_TABLE);
		column.getColumn().setText("PNR");
		column.getColumn().setWidth(80);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		AZVWebServiceEditingSupport editor = new AZVWebServiceEditingSupport(tableViewer);
		editor.setPnrs(dataService.getPersonalnummern());
		column.setEditingSupport(editor);

		column = new TableViewerColumn(tableViewer, SWT.RIGHT, CSVConstants.AZV.USERNAME_INDEX_TABLE);
		column.getColumn().setText("Benutzer");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		column = new TableViewerColumn(tableViewer, SWT.RIGHT, CSVConstants.AZV.NACHNAME_INDEX_TABLE);
		column.getColumn().setText("Name");
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		column = new TableViewerColumn(tableViewer, SWT.RIGHT, CSVConstants.AZV.TEAM_INDEX_TABLE);
		column.getColumn().setText("Team");
		column.getColumn().setWidth(60);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.AZV.BERICHTSMONAT_INDEX_TABLE);
		column.getColumn().setText(CSVConstants.AZV.BERICHTSMONAT_LABEL_TABLE);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.AZV.KOSTENSTELLE_INDEX_TABLE);
		column.getColumn().setText(CSVConstants.AZV.KOSTENSTELLE_LABEL_TABLE);
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);	
		
		column = new TableViewerColumn(tableViewer, SWT.LEFT, CSVConstants.AZV.KOSTENTRAEGER_INDEX_TABLE);
		column.getColumn().setText(CSVConstants.AZV.KOSTENTRAEGER_LABEL_TABLE);
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);	
		
		column = new TableViewerColumn(tableViewer, SWT.RIGHT, CSVConstants.AZV.PROZENTANTEIL_INDEX_TABLE);
		column.getColumn().setText(CSVConstants.AZV.PROZENTANTEIL_LABEL_TABLE);
		column.getColumn().setWidth(50);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);	
	}	
	@Focus
	public void onFocus() 
	{
	
	}
}