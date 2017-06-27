 
package de.hannit.fsch.rcp.klr.parts;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
import de.hannit.fsch.klr.model.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.klr.model.mitarbeiter.SummenZeile;
import de.hannit.fsch.klr.model.team.Team;
import de.hannit.fsch.rcp.klr.provider.PersonalDurchschnittsKostenLabelProvider;

public class PersonaldurchschnittskostenPart 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Named(AppConstants.CONTEXT_PERSONALDURCHSCHNITTSKOSTEN) PersonalDurchschnittsKosten pdk;

private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM yyyy");
private Group grpBerichtsmonat = null;
private Label lblSummeBrutto = null;
private Label lblSummeStellen = null;
private Label lblAnzahlMitarbeiter = null;
private TableViewerColumn column = null;
private TableViewer pdkViever = null;
private Table pdkTable;
private Group grpPersonaldurchschnittsKosten;


	@Inject
	public PersonaldurchschnittskostenPart() 
	{
	}
	
	@PostConstruct
	public void createComposite(Composite parent) 
	{
	String plugin = this.getClass().getName() + ".createComposite(Composite parent)";
	
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
		
		grpPersonaldurchschnittsKosten = new Group(parent, SWT.NONE);
		grpPersonaldurchschnittsKosten.setLayout(new GridLayout(1, true));
		grpPersonaldurchschnittsKosten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpPersonaldurchschnittsKosten.setText("Personaldurchschnittskosten");
		
		pdkViever = new TableViewer(grpPersonaldurchschnittsKosten, SWT.BORDER | SWT.FULL_SELECTION);
		pdkTable = pdkViever.getTable();
		pdkTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		pdkTable.setHeaderVisible(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 0);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN1_OE);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 1);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN2_ANGESTELLTE);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 2);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN3_ANGESTELLTE_VZAE);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 3);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN4_BEAMTE);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 4);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN5_BEAMTE_VZAE);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 5);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN6_SUMME_BRUTTO);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);		
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 6);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN7_SUMME_VZAE);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 7);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN8_ABZUG_VORKOSSTENSTELLEN);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);		
		
		column = new TableViewerColumn(pdkViever, SWT.RIGHT, 8);
		column.getColumn().setText(PersonalDurchschnittsKosten.COLUMN9_VZAE_ENDKOSTENSTELLEN);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);		
		
		pdkViever.setContentProvider(new ArrayContentProvider());
			if (pdk != null)
			{
			pdkViever.setLabelProvider(new PersonalDurchschnittsKostenLabelProvider());		
			log.info("Generiere Input f�r pdkViewer aus pdk.getSummentabelle()", plugin);	
			log.info("Input f�r pdkViewer Schritt1: Pr�fe gefundene Teams.", plugin);
				for (Team t : pdk.getTeams().values())
				{
				log.info(t.getTeamBezeichnung() + " enth�lt " + t.getTeamMitglieder().size() + " Mitarbeiter, davon " + t.getAngestellte().size() + " Angestellte und " + t.getBeamte().size() + " Beamte.", plugin);
				}
			log.info("Input f�r pdkViewer Schritt2: erstelle Summentabelle aus den vorhandenen Teams.", plugin);
			TreeMap<Integer, SummenZeile> st = pdk.getSummentabelle();
			log.info("Summentabelle enth�lt " + st.size() + " Zeilen.", plugin);

				for (SummenZeile sz : st.values())
				{
				log.info("Summentabelle enth�lt Summenzeile: " + sz.getColumn0() + ", " + sz.getColumn1(), plugin);	
				}
			pdkViever.setInput(st.values().toArray());	
			grpBerichtsmonat.setText(fMonatJahr.format(pdk.getBerichtsMonat()));
			lblSummeBrutto.setText("Summe Brutto: " + NumberFormat.getCurrencyInstance().format(pdk.getSummeBruttoGesamt()));
			lblSummeStellen.setText("Summe Stellen: " + String.valueOf(pdk.getSummeVZAEGesamt()));
			lblAnzahlMitarbeiter.setText(String.valueOf(pdk.getAnzahlMitarbeiter()) + " Mitarbeiter");
			}
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
}