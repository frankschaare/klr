/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.hannit.fsch.rcp.klr.parts;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.swt.modeling.EMenuService;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.osgi.service.event.Event;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.AuswertungsMonat;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.MonatsSummen;
import de.hannit.fsch.common.csv.azv.Arbeitszeitanteil;
import de.hannit.fsch.common.mitarbeiter.GemeinKosten;
import de.hannit.fsch.common.mitarbeiter.Mitarbeiter;
import de.hannit.fsch.common.mitarbeiter.PersonalDurchschnittsKosten;
import de.hannit.fsch.common.mitarbeiter.besoldung.Tarifgruppe;
import de.hannit.fsch.common.mitarbeiter.besoldung.Tarifgruppen;
import de.hannit.fsch.common.organisation.hannit.Organisation;
import de.hannit.fsch.common.organisation.reporting.Monatsbericht;
import de.hannit.fsch.klr.dataservice.DataService;
import de.hannit.fsch.rcp.klr.constants.Topics;
import de.hannit.fsch.rcp.klr.provider.NavTreeContentProvider;

public class NavPart 
{
private AuswertungsMonat auswertungsMonat = new AuswertungsMonat();
	
@Inject IEventBroker broker;
@Inject DataService dataService;
@Inject @Optional Organisation hannit;
@Inject private EPartService partService;
@Inject private EMenuService menuService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;

private TreeMap<Integer, Mitarbeiter> mitarbeiter;	
private Tarifgruppen tarifgruppen = null;
private TreeMap<String, Double> monatsSummen = null;
private MonatsSummen mSumme = null;
private TreeMap<String, Arbeitszeitanteil> azvMonat = null;

@Inject @Optional private MApplication application;
private IEclipseContext context;

private TreeViewer treeViewer = null;
private Combo comboMonth = null;
private Combo comboYear = null; 
private	SimpleDateFormat fMonat = new SimpleDateFormat("MMMM");
private	SimpleDateFormat fMonatJahr = new SimpleDateFormat("MMMM.yyyy");
private	SimpleDateFormat fLog = new SimpleDateFormat("MMMM yyyy");

private static final String POPUPMENUD_ID = "de.hannit.fsch.rcp.klr.menu.main.users";

/**
 * Wieviel Vollzeitanteile wurden aus den Tarifgruppen verteilt ?
 */
private double vzaeVerteilt = 0;
/**
 * Wie hoch ist die Summe der in den Mitarbeiterdaten gespeicherten Bruttoaufwendungen ?
 */
private double vzaeTotal = 0;	

	/*
	 * Beispiel für Registrierung an Eclipse Framework Events
	 * Bei dem Event handelt es sich um einen org.osgi.service.event.Event !
	 * 
	 */
	@Inject
	@Optional
	public void partActivation(@UIEventTopic(UIEvents.UILifeCycle.ACTIVATE) Event event) 
	{
	// TODO: org.osgi.service.event.Event sind noch nicht in den Product Dependencies	
	// Den aktiven Part ausgeben:	
	MPart activePart = (MPart) event.getProperty(UIEvents.EventTags.ELEMENT);
	MWindow main = application.getChildren().get(0);
	main.setLabel("HannIT KLR - " + dataService.getConnectionInfo());
	// Den Eclipse Context ausgeben:
	context = application.getContext();
		// Eine eigene Variable setzen:
		if (activePart != null) 
		{
		context.set("myactivePartId", activePart.getElementId());
		}
		
	System.out.println(activePart.getElementId());
	} 	

	/*
	 * Lädt Daten aus der DB zur Weiterverwendung im CSVDetailspart.
	 * Wird initial einmal und bei jeder Änderung der MonatsCombo aufgerufen.
	 */
	public void loadData(Date selectedMonth)
	{
	String plugin = this.getClass().getName() + ".loadData()";
	
	log.info("Fordere Mitarbeiterliste und AZV-Daten für den Monat " + fLog.format(selectedMonth) + " vom DataService an.", plugin);
	mitarbeiter = dataService.getAZVMonat(selectedMonth);
	log.confirm("Mitarbeiterliste enthält " + mitarbeiter.size() + " Mitarbeiter", plugin);
	
	/*
	 * Prüfung, ob alle AZV-Anteile des Mitarbeiters zusammengezählt 100% ergeben 
	 */
		for (Mitarbeiter m : mitarbeiter.values())
		{
			if (m.getAzvProzentSumme() != 100)
			{
			log.error("AZV-Meldungen für Mitarbeiter: " + m.getNachname() + ", " + m.getVorname() + " sind ungleich 100% !", plugin, null);	
			}
		}

	log.info("Fordere Tarifgruppen für den Monat " + fLog.format(selectedMonth) + " vom DataService an.", plugin);	
	tarifgruppen = dataService.getTarifgruppen(selectedMonth);
	tarifgruppen.setAnzahlMitarbeiter(mitarbeiter.size());
	tarifgruppen.setBerichtsMonat(selectedMonth);
	log.confirm("Es wurden "+ tarifgruppen.getTarifGruppen().size() + " Tarifgruppen geladen.", plugin);
	
	// Speichert die Tarifgruppen zur initialen Verwendung im Applikationscontext ab:
		if (application != null)
		{
		context = application.getContext();
		context.set(AppConstants.CONTEXT_TARIFGRUPPEN, tarifgruppen);
		log.info("Tarifgruppen für den Monat " + fLog.format(selectedMonth) + ", wurden im Applikationskontext gespeichert.", plugin);
		}
	
	log.info("Eventbroker versendet Tarifgruppen für den Monat " + fLog.format(selectedMonth) + ", Topic: Topics.TARIFGRUPPEN", plugin);
	broker.send(Topics.TARIFGRUPPEN, tarifgruppen);
	
	/*
	 * Nachdem die Tarifgruppen geladen wurden, wird für jeden Mitarbeiter
	 * das passende Vollzeitäquivalent gespeichert:
	 */
	vzaeVerteilt = 0;
		Tarifgruppe t = null;
		for (Mitarbeiter m : mitarbeiter.values())
		{
		t = tarifgruppen.getTarifGruppen().get(m.getTarifGruppe());	
		vzaeVerteilt += m.setVollzeitAequivalent(t.getVollzeitAequivalent());
		}
	log.info("Für den Monat " + fLog.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeVerteilt) + " Vollzeitäquivalente auf " + mitarbeiter.size() + " Mitarbeiter verteilt.", plugin);	
		
	/*
	 * Im Log wird nun zu Prüfzwecken ausgegeben, wie hoch das Vollzeitäquivalent Insgesamt beträgt:	
	 */
	vzaeTotal = 0;	
		for (Mitarbeiter m : mitarbeiter.values())
		{
			for (String a : m.getAzvMonat().keySet())
			{
			vzaeTotal += m.getAzvMonat().get(a).getBruttoAufwand();	
			}
		}
		if (NumberFormat.getCurrencyInstance().format(vzaeTotal).equals(NumberFormat.getCurrencyInstance().format(vzaeVerteilt)))
		{
		log.confirm("Für den Monat " + fLog.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeitäquivalente entsprechend der Arbeitszeitanteile verteilt.", plugin);	
		}
		else
		{
		log.error("Für den Monat " + fLog.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeitäquivalente entsprechend der Arbeitszeitanteile verteilt.", plugin, null);
		}	
		
	/*
	 * Nun steht das Vollzeitäquivalent für jeden Mitarbeiter fest.
	 * Die Mitarbeiter werden erneut durchlaufen und es werden die Monatssummen für alle
	 * gemeldeten Kostenstellen / Kostenträger gebildet	
	 */
	monatsSummen = new TreeMap<String, Double>();	
		for (Mitarbeiter m : mitarbeiter.values())
		{
		azvMonat = m.getAzvMonat();
			for (String strKSTKTR : azvMonat.keySet())
			{
				/*
				 * Ist die Kostenstelle / Kostenträger bereits in den Monatssummen gespeichert ?
				 * Wenn Ja, wird der Bruttoaufwand addiert,
				 * Wenn Nein, wird die Kostenstelle / Kostenträger neu eingefügt:
				 */
				try
				{
				double monatssumme = monatsSummen.get(strKSTKTR);
				// System.out.println("Addiere: " + strKSTKTR + ": " + azvMonat.get(strKSTKTR).getBruttoAufwand());
				monatsSummen.put(strKSTKTR, (monatssumme + azvMonat.get(strKSTKTR).getBruttoAufwand()));
				}
				catch (NullPointerException e)
				{
				monatsSummen.put(strKSTKTR, azvMonat.get(strKSTKTR).getBruttoAufwand());	
				// System.out.println("Neu: " + strKSTKTR + ": " + azvMonat.get(strKSTKTR).getBruttoAufwand());	
				}	
			}
		}
		mSumme = new MonatsSummen();
		mSumme.setGesamtSummen(monatsSummen);
		mSumme.setBerichtsMonat(selectedMonth);
		
		PersonalDurchschnittsKosten pdk = new PersonalDurchschnittsKosten(selectedMonth);
		pdk.setMitarbeiter(mitarbeiter);
		
		GemeinKosten gk = new GemeinKosten(selectedMonth);
		gk.setMitarbeiter(mitarbeiter);
				
		/*
		 * Nachdem alle Kostenstellen / Kostenträger verteilt sind, wird die Gesamtsumme gebildet und im Log ausgegeben.
		 * Diese MUSS gleich dem Gesamtbruttoaufwand sein !
		 */
		double monatssummenTotal = mSumme.getKstktrMonatssumme();
		
		if (NumberFormat.getCurrencyInstance().format(monatssummenTotal).equals(NumberFormat.getCurrencyInstance().format(vzaeVerteilt)))
		{
		mSumme.setChecked(true);
		mSumme.setSummeOK(true);
		pdk.setChecked(true);
		pdk.setDatenOK(true);
		gk.setChecked(true);
		gk.setDatenOK(true);
		log.confirm("Für den Monat " + fLog.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + monatsSummen.size() + " Kostenstellen / Kostenträger verteilt.", plugin);
		}
		else
		{
		mSumme.setChecked(true);
		mSumme.setSummeOK(false);
		pdk.setChecked(true);
		pdk.setDatenOK(false);
		gk.setChecked(true);
		gk.setDatenOK(false);
		log.error("Für den Monat " + fLog.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + monatsSummen.size() + " Kostenstellen / Kostenträger verteilt.", plugin, null);
		}	
		
	/*
	 * Nach Abschluss aller Prüfungen werden die Monatssummen versendet:	
	 */
	log.info("Eventbroker versendet Monatssummen für den Monat " + fLog.format(selectedMonth) + ", Topic: Topics.MONATSSUMMEN", plugin);
	broker.send(Topics.MONATSSUMMEN, mSumme);
	broker.send(Topics.PERSONALDURCHSCHNITTSKOSTEN, pdk);
	broker.send(Topics.GEMEINKOSTERKOSTEN, gk);
	
	treeViewer.setInput(mitarbeiter);
	}		

	private Date parseCombos(String strMonth, String strYear)
	{
	Date selectedMonth = null;	
	
		try
		{
		selectedMonth = fMonatJahr.parse(strMonth  +  "." + strYear);
		}
		catch (ParseException ex)
		{
		ex.printStackTrace();
		log.error("ParseException beim Auslesen von NavPart.comboMonth !", this.getClass().getName() + ".parseCombos()", ex);
		}	
		
	return selectedMonth;
	}
	
	@PostConstruct
	public void createComposite(Composite parent) 
	{
	String method = this.getClass().getName() + ".createComposite()";
	hannit = dataService.getOrganisation();
	
		parent.setLayout(new GridLayout(1, false));
		
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout gl_top = new GridLayout(6, false);
		gl_top.verticalSpacing = 0;
		gl_top.marginWidth = 0;
		gl_top.marginHeight = 0;
		top.setLayout(gl_top);
		top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBack = new Button(top, SWT.FLAT | SWT.ARROW | SWT.LEFT);
		btnBack.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnBack.setToolTipText("Zum Vormonat wechseln");
		btnBack.setText("<<");
		new Label(top, SWT.NONE);
		
		comboMonth = new Combo(top, SWT.READ_ONLY);
		comboMonth.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
			for (Date date : hannit.getMonatsBerichte().keySet())
			{
			comboMonth.add(fMonat.format(date));
			comboMonth.setText(comboMonth.getItem(comboMonth.getItemCount()-1));
			}
		comboMonth.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			Date selected = parseCombos(comboMonth.getItem(comboMonth.getSelectionIndex()), comboYear.getItem(comboYear.getSelectionIndex()));	
	
			loadData(selected);
			// TODO Monatsbericht ist überflüssig !
			broker.send(AppConstants.ActiveSelections.AUSWERTUNGSMONAT, selected);
			Monatsbericht selectedReport = hannit.getMonatsBerichte().get(selected);
			broker.send(AppConstants.ActiveSelections.MONATSBERICHT, selectedReport);
			}	
		});	
			
		new Label(top, SWT.NONE);
		
		Button btnForward = new Button(top, SWT.FLAT | SWT.ARROW | SWT.RIGHT);
		btnForward.setToolTipText("Zum n\u00E4chsten Monat wechseln. (Nicht verf\u00FCgbar, wenn der aktuelle Monat gleich dem letzten Monat ist)");
		btnForward.setEnabled(auswertungsMonat.lastMonth());;
		
		comboYear = new Combo(top, SWT.READ_ONLY);
		comboYear.setToolTipText("Liste der Verf\u00FCgbaren Berichtsjahre");
		comboYear.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboYear.add("2013");
		comboYear.setText("2013");
		// comboYear.add(auswertungsMonat.getActualYear());
		// comboYear.setText(auswertungsMonat.getActualYear());
		
		Composite bottom = new Composite(parent, SWT.NONE);
		GridLayout gl_bottom = new GridLayout(1, false);
		gl_bottom.verticalSpacing = 0;
		gl_bottom.marginWidth = 0;
		gl_bottom.marginHeight = 0;
		bottom.setLayout(gl_bottom);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabFolder tabs = new TabFolder(bottom, SWT.NONE);
		tabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmAktuell = new TabItem(tabs, SWT.NONE);
		tbtmAktuell.setToolTipText("Alle Mitarbeiter, die f\u00FCr den ausgew\u00E4hlten Monat Gehalt bezogen haben");
		tbtmAktuell.setText("Aktuell");
		
		TabItem tabItem_1 = new TabItem(tabs, SWT.NONE);
		tabItem_1.setText("New Item");
		
		treeViewer = new TreeViewer(tabs, SWT.BORDER);
		NavTreeContentProvider cp = new NavTreeContentProvider();
		treeViewer.setContentProvider(cp);
		treeViewer.setLabelProvider(cp);
		menuService.registerContextMenu(treeViewer.getTree(), POPUPMENUD_ID);
			
		Tree tree = treeViewer.getTree();

		tbtmAktuell.setControl(tree);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	
		loadData(parseCombos(comboMonth.getItem(comboMonth.getSelectionIndex()), comboYear.getItem(comboYear.getSelectionIndex())));

		treeViewer.setInput(mitarbeiter);
	
		// application.getContext().declareModifiable(AppConstants.LOG_STACK);
		//application.getContext().runAndTrack(new RunAndTrackExample(application.getContext(), logStack));
		
		MPart mpart = partService.findPart("de.hannit.fsch.rcp.klr.parts.ConsolePart");
		partService.activate(mpart);
	}

	@Focus
	public void setFocus() 
	{
		// tableViewer.getTable().setFocus();
	}
}
