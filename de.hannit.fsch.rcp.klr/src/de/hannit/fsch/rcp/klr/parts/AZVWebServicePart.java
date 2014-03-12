 
package de.hannit.fsch.rcp.klr.parts;

import java.text.ParseException;
import java.util.Calendar;
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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.rcp.klr.azv.AZVDaten;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class AZVWebServicePart 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(CSVConstants.AZV.CONTEXT_DATEN_LETZERBERICHTSMONAT) TreeMap<Integer, String> result;
@Inject @Named(CSVConstants.AZV.CONTEXT_WEBSERVICEIP) String strIP;
private  AZVDaten azvDaten = new AZVDaten();

private String plugin = this.getClass().getName();
private Group grpBerichtsmonat = null;
private Label lblMonat = null;
private Combo selectMonat = null;
private Combo selectJahr = null;
private TableViewerColumn column = null;
private Group grpAZVMeldungen;
private Table table;
private TableViewer tableViewer;
private int anzahlAZVMeldungen = 0;
private String letzterBerichtsMonat = "";
private Label lblBerichtsMonatInfo;

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.AZV_WEBSERVICE) AZVDaten incoming)
	{
	this.azvDaten = incoming;	
	updateControls();
	}	

	private void updateControls()
	{
		if (azvDaten.getAzvMeldungen() != null)
		{
		grpAZVMeldungen.setText(azvDaten.getAzvMeldungen().size() + " AZV-Meldungen für den Monat " + azvDaten.getRequestedMonth() + " " + azvDaten.getRequestedYear() + " vom OS/ECM Webservice empfangen.");
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(azvDaten);
		tableViewer.setInput(azvDaten.getAzvMeldungen().toArray());
		}
	}

	@Inject
	public AZVWebServicePart() 
	{
	}

	private void setControlsToNextMonth()
	{
		if (result != null)
		{
		Date lastMonth = null;
		Calendar cal = Calendar.getInstance();
			try
			{
			lastMonth = Datumsformate.STANDARDFORMAT_SQLSERVER.parse(result.get(result.firstKey()));
			cal.setTime(lastMonth);
			letzterBerichtsMonat = Datumsformate.MONATLANG_JAHR.format(lastMonth);
			cal.add(Calendar.MONTH, 1);
			selectMonat.select(cal.get(Calendar.MONTH));
			selectJahr.removeAll();
			cal.add(Calendar.YEAR, -2);
			selectJahr.add(Datumsformate.JAHR.format(cal.getTime()), 0);
			cal.add(Calendar.YEAR, 1);
			selectJahr.add(Datumsformate.JAHR.format(cal.getTime()), 1);
			cal.add(Calendar.YEAR, 1);
			selectJahr.add(Datumsformate.JAHR.format(cal.getTime()), 2);
			cal.add(Calendar.YEAR, 1);
			selectJahr.add(Datumsformate.JAHR.format(cal.getTime()), 3);
			cal.add(Calendar.YEAR, 1);
			selectJahr.add(Datumsformate.JAHR.format(cal.getTime()), 4);
			selectJahr.select(2);
			}
			catch (ParseException ex)
			{
			ex.printStackTrace();
			log.error("ParseException beim ermitteln des letzten Berichtsmonats !", plugin + ".createComposite(Composite parent)", ex);
			}		
		anzahlAZVMeldungen = result.firstKey();
		lblBerichtsMonatInfo.setText("Letzer gespeicherter Berichtsmonat ist " +  letzterBerichtsMonat + " (" + anzahlAZVMeldungen + " AZV-Meldungen)" );
			
			if (azvDaten != null)
			{
			azvDaten.setRequestedMonth(selectMonat.getItem(selectMonat.getSelectionIndex()));
			azvDaten.setRequestedYear(selectJahr.getItem(selectJahr.getSelectionIndex()));
			azvDaten.setWebServiceIP(strIP);
			broker.send(Topics.AZV_WEBSERVICE, azvDaten);
			}
		}
	}
	@PostConstruct
	public void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(1, false));
		
		grpBerichtsmonat = new Group(parent, SWT.NONE);
		grpBerichtsmonat.setLayout(new GridLayout(17, false));
		grpBerichtsmonat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBerichtsmonat.setText("Berichtsmonat ausw\u00E4hlen");
		
		lblMonat = new Label(grpBerichtsmonat, SWT.NONE);
		lblMonat.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMonat.setText("Monat: ");
		
		selectMonat = new Combo(grpBerichtsmonat, SWT.NONE);
		selectMonat.setToolTipText("Berichtsmonat für die gewünschten AZV-Meldungen");
		selectMonat.add("Januar", 0);
		selectMonat.add("Februar", 1);
		selectMonat.add("März", 2);
		selectMonat.add("April", 3);
		selectMonat.add("Mai", 4);
		selectMonat.add("Juni", 5);
		selectMonat.add("Juli", 6);
		selectMonat.add("August", 7);
		selectMonat.add("September", 8);
		selectMonat.add("Oktober", 9);
		selectMonat.add("November", 10);
		selectMonat.add("Dezember", 11);
		selectMonat.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			azvDaten.setRequestedMonth(selectMonat.getItem(selectMonat.getSelectionIndex()));
			}
		});


		Label lblJahr = new Label(grpBerichtsmonat, SWT.NONE);
		lblJahr.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblJahr.setText("Jahr:");
		
		selectJahr = new Combo(grpBerichtsmonat, SWT.NONE);
		selectJahr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		selectJahr.setToolTipText("Berichtsjahr für die gewünschten AZV-Meldungen");
		selectJahr.add("2013");
		selectJahr.add("2014");
		selectJahr.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			azvDaten.setRequestedYear(selectJahr.getItem(selectJahr.getSelectionIndex()));
			}
		});
		selectJahr.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusLost(FocusEvent e)
			{
			broker.send(Topics.AZV_WEBSERVICE, azvDaten);
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
							
			}
		});

		new Label(grpBerichtsmonat, SWT.NONE);
		
		lblBerichtsMonatInfo = new Label(grpBerichtsmonat, SWT.NONE);
		lblBerichtsMonatInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 12, 1));
		
		setControlsToNextMonth();
		
		grpAZVMeldungen = new Group(parent, SWT.NONE);
		grpAZVMeldungen.setLayout(new GridLayout(1, true));
		grpAZVMeldungen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpAZVMeldungen.setText("AZV-Meldungen");
		
		tableViewer = new TableViewer(grpAZVMeldungen, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		column = new TableViewerColumn(tableViewer, SWT.RIGHT, 0);
		column.getColumn().setText("ID");
		column.getColumn().setWidth(50);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tableViewer, SWT.RIGHT, CSVConstants.AZV.PERSONALNUMMER_INDEX_TABLE);
		column.getColumn().setText("PNR");
		column.getColumn().setWidth(80);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

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