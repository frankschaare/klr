 
package de.hannit.fsch.rcp.klr.parts;

import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
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
import org.eclipse.swt.widgets.Text;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.mitarbeiter.GemeinKosten;
import de.hannit.fsch.common.mitarbeiter.PersonalDurchschnittsKosten;

public class GemeinkostenPart 
{
@Inject IEventBroker broker;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@ Inject @Named(AppConstants.CONTEXT_GEMEINKOSTEN) GemeinKosten gk;

private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM yyyy");
private Group grpBerichtsmonat = null;
private Label lblKostenstelle = null;
private Label lblVerteilungsSumme = null;
private Combo selectEndkostenStelle = null;
private TableViewerColumn column = null;
private TableViewer gkViever = null;
private Table gkTable;
private Group grpGemeinKosten;
private String txtLabelGemeinkosten = "Gemeinkosten ";
private Text txtVerteilungsSumme;
private double dVerteilungsSumme = 0;
private Label lblNewLabel;


	@Inject
	public GemeinkostenPart() 
	{
	}
	
	@PostConstruct
	public void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(1, false));
		
		grpBerichtsmonat = new Group(parent, SWT.NONE);
		grpBerichtsmonat.setLayout(new GridLayout(16, true));
		grpBerichtsmonat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpBerichtsmonat.setText("Berichtsmonat");
		
		lblKostenstelle = new Label(grpBerichtsmonat, SWT.NONE);
		lblKostenstelle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblKostenstelle.setText("Kostenstelle: ");
		
		selectEndkostenStelle = new Combo(grpBerichtsmonat, SWT.NONE);
		selectEndkostenStelle.setToolTipText("Endkostenstelle, von der auf die Kostentr\u00E4ger des Teams verteilt wird");
		selectEndkostenStelle.add(AppConstants.ENDKOSTENSTELLE_TEAM1);
		selectEndkostenStelle.add(AppConstants.ENDKOSTENSTELLE_TEAM2);
		selectEndkostenStelle.add(AppConstants.ENDKOSTENSTELLE_TEAM3);
		selectEndkostenStelle.add(AppConstants.ENDKOSTENSTELLE_TEAM4);
		selectEndkostenStelle.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			String selected = selectEndkostenStelle.getItem(selectEndkostenStelle.getSelectionIndex());
			txtLabelGemeinkosten = "Gemeinkosten ";
				switch (selected)
				{
				case AppConstants.ENDKOSTENSTELLE_TEAM1:
				txtLabelGemeinkosten = txtLabelGemeinkosten + "Endkostenstelle: " + AppConstants.ENDKOSTENSTELLE_TEAM1 + ", Gesamtprozentanteil: " + gk.getGesamtsummenProzentanteile().get(1) + " %"; 	
				break;

				case AppConstants.ENDKOSTENSTELLE_TEAM2:
				txtLabelGemeinkosten = txtLabelGemeinkosten + "Endkostenstelle: " + AppConstants.ENDKOSTENSTELLE_TEAM2 + ", Gesamtprozentanteil: " + gk.getGesamtsummenProzentanteile().get(2) + " %";	
				break;
				
				case AppConstants.ENDKOSTENSTELLE_TEAM3:
				txtLabelGemeinkosten = txtLabelGemeinkosten + "Endkostenstelle: " + AppConstants.ENDKOSTENSTELLE_TEAM3 + ", Gesamtprozentanteil: " + gk.getGesamtsummenProzentanteile().get(3) + " %";					
				break;
				
				default:
				txtLabelGemeinkosten = txtLabelGemeinkosten + "Endkostenstelle: " + AppConstants.ENDKOSTENSTELLE_TEAM4 + ", Gesamtprozentanteil: " + gk.getGesamtsummenProzentanteile().get(4) + " %";					
				break;
				}
			grpGemeinKosten.setText(txtLabelGemeinkosten);	
			txtVerteilungsSumme.setEnabled(true);
			gk.setVorkostenStelle(selected);
			}	
		});	
		
		lblVerteilungsSumme = new Label(grpBerichtsmonat, SWT.NONE);
		lblVerteilungsSumme.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVerteilungsSumme.setText("Verteilungssumme: ");
		
		txtVerteilungsSumme = new Text(grpBerichtsmonat, SWT.BORDER);
		txtVerteilungsSumme.setEnabled(false);
		txtVerteilungsSumme.setToolTipText("Ergebnis [E3] aus /Kostenkontenschemata/Kostenstellenrechnung/ERG_KST/ENDKOSTSTE f\u00FCr das zu bearbeitende Team");
		txtVerteilungsSumme.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtVerteilungsSumme.addFocusListener(new FocusListener()
		{	
			@Override
			public void focusLost(FocusEvent e)
			{
			txtVerteilungsSumme = (Text) e.widget;
				try
				{
				dVerteilungsSumme = Double.parseDouble(txtVerteilungsSumme.getText().replace(",", "."));
				gk.setVerteilungsSumme(dVerteilungsSumme);
				gkViever.setInput(gk.getAufteilungGemeinKosten().values().toArray());
				}
				catch (NumberFormatException e2)
				{
				log.error("Bitte eine gültige Zahl im Feld Verteilungssumme eingeben !", this.getClass().getName(), e2);
				txtVerteilungsSumme.setFocus();
				}
				
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		lblNewLabel = new Label(grpBerichtsmonat, SWT.NONE);
		lblNewLabel.setText("\u20AC");
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);
		new Label(grpBerichtsmonat, SWT.NONE);

		
		grpGemeinKosten = new Group(parent, SWT.NONE);
		grpGemeinKosten.setLayout(new GridLayout(1, true));
		grpGemeinKosten.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpGemeinKosten.setText(txtLabelGemeinkosten);
		
		gkViever = new TableViewer(grpGemeinKosten, SWT.BORDER | SWT.FULL_SELECTION);
		gkTable = gkViever.getTable();
		gkTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		gkTable.setHeaderVisible(true);
		
		column = new TableViewerColumn(gkViever, SWT.RIGHT, 0);
		column.getColumn().setText(GemeinKosten.COLUMN1_KTR);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(gkViever, SWT.LEFT, 1);
		column.getColumn().setText(GemeinKosten.COLUMN2_KTR_BEZEICHNUNG);
		column.getColumn().setWidth(300);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(gkViever, SWT.RIGHT, 2);
		column.getColumn().setText(GemeinKosten.COLUMN3_PROZENTANTEIL);
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		
		column = new TableViewerColumn(gkViever, SWT.RIGHT, 3);
		column.getColumn().setText(GemeinKosten.COLUMN4_VERTEILUNG);
		column.getColumn().setWidth(150);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
				
		gkViever.setContentProvider(new ArrayContentProvider());
			if (gk != null)
			{
			gkViever.setLabelProvider(gk);		
			gkViever.setInput(gk.getAufteilungGemeinKosten().values().toArray());	
			grpBerichtsmonat.setText(fMonatJahr.format(gk.getBerichtsMonat()));
			}
	}	
	@Focus
	public void onFocus() 
	{
		//TODO Your code here
	}
}