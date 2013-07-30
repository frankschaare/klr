 
package de.hannit.fsch.rcp.klr.parts;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.mitarbeiter.besoldung.Tarifgruppen;
import de.hannit.fsch.common.organisation.reporting.Monatsbericht;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class CSVDetailsPart 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(AppConstants.CONTEXT_TARIFGRUPPEN) private Tarifgruppen tarifGruppen;

private Monatsbericht report;
private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM yyyy");
private Group grpBerichtsmonat = null;
private Label lblSummeBrutto = null;
private Label lblSummeStellen = null;
private Label lblAnzahlMitarbeiter = null;
private Label lblVollzeitquivalent = null;
private TableViewerColumn column = null;
private TableViewer vzae = null;
private Table vzaeTable;
private Table table_1;
private Table table_2;


	@Inject
	public CSVDetailsPart() 
	{
		//TODO Your code here
	}
	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.TARIFGRUPPEN) Tarifgruppen tgs)
	{
	log.info("Es wurden "+ tgs.getTarifGruppen().size() + " Tarifgruppen geladen.", this.getClass().getName());
	tarifGruppen = tgs;
	vzae.setLabelProvider(tgs);	
	vzae.setInput(tarifGruppen.getTarifGruppen().values().toArray());
	
	grpBerichtsmonat.setText(fMonatJahr.format(tarifGruppen.getBerichtsMonat()));
	lblSummeBrutto.setText("Summe Brutto: " + NumberFormat.getCurrencyInstance().format(tarifGruppen.getSummeTarifgruppen()));
	lblSummeStellen.setText("Summe Stellen: " + String.valueOf(tarifGruppen.getSummeStellen()));
	lblAnzahlMitarbeiter.setText(String.valueOf(tarifGruppen.getAnzahlMitarbeiter()) + " Mitarbeiter");
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
		
		lblSummeStellen = new Label(grpBerichtsmonat, SWT.NONE);
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
		
		lblVollzeitquivalent = new Label(grpAzvDaten, SWT.NONE);
		lblVollzeitquivalent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblVollzeitquivalent.setText("Vollzeit\u00E4quivalent");
		
		vzae = new TableViewer(grpAzvDaten, SWT.BORDER | SWT.FULL_SELECTION);
		vzaeTable = vzae.getTable();
		vzaeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		vzaeTable.setHeaderVisible(true);
		
		column = new TableViewerColumn(vzae, SWT.RIGHT, 0);
		column.getColumn().setText("Tarifgruppe");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(vzae, SWT.RIGHT, 1);
		column.getColumn().setText("Summe Tarifgruppe");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(vzae, SWT.RIGHT, 2);
		column.getColumn().setText("Summe Stellen");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(vzae, SWT.RIGHT, 3);
		column.getColumn().setText("Vollzeitäquivalent");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		vzae.setContentProvider(new ArrayContentProvider());
			if (tarifGruppen != null)
			{
			vzae.setLabelProvider(tarifGruppen);		
			vzae.setInput(tarifGruppen.getTarifGruppen().values().toArray());	
			grpBerichtsmonat.setText(fMonatJahr.format(tarifGruppen.getBerichtsMonat()));
			lblSummeBrutto.setText("Summe Brutto: " + NumberFormat.getCurrencyInstance().format(tarifGruppen.getSummeTarifgruppen()));
			lblSummeStellen.setText("Summe Stellen: " + String.valueOf(tarifGruppen.getSummeStellen()));
			lblAnzahlMitarbeiter.setText(String.valueOf(tarifGruppen.getAnzahlMitarbeiter()) + " Mitarbeiter");
			}
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
}