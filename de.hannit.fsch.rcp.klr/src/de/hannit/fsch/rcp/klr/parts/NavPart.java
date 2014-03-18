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
import java.util.ArrayList;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.Event;

import de.hannit.fsch.common.AppConstants;
import de.hannit.fsch.common.ContextLogger;
import de.hannit.fsch.common.Datumsformate;
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
import de.hannit.fsch.rcp.klr.loga.LoGaDatei;
import de.hannit.fsch.rcp.klr.provider.NavTreeContentProvider;
import de.hannit.fsch.rcp.klr.provider.TeamTreeContentProvider;

@SuppressWarnings("restriction")
public class NavPart 
{
@Inject IEventBroker broker;
@Inject DataService dataService;
@Inject @Optional Organisation hannit;
@Inject private EPartService partService;
@Inject private EMenuService menuService;
@Inject @Named(AppConstants.LOGGER) private ContextLogger log;
@Inject @Optional private MApplication application;

private Tarifgruppen tarifgruppen = null;
private MonatsSummen mSumme = null;
private IEclipseContext context;

private TreeViewer tvPNR = null;
private TreeViewer tvNachname = null;
private TreeViewer tvTeams = null;
private Button btnForward = null;
private Button btnBack = null;
private Combo comboMonth = null;
private Combo comboYear = null; 

private static final String POPUPMENUD_ID = "de.hannit.fsch.rcp.klr.menu.main.users";

/**
 * Wieviel Vollzeitanteile wurden aus den Tarifgruppen verteilt ?
 */
private double vzaeVerteilt = 0;
/**
 * Wie hoch ist die Summe der in den Mitarbeiterdaten gespeicherten Bruttoaufwendungen ?
 */
private double vzaeTotal = 0;	


	@Inject @Optional
	public void handleEvent(@UIEventTopic(Topics.LOGA_DATEN) LoGaDatei logaDatei)
	{
		if (logaDatei != null && logaDatei.isSaved())
		{
		MPart navPart = partService.findPart(AppConstants.PartIDs.NAVPART);
		partService.activate(navPart);
		// DatumsCombos müssen neu geladen werden:
		updateCombos();
		refresh();	
		}
	}
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
	} 	
	
	public void refresh()
	{
	Date selected = parseCombos(comboMonth.getItem(comboMonth.getSelectionIndex()), comboYear.getItem(comboYear.getSelectionIndex()));	
		
	loadData(selected);
		// TODO Monatsbericht ist überflüssig !
	broker.send(AppConstants.ActiveSelections.AUSWERTUNGSMONAT, selected);
	Monatsbericht selectedReport = hannit.getMonatsBerichte().get(selected);
	broker.send(AppConstants.ActiveSelections.MONATSBERICHT, selectedReport);
		if (comboMonth.getSelectionIndex() == (comboMonth.getItemCount() - 1))
		{
		btnForward.setEnabled(false);	
		}
		else 
		{
		btnForward.setEnabled(true);	
		}
		if (comboMonth.getSelectionIndex() == 0)
		{
		btnBack.setEnabled(false);	
		}
		else 
		{
		btnBack.setEnabled(true);	
		}		
	}

	/*
	 * Lädt Daten aus der DB zur Weiterverwendung im CSVDetailspart.
	 * Wird initial einmal und bei jeder Änderung der MonatsCombo aufgerufen.
	 */
	public void loadData(Date selectedMonth)
	{
	String plugin = this.getClass().getName() + ".loadData()";
	
	log.info("Fordere Mitarbeiterliste und AZV-Daten für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " vom DataService an.", plugin);
	hannit.setMitarbeiter(dataService.getAZVMonat(selectedMonth));
	log.confirm("Mitarbeiterliste enthält " + hannit.getMitarbeiterNachPNR().size() + " Mitarbeiter", plugin);
	
	/*
	 * Prüfung, ob alle AZV-Anteile des Mitarbeiters zusammengezählt 100% ergeben 
	 */
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
			if (m.getAzvProzentSumme() != 100)
			{
			log.error("AZV-Meldungen für Mitarbeiter: " + m.getNachname() + ", " + m.getVorname() + " sind ungleich 100% !", plugin, null);	
			}
		}

	log.info("Fordere Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " vom DataService an.", plugin);	
	tarifgruppen = dataService.getTarifgruppen(selectedMonth);
	tarifgruppen.setAnzahlMitarbeiter(hannit.getMitarbeiterNachPNR().size());
	tarifgruppen.setBerichtsMonat(selectedMonth);
	log.confirm("Es wurden "+ tarifgruppen.getTarifGruppen().size() + " Tarifgruppen geladen.", plugin);
	
	// Speichert die Tarifgruppen zur initialen Verwendung im Applikationscontext ab:
		if (application != null)
		{
		context = application.getContext();
		context.set(AppConstants.CONTEXT_TARIFGRUPPEN, tarifgruppen);
		log.info("Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", wurden im Applikationskontext gespeichert.", plugin);
		}
	
	log.info("Eventbroker versendet Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", Topic: Topics.TARIFGRUPPEN", plugin);
	broker.send(Topics.TARIFGRUPPEN, tarifgruppen);
	
	/*
	 * Nachdem die Tarifgruppen geladen wurden, wird für jeden Mitarbeiter
	 * das passende Vollzeitäquivalent gespeichert:
	 */
	vzaeVerteilt = 0;
		Tarifgruppe t = null;
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
		t = tarifgruppen.getTarifGruppen().get(m.getTarifGruppe());	
		vzaeVerteilt += m.setVollzeitAequivalent(t.getVollzeitAequivalent());
		}
	log.info("Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeVerteilt) + " Vollzeitäquivalente auf " + hannit.getMitarbeiterNachPNR().size() + " Mitarbeiter verteilt.", plugin);	
		
	/*
	 * Im Log wird nun zu Prüfzwecken ausgegeben, wie hoch das Vollzeitäquivalent Insgesamt beträgt:	
	 */
	vzaeTotal = 0;	
		for (Mitarbeiter m : hannit.getMitarbeiterNachPNR().values())
		{
			for (String a : m.getAzvMonat().keySet())
			{
			vzaeTotal += m.getAzvMonat().get(a).getBruttoAufwand();	
			}
		}
		if (NumberFormat.getCurrencyInstance().format(vzaeTotal).equals(NumberFormat.getCurrencyInstance().format(vzaeVerteilt)))
		{
		log.confirm("Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeitäquivalente entsprechend der Arbeitszeitanteile verteilt.", plugin);	
		}
		else
		{
		log.error("Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(vzaeTotal) + " Vollzeitäquivalente entsprechend der Arbeitszeitanteile verteilt.", plugin, null);
		}	
		
		/*
		 * Nun steht das Vollzeitäquivalent für jeden Mitarbeiter fest.
		 * Die Mitarbeiter werden erneut durchlaufen und es werden die Monatssummen für alle
		 * gemeldeten Kostenstellen / Kostenträger gebildet	
		 */
		mSumme = new MonatsSummen();
		mSumme.setGesamtSummen(hannit.getMitarbeiterNachPNR());
		mSumme.setBerichtsMonat(selectedMonth);
		
		PersonalDurchschnittsKosten pdk = new PersonalDurchschnittsKosten(selectedMonth);
		pdk.setMitarbeiter(hannit.getMitarbeiterNachPNR());
		
		GemeinKosten gk = new GemeinKosten(selectedMonth);
		gk.setMitarbeiter(hannit.getMitarbeiterNachPNR());
				
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
		log.confirm("Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostenträger verteilt.", plugin);
		}
		else
		{
		mSumme.setChecked(true);
		mSumme.setSummeOK(false);
		pdk.setChecked(true);
		pdk.setDatenOK(false);
		gk.setChecked(true);
		gk.setDatenOK(false);
		log.error("Für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + " wurden insgesamt " + NumberFormat.getCurrencyInstance().format(monatssummenTotal) + " auf " + mSumme.getGesamtKosten().size() + " Kostenstellen / Kostenträger verteilt.", plugin, null);
		}	
		
	/*
	 * Nach Abschluss aller Prüfungen werden die Monatssummen versendet:	
	 */
	log.info("Eventbroker versendet Monatssummen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", Topic: Topics.MONATSSUMMEN", plugin);
	broker.send(Topics.MONATSSUMMEN, mSumme);
		// Speichert die Monatssummen zur initialen Verwendung im CSVDetailsPart im Applikationscontext ab:
		if (application != null)
		{
		context = application.getContext();
		context.set(AppConstants.CONTEXT_MONATSSUMMEN, mSumme);
		log.info("Tarifgruppen für den Monat " + Datumsformate.MONATLANG_JAHR.format(selectedMonth) + ", wurden im Applikationskontext gespeichert.", plugin);
		}
	broker.send(Topics.PERSONALDURCHSCHNITTSKOSTEN, pdk);
	broker.send(Topics.GEMEINKOSTERKOSTEN, gk);
	
	tvPNR.setInput(hannit.getMitarbeiterNachPNR());
	tvNachname.setInput(hannit.getMitarbeiterNachName());
	}		

	private void updateCombos()
	{
	hannit = dataService.getOrganisation();
	ArrayList<String> availableMonth = new ArrayList<String>();
	ArrayList<String> availableYears = new ArrayList<String>();
		for (Date date : hannit.getMonatsBerichte().keySet())
		{
			if (! availableMonth.contains(Datumsformate.MONATLANG.format(date)))
			{
			availableMonth.add(Datumsformate.MONATLANG.format(date));
			}
			if (! availableYears.contains(Datumsformate.JAHR.format(date)))
			{
			availableYears.add(Datumsformate.JAHR.format(date));
			}			
		}
		for (String strMonth : availableMonth)
		{
		comboMonth.add(strMonth);	
		}
		for (String strYear : availableYears)
		{
		comboYear.add(strYear);	
		}			
	comboMonth.setText(availableMonth.get(availableMonth.size()-1));
	comboYear.setText(availableYears.get(availableYears.size()-1));
	}
	
	private Date parseCombos(String strMonth, String strYear)
	{
	Date selectedMonth = null;	
	
		try
		{
		selectedMonth = Datumsformate.MONATLANG_PUNKT_JAHR.parse(strMonth  +  "." + strYear);
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
		parent.setLayout(new GridLayout(1, false));
		
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout gl_top = new GridLayout(18, false);
		gl_top.verticalSpacing = 0;
		gl_top.marginWidth = 0;
		gl_top.marginHeight = 0;
		top.setLayout(gl_top);
		
		btnBack = new Button(top, SWT.FLAT | SWT.ARROW | SWT.LEFT);
		btnBack.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnBack.setToolTipText("Zum Vormonat wechseln");
		btnBack.setText("<<");
		btnBack.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseDown(MouseEvent e)
			{
				if (comboMonth != null && comboMonth.getSelectionIndex() != 0)
				{
				comboMonth.select((comboMonth.getSelectionIndex() - 1));
				refresh();
				}
			}
			
		});
		new Label(top, SWT.NONE);
		
		comboMonth = new Combo(top, SWT.READ_ONLY);
		comboMonth.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		comboMonth.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
			refresh();	
			}	
		});	
			
		new Label(top, SWT.NONE);
		
		btnForward = new Button(top, SWT.FLAT | SWT.ARROW | SWT.RIGHT);
		btnForward.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnForward.setToolTipText("Zum n\u00E4chsten Monat wechseln. (Nicht verf\u00FCgbar, wenn der aktuelle Monat gleich dem letzten Monat ist)");
			if (comboMonth.getSelectionIndex() == (comboMonth.getItemCount() - 1))
			{
			btnForward.setEnabled(false);	
			}
		btnForward.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mouseDown(MouseEvent e)
				{
					if (comboMonth != null && (comboMonth.getSelectionIndex() != (comboMonth.getItemCount() - 1)))
					{
					comboMonth.select((comboMonth.getSelectionIndex() + 1));
					refresh();
					}
				}
				
			});			
		
		comboYear = new Combo(top, SWT.READ_ONLY);
		comboYear.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		comboYear.setToolTipText("Liste der Verf\u00FCgbaren Berichtsjahre");
		updateCombos();
		
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);
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
		tbtmAktuell.setToolTipText("Alle Mitarbeiter, die f\u00FCr den ausgew\u00E4hlten Monat Gehalt bezogen haben, sortiert nach Personalnummer");
		tbtmAktuell.setText("PNr");
		
		TabItem tbtmNachname = new TabItem(tabs, SWT.NONE);
		tbtmNachname.setToolTipText("Alle Mitarbeiter, die f\u00FCr den ausgew\u00E4hlten Monat Gehalt bezogen haben, sortiert nach Nachname");
		tbtmNachname.setText("Nachname");

		TabItem tbtmTeams = new TabItem(tabs, SWT.NONE);
		tbtmTeams.setToolTipText("Alle Mitarbeiter, die f\u00FCr den ausgew\u00E4hlten Monat Gehalt bezogen haben, sortiert nach Teams");
		tbtmTeams.setText("Teams");
		
		tvPNR = new TreeViewer(tabs, SWT.BORDER);
		NavTreeContentProvider cp = new NavTreeContentProvider();
		tvPNR.setContentProvider(cp);
		tvPNR.setLabelProvider(cp);
		menuService.registerContextMenu(tvPNR.getTree(), POPUPMENUD_ID);
			
		Tree tree = tvPNR.getTree();
		tree.addSelectionListener(new SelectionAdapter() {
			  @Override
			  public void widgetSelected(SelectionEvent e) 
			  {
			  TreeItem item = (TreeItem) e.item;
			      if (item.getItemCount() > 0) 
			      {
			      broker.send(Topics.TREESELECTION_MITARBEITER, item);
			      }
			    }
			}); 

		tbtmAktuell.setControl(tree);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		tvNachname = new TreeViewer(tabs, SWT.BORDER);
		tvNachname.setContentProvider(cp);
		tvNachname.setLabelProvider(cp);
		menuService.registerContextMenu(tvNachname.getTree(), POPUPMENUD_ID);
			
		Tree nachnameTree = tvNachname.getTree();
		nachnameTree.addSelectionListener(new SelectionAdapter() {
			  @Override
			  public void widgetSelected(SelectionEvent e) 
			  {
			  TreeItem item = (TreeItem) e.item;
			      if (item.getItemCount() > 0) 
			      {
			      broker.send(Topics.TREESELECTION_MITARBEITER, item);
			      }
			    }
			}); 

		tbtmNachname.setControl(nachnameTree);
		nachnameTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));	
		
		tvTeams = new TreeViewer(tabs, SWT.BORDER);
		TeamTreeContentProvider tp = new TeamTreeContentProvider();
		tvTeams.setContentProvider(tp);
		tvTeams.setLabelProvider(tp);
		menuService.registerContextMenu(tvTeams.getTree(), POPUPMENUD_ID);
			
		Tree teamTree = tvTeams.getTree();
		teamTree.addSelectionListener(new SelectionAdapter() {
			  @Override
			  public void widgetSelected(SelectionEvent e) 
			  {
			  TreeItem item = (TreeItem) e.item;
			      if (item.getItemCount() > 0) 
			      {
			      broker.send(Topics.TREESELECTION_MITARBEITER, item);
			      }
			    }
			}); 

		tbtmTeams.setControl(teamTree);
		teamTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));			
		
		loadData(parseCombos(comboMonth.getItem(comboMonth.getSelectionIndex()), comboYear.getItem(comboYear.getSelectionIndex())));

		tvPNR.setInput(hannit.getMitarbeiterNachPNR());
		tvNachname.setInput(hannit.getMitarbeiterNachName());
		tvTeams.setInput(hannit.getTeams());
	
		// application.getContext().declareModifiable(AppConstants.LOG_STACK);
		//application.getContext().runAndTrack(new RunAndTrackExample(application.getContext(), logStack));
		
		MPart mpart = partService.findPart("de.hannit.fsch.rcp.klr.parts.ConsolePart");
		partService.activate(mpart);
	}

	@Focus
	public void setFocus() 
	{
	}
}
