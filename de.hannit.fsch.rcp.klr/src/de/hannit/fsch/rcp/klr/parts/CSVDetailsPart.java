 
package de.hannit.fsch.rcp.klr.parts;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.CSVDatei;
import de.hannit.fsch.common.LogMessage;
import de.hannit.fsch.common.organisation.reporting.Monatsbericht;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.provider.CSVLabelProvider;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class CSVDetailsPart 
{
@Inject IEventBroker broker;
private Monatsbericht report;
private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM yyyy");
private Group grpBerichtsmonat = null;
private Label lblSummeBrutto = null;
private Label lblAnzahlMitarbeiter = null;
private Table table;
private Table table_1;
private Table table_2;


	@Inject
	public CSVDetailsPart() 
	{
		//TODO Your code here
	}
	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(AppConstants.ActiveSelections.MONATSBERICHT) Monatsbericht report)
	{
	this.report = report;	
	grpBerichtsmonat.setText(fMonatJahr.format(report.getBerichtsMonat()));
	lblSummeBrutto.setText("Summe Brutto: " + String.valueOf(report.getSummeBrutto()) + " €" );
	lblAnzahlMitarbeiter.setText("Anzahl Mitarbeiter: " + String.valueOf(report.getMitarbeiterGesamt()));
	}

	@PostConstruct
	public void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(1, false));
		
		grpBerichtsmonat = new Group(parent, SWT.NONE);
		grpBerichtsmonat.setLayout(new GridLayout(5, true));
		grpBerichtsmonat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBerichtsmonat.setText("Berichtsmonat");
		
		lblSummeBrutto = new Label(grpBerichtsmonat, SWT.NONE);
		lblSummeBrutto.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblSummeBrutto.setText("Summe Brutto");
		
		Label lblSummeStellen = new Label(grpBerichtsmonat, SWT.NONE);
		lblSummeStellen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSummeStellen.setText("Summe Stellen");
		new Label(grpBerichtsmonat, SWT.NONE);
		
		lblAnzahlMitarbeiter = new Label(grpBerichtsmonat, SWT.NONE);
		lblAnzahlMitarbeiter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblAnzahlMitarbeiter.setText("Anzahl Mitarbeiter");
		
		Group grpAzvDaten = new Group(parent, SWT.NONE);
		grpAzvDaten.setLayout(new GridLayout(3, true));
		grpAzvDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpAzvDaten.setText("AZV Daten");
		
		TabFolder tabFolder = new TabFolder(grpAzvDaten, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("New Item");
		
		TableViewer tableViewer_1 = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		tabItem.setControl(table_1);
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("New Item");
		
		TableViewer tableViewer_2 = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		table_2 = tableViewer_2.getTable();
		tabItem_1.setControl(table_2);
		
		Label lblVollzeitquivalent = new Label(grpAzvDaten, SWT.NONE);
		lblVollzeitquivalent.setText("Vollzeit\u00E4quivalent");
		
		TableViewer tableViewer = new TableViewer(grpAzvDaten, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
}