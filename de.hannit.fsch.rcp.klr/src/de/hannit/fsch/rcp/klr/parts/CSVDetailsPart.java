 
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
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.common.mitarbeiter.besoldung.Tarifgruppen;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class CSVDetailsPart 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(AppConstants.CONTEXT_TARIFGRUPPEN) Tarifgruppen tarifGruppen;
@Inject @Named(AppConstants.CONTEXT_MONATSSUMMEN) MonatsSummen mSummen;
@Inject private EMenuService menuService;
@ Inject ESelectionService selectionService;
private static final String POPUPMENUD_ID = "de.hannit.fsch.rcp.klr.menu.main.database";

private Group grpBerichtsmonat = null;
private Label lblSummeBrutto = null;
private Label lblSummeStellen = null;
private Label lblAnzahlMitarbeiter = null;
private Label lblVollzeitquivalent = null;
private TableViewerColumn column = null;
private TableViewer vzae = null;
private Table vzaeTable;
private Group grpAzvDaten;
private TableViewer tvGesamt = null;
private TableViewer tvKST = null;
private TableViewer tvKTR = null;
private Table gesamtTable;
private Table kstTable;
private Table ktrTable;
private TabItem tabKST;
private TabItem tabKTR;
private TabItem tbtmGesamt;


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
	
	grpBerichtsmonat.setText(Datumsformate.MONATLANG_JAHR.format(tarifGruppen.getBerichtsMonat()));
	lblSummeBrutto.setText("Summe Brutto: " + NumberFormat.getCurrencyInstance().format(tarifGruppen.getSummeTarifgruppen()));
	lblSummeStellen.setText("Summe Stellen: " + String.valueOf(tarifGruppen.getSummeStellen()));
	lblAnzahlMitarbeiter.setText(String.valueOf(tarifGruppen.getAnzahlMitarbeiter()) + " Mitarbeiter");
	}

	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.MONATSSUMMEN) MonatsSummen ms, Composite parent)
	{
	this.mSummen = ms;
	grpAzvDaten.setForeground(mSummen.isSummeOK() ? parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN) : parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
	grpAzvDaten.setText("Summe KST / KTR = " + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()));
	tbtmGesamt.setText(mSummen != null ? "Gesamt (" + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()) + ")" : "Gesamt");
	tvGesamt.setLabelProvider(mSummen);	
	tvGesamt.setInput(mSummen.getGesamtKosten().values().toArray());
	
	tabKST.setText(mSummen != null ? "Kostenstellen (" + NumberFormat.getCurrencyInstance().format(mSummen.getKSTMonatssumme()) + ")" : "Kostenstellen");
	tabKTR.setText(mSummen != null ? "Kostenträger (" + NumberFormat.getCurrencyInstance().format(mSummen.getKTRMonatssumme()) + ")" : "Kostenträger");
	
	tvKST.setInput(mSummen.getGesamtKostenstellen().values().toArray());
	tvKTR.setInput(mSummen.getGesamtKostentraeger().values().toArray());
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
		
		grpAzvDaten = new Group(parent, SWT.NONE);
		grpAzvDaten.setLayout(new GridLayout(3, true));
		grpAzvDaten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
			if (mSummen != null)
			{
			grpAzvDaten.setForeground(mSummen.isSummeOK() ? parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN) : parent.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
			grpAzvDaten.setText("Summe KST / KTR = " + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()));
			}
			else
			{
			grpAzvDaten.setText("AZV Daten");
			}
		
		TabFolder tabFolder = new TabFolder(grpAzvDaten, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
		
		tbtmGesamt = new TabItem(tabFolder, SWT.NONE);
		tbtmGesamt.setText(mSummen != null ? "Gesamt (" + NumberFormat.getCurrencyInstance().format(mSummen.getKstktrMonatssumme()) + ")" : "Gesamt");
		
		tvGesamt = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		gesamtTable = tvGesamt.getTable();
		gesamtTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		gesamtTable.setHeaderVisible(true);
		gesamtTable.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
			selectionService.setSelection((mSummen != null) ? mSummen : gesamtTable); 
			}
			
		});
		
		column = new TableViewerColumn(tvGesamt, SWT.RIGHT, 0);
		column.getColumn().setText("KST / KTR");
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tvGesamt, SWT.RIGHT, 1);
		column.getColumn().setText("Summe");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		tbtmGesamt.setControl(gesamtTable);
		
		tabKST = new TabItem(tabFolder, SWT.NONE);
		tabKST.setText(mSummen != null ? "Kostenstellen (" + NumberFormat.getCurrencyInstance().format(mSummen.getKSTMonatssumme()) + ")" : "Kostenstellen");
		
		tvKST = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		kstTable = tvKST.getTable();
		kstTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		kstTable.setHeaderVisible(true);
	
		column = new TableViewerColumn(tvKST, SWT.RIGHT, 0);
		column.getColumn().setText("Kostenstelle");
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tvKST, SWT.RIGHT, 1);
		column.getColumn().setText("Summe");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
			
		tabKST.setControl(kstTable);
		
		tabKTR = new TabItem(tabFolder, SWT.NONE);
		tabKTR.setText(mSummen != null ? "Kostenträger (" + NumberFormat.getCurrencyInstance().format(mSummen.getKTRMonatssumme()) + ")" : "Kostenträger");
		
		tvKTR = new TableViewer(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		ktrTable = tvKTR.getTable();
		ktrTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		ktrTable.setHeaderVisible(true);
	
		column = new TableViewerColumn(tvKTR, SWT.RIGHT, 0);
		column.getColumn().setText("Kostenträger");
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(tvKTR, SWT.RIGHT, 1);
		column.getColumn().setText("Summe");
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		tabKTR.setControl(ktrTable);		
		
		lblVollzeitquivalent = new Label(grpAzvDaten, SWT.NONE);
		lblVollzeitquivalent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblVollzeitquivalent.setText("Vollzeit\u00E4quivalent");
		
		vzae = new TableViewer(grpAzvDaten, SWT.BORDER | SWT.FULL_SELECTION);
		vzaeTable = vzae.getTable();
		menuService.registerContextMenu(vzae.getTable(), POPUPMENUD_ID);
		vzaeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		vzaeTable.setHeaderVisible(true);
		vzaeTable.addFocusListener(new FocusAdapter()
		{

			@Override
			public void focusGained(FocusEvent e)
			{
			selectionService.setSelection((tarifGruppen != null) ? tarifGruppen : vzaeTable); 
			}
			
		});
		
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
			grpBerichtsmonat.setText(Datumsformate.MONATLANG_JAHR.format(tarifGruppen.getBerichtsMonat()));
			lblSummeBrutto.setText("Summe Brutto: " + NumberFormat.getCurrencyInstance().format(tarifGruppen.getSummeTarifgruppen()));
			lblSummeStellen.setText("Summe Stellen: " + String.valueOf(tarifGruppen.getSummeStellen()));
			lblAnzahlMitarbeiter.setText(String.valueOf(tarifGruppen.getAnzahlMitarbeiter()) + " Mitarbeiter");
			}
			
		tvGesamt.setContentProvider(new ArrayContentProvider());
		tvKTR.setContentProvider(new ArrayContentProvider());
		tvKST.setContentProvider(new ArrayContentProvider());
			if (mSummen != null)
			{
			tvGesamt.setLabelProvider(mSummen);
			tvKST.setLabelProvider(mSummen);
			tvKTR.setLabelProvider(mSummen);
			tvGesamt.setInput(mSummen.getGesamtKosten().values().toArray());
			tvKST.setInput(mSummen.getGesamtKostenstellen().values().toArray());
			tvKTR.setInput(mSummen.getGesamtKostentraeger().values().toArray());
			}
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
}