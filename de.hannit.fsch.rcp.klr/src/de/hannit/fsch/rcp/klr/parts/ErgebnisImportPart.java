 
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
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.win32.UDACCEL;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.common.mitarbeiter.GemeinKosten;
import de.hannit.fsch.common.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.klr.kostenrechnung.Ergebnis;
import de.hannit.fsch.rcp.klr.constants.Topics;

public class ErgebnisImportPart 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(AppConstants.CONTEXT_ERGEBNIS) Ergebnis ergebnis;

private String plugin = this.getClass().getName();
private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM yyyy");
private Group grpBerichtszeitraum;
private Label lblTeam;
private Label lblBerichtszeitraumVon;
private Combo selectTeam = null;
private TableViewerColumn column = null;
private TableViewer ergebnisViewer = null;
private Table ergebnisTable;
private Group grpErgebnis;
private Text txtBerichtszeitraumVon;
private Label lblBerichtszeitraumBis;
private Text txtBerichtszeitraumBis;

	@Inject
	public ErgebnisImportPart() 
	{
	}
	
	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.ERGEBNIS_DATEN) Ergebnis incoming)
	{
	this.ergebnis = incoming;
	updateControls();
	}	
	
	private void updateControls()
	{			
		if (ergebnis != null)
		{
		grpErgebnis.setText("Datenquelle: " + " (" + ergebnis.getDatenQuelle() + ")");
		txtBerichtszeitraumVon.setText(Datumsformate.STANDARDFORMAT_JAHR_VIERSTELLIG.format(ergebnis.getBerichtszeitraumVon()));
		txtBerichtszeitraumBis.setText(Datumsformate.STANDARDFORMAT_JAHR_VIERSTELLIG.format(ergebnis.getBerichtszeitraumBis()));
		ergebnisViewer.setLabelProvider(ergebnis);		
		ergebnisViewer.setInput(ergebnis.getActiveServices().toArray());	
		// grpBerichtszeitraum.setText(fMonatJahr.format(gk.getBerichtsMonat()));
		}	
	}

	@PostConstruct
	public void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(1, false));
		
		grpBerichtszeitraum = new Group(parent, SWT.NONE);
		grpBerichtszeitraum.setLayout(new GridLayout(16, true));
		grpBerichtszeitraum.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBerichtszeitraum.setText("Berichtszeitraum");
		
		lblTeam = new Label(grpBerichtszeitraum, SWT.NONE);
		lblTeam.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblTeam.setText("Team: ");
		
		selectTeam = new Combo(grpBerichtszeitraum, SWT.NONE);
		selectTeam.setToolTipText("Endkostenstelle, von der auf die Kostentr\u00E4ger des Teams verteilt wird");
		selectTeam.add(AppConstants.ENDKOSTENSTELLE_TEAM1);
		selectTeam.add(AppConstants.ENDKOSTENSTELLE_TEAM2);
		selectTeam.add(AppConstants.ENDKOSTENSTELLE_TEAM3);
		selectTeam.add(AppConstants.ENDKOSTENSTELLE_TEAM4);
		selectTeam.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			String selected = selectTeam.getItem(selectTeam.getSelectionIndex());
			}	
		});	
		
		lblBerichtszeitraumVon = new Label(grpBerichtszeitraum, SWT.NONE);
		lblBerichtszeitraumVon.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBerichtszeitraumVon.setText("Berichtszeitraum von: ");
		
		txtBerichtszeitraumVon = new Text(grpBerichtszeitraum, SWT.BORDER);
		txtBerichtszeitraumVon.setToolTipText("Ergebnis [E3] aus /Kostenkontenschemata/Kostenstellenrechnung/ERG_KST/ENDKOSTSTE f\u00FCr das zu bearbeitende Team");
		txtBerichtszeitraumVon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblBerichtszeitraumBis = new Label(grpBerichtszeitraum, SWT.NONE);
		lblBerichtszeitraumBis.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblBerichtszeitraumBis.setText("bis:");
		
		txtBerichtszeitraumBis = new Text(grpBerichtszeitraum, SWT.BORDER);
		txtBerichtszeitraumBis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		new Label(grpBerichtszeitraum, SWT.NONE);
		
		grpErgebnis = new Group(parent, SWT.NONE);
		grpErgebnis.setLayout(new GridLayout(1, true));
		grpErgebnis.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpErgebnis.setText("Ergebnis");
		
		ergebnisViewer = new TableViewer(grpErgebnis, SWT.BORDER | SWT.FULL_SELECTION);
		ergebnisTable = ergebnisViewer.getTable();
		
		ergebnisTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		ergebnisTable.setHeaderVisible(true);
		ergebnisTable.setLinesVisible(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.LEFT, 0);
		column.getColumn().setText(Ergebnis.COLUMN0_SERVICE);
		column.getColumn().setWidth(200);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 1);
		column.getColumn().setText(Ergebnis.COLUMN1_ERLÖSE);
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 2);
		column.getColumn().setText(Ergebnis.COLUMN2_MATERIALAUFWAND);
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 3);
		column.getColumn().setText(Ergebnis.COLUMN3_AFA);
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 4);
		column.getColumn().setText(Ergebnis.COLUMN4_SBA);
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);

		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 5);
		column.getColumn().setText(Ergebnis.COLUMN5_PERSONALKOSTEN);
		column.getColumn().setWidth(90);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);		
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 6);
		column.getColumn().setText(Ergebnis.COLUMN6_SUMME_EINZELKOSTEN);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 7);
		column.getColumn().setText(Ergebnis.COLUMN7_DECKUNGSBEITRAG1);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 8);
		column.getColumn().setText(Ergebnis.COLUMN8_VERTEILUNG_KST1110);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 9);
		column.getColumn().setText(Ergebnis.COLUMN9_VERTEILUNG_KST2010);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 10);
		column.getColumn().setText(Ergebnis.COLUMN10_VERTEILUNG_KST2020);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 11);
		column.getColumn().setText(Ergebnis.COLUMN11_VERTEILUNG_KST3010);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 12);
		column.getColumn().setText(Ergebnis.COLUMN12_VERTEILUNG_KST4010);
		column.getColumn().setWidth(110);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 13);
		column.getColumn().setText(Ergebnis.COLUMN13_VERTEILUNG_KSTGESAMT);
		column.getColumn().setWidth(120);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(ergebnisViewer, SWT.RIGHT, 14);
		column.getColumn().setText(Ergebnis.COLUMN14_ERGBNIS);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		ergebnisViewer.setContentProvider(new ArrayContentProvider());
		//menuService.registerContextMenu(gkViever.getControl(), POPUPMENUD_ID);
		
		updateControls();
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
}